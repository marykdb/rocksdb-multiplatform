@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_mergeoperator_t
import kotlinx.cinterop.*
import maryk.asSizeT
import maryk.sizeTToInt
import platform.posix.memcpy
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.concurrent.AtomicReference
import kotlin.experimental.ExperimentalNativeApi

private val fallbackMergeOperatorName: CPointer<ByteVar> = run {
    val nameBytes = "KotlinMergeOperator\u0000".encodeToByteArray()
    val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)
    nameBytes.usePinned { pinned ->
        memcpy(mem, pinned.addressOf(0), nameBytes.size.asSizeT())
    }
    mem
}

/**
 * Base (actual) merge operator implementation.
 *
 * This implementation now uses a persistent pinned name similar to AbstractComparator.
 */
actual abstract class MergeOperator : RocksObject() {
    // Allocate and persist the operator's name on the native heap.
    private val pinnedName = AtomicReference<CPointer<ByteVar>?>(null)

    internal fun pinnedName(): CPointer<ByteVar> {
        pinnedName.value?.let { return it }
        val actualName = try {
            name()
        } catch (_: Throwable) {
            "KotlinMergeOperator"
        }
        // Append a null terminator.
        val nameBytes = (actualName + "\u0000").encodeToByteArray()
        val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)
        nameBytes.usePinned { pinned ->
            memcpy(mem, pinned.addressOf(0), nameBytes.size.asSizeT())
        }
        return if (pinnedName.compareAndSet(null, mem)) {
            mem
        } else {
            nativeHeap.free(mem.rawValue)
            requireNotNull(pinnedName.value) { "Merge operator name was cleared while being initialized." }
        }
    }

    val native: CPointer<rocksdb_mergeoperator_t>

    init {
        // Create a stable reference to this instance so that C callbacks can find it.
        val stableRef = StableRef.create(this)
        try {
            native = rocksdb.rocksdb_mergeoperator_create(
                state = stableRef.asCPointer(),
                destructor = staticCFunction(::mergeOperatorDestructor),
                full_merge = staticCFunction(::fullMergeCallback),
                partial_merge = staticCFunction(::partialMergeCallback),
                delete_value = staticCFunction(::deleteValueCallback),
                name = staticCFunction(::mergeOperatorNameCallback)
            ) ?: error("Failed to create merge operator")
        } catch (throwable: Throwable) {
            stableRef.dispose()
            throw throwable
        }
    }

    // Override close() to free our pinned name and destroy the native object.
    override fun close() {
        if (tryClose()) {
            // Destroy the native merge operator. Adjust this function call if needed.
            rocksdb.rocksdb_mergeoperator_destroy(native)
            super.close()
        }
    }

    internal fun transferOwnershipToNative() {
        check(disownHandle()) { "MergeOperator is already closed or registered." }
    }

    internal fun destroyFromNative() {
        pinnedName.value?.let {
            if (pinnedName.compareAndSet(it, null)) {
                nativeHeap.free(it.rawValue)
            }
        }
        tryCloseTransferred()
    }

    /**
     * Full merge: by default returns a copy of the last operand (or the existing value).
     */
    open fun fullMerge(
        key: CPointer<ByteVarOf<Byte>>?, keyLen: size_t,
        existingValue: CPointer<ByteVarOf<Byte>>?, existingValueLen: size_t,
        operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
        operandsLengths: CPointer<size_tVar>?, numOperands: Int
    ): Pair<Boolean, Pair<CPointer<ByteVarOf<Byte>>?, size_t>> {
        if (numOperands > 0 && operands != null && operandsLengths != null) {
            val lastIndex = numOperands - 1
            val operandPtr = (operands + lastIndex)!!.pointed.value
                ?: return Pair(false, Pair(null, 0.asSizeT()))
            val operandLen = (operandsLengths + lastIndex)!!.pointed.value
            return Pair(true, Pair(copyMergeResult(operandPtr, operandLen, "merge operand"), operandLen))
        } else if (existingValue != null) {
            return Pair(true, Pair(copyMergeResult(existingValue, existingValueLen, "merge existing value"), existingValueLen))
        } else {
            return Pair(false, Pair(null, 0.asSizeT()))
        }
    }

    /**
     * Partial merge: by default returns a copy of the last operand.
     */
    open fun partialMerge(
        key: CPointer<ByteVarOf<Byte>>?, keyLen: size_t,
        operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
        operandsLengths: CPointer<size_tVar>?, numOperands: Int
    ): Pair<Boolean, Pair<CPointer<ByteVarOf<Byte>>?, size_t>> {
        if (numOperands > 0 && operands != null && operandsLengths != null) {
            val lastIndex = numOperands - 1
            val operandPtr = (operands + lastIndex)!!.pointed.value
                ?: return Pair(false, Pair(null, 0.asSizeT()))
            val operandLen = (operandsLengths + lastIndex)!!.pointed.value
            return Pair(true, Pair(copyMergeResult(operandPtr, operandLen, "merge operand"), operandLen))
        } else {
            return Pair(false, Pair(null, 0.asSizeT()))
        }
    }

    /**
     * Returns the name of the merge operator.
     */
    open fun name(): String = "DefaultMergeOperator"

    /**
     * Called when the merge operator is being destroyed.
     * Default implementation does nothing.
     */
    open fun destructor() {
        // No instance state to clean up.
    }

    /**
     * Called to delete a previously-merged value.
     * Default implementation frees the native heap allocation.
     */
    open fun deleteValue(value: CPointer<ByteVarOf<Byte>>?, valueLen: size_t) {
        if (value != null) {
            nativeHeap.free(value)
        }
    }
}

// --- C Callback Implementations ---

private fun mergeOperatorDestructor(state: CPointer<out CPointed>?) {
    try {
        state?.asStableRef<MergeOperator>()?.let { stableRef ->
            try {
                stableRef.get().run {
                    try {
                        destructor()
                    } catch (_: Throwable) {
                    } finally {
                        destroyFromNative()
                    }
                }
            } finally {
                stableRef.dispose()
            }
        }
    } catch (_: Throwable) {
    }
}

private fun fullMergeCallback(
    state: CPointer<out CPointed>?,
    key: CPointer<ByteVarOf<Byte>>?, keyLen: size_t,
    existingValue: CPointer<ByteVarOf<Byte>>?, existingValueLen: size_t,
    operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
    operandsLengths: CPointer<size_tVar>?, numOperands: Int,
    success: CPointer<UByteVarOf<UByte>>?, newValueLength: CPointer<size_tVar>?
): CPointer<ByteVarOf<Byte>>? {
    try {
        val instance = state?.asStableRef<MergeOperator>()?.get()
            ?: return failedMergeValue(success, newValueLength)
        val (succeeded, resultPair) = instance.fullMerge(
            key,
            keyLen,
            existingValue,
            existingValueLen,
            operands,
            operandsLengths,
            numOperands
        )
        val value = resultPair.first
        val valueLength = resultPair.second
        if (!succeeded || value == null) {
            if (value != null) {
                try {
                    instance.deleteValue(value, valueLength)
                } catch (_: Throwable) {
                }
            }
            return failedMergeValue(success, newValueLength)
        }
        success?.pointed?.value = 1u
        newValueLength?.pointed?.value = valueLength
        return value
    } catch (_: Throwable) {
        return failedMergeValue(success, newValueLength)
    }
}

private fun partialMergeCallback(
    state: CPointer<out CPointed>?,
    key: CPointer<ByteVarOf<Byte>>?, keyLen: size_t,
    operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
    operandsLengths: CPointer<size_tVar>?, numOperands: Int,
    success: CPointer<UByteVarOf<UByte>>?, newValueLength: CPointer<size_tVar>?
): CPointer<ByteVarOf<Byte>>? {
    try {
        val instance = state?.asStableRef<MergeOperator>()?.get()
            ?: return failedMergeValue(success, newValueLength)
        val (succeeded, resultPair) = instance.partialMerge(
            key,
            keyLen,
            operands,
            operandsLengths,
            numOperands
        )
        val value = resultPair.first
        val valueLength = resultPair.second
        if (!succeeded || value == null) {
            if (value != null) {
                try {
                    instance.deleteValue(value, valueLength)
                } catch (_: Throwable) {
                }
            }
            return failedMergeValue(success, newValueLength)
        }
        success?.pointed?.value = 1u
        newValueLength?.pointed?.value = valueLength
        return value
    } catch (_: Throwable) {
        return failedMergeValue(success, newValueLength)
    }
}

private fun failedMergeValue(
    success: CPointer<UByteVarOf<UByte>>?,
    newValueLength: CPointer<size_tVar>?,
): CPointer<ByteVarOf<Byte>> {
    success?.pointed?.value = 0u
    newValueLength?.pointed?.value = 0.asSizeT()
    return nativeHeap.allocArray<ByteVar>(1)
}

internal fun copyMergeResult(
    source: CPointer<ByteVarOf<Byte>>,
    length: size_t,
    label: String,
): CPointer<ByteVar> {
    val lengthInt = sizeTToInt(length, label)
    val result = nativeHeap.allocArray<ByteVar>(if (lengthInt == 0) 1 else lengthInt)
    if (lengthInt > 0) {
        memcpy(result, source, length)
    }
    return result
}

internal fun ByteArray.copyToMergeResult(): CPointer<ByteVar> {
    val result = nativeHeap.allocArray<ByteVar>(if (isEmpty()) 1 else size)
    if (isNotEmpty()) {
        usePinned { pinned ->
            memcpy(result, pinned.addressOf(0), size.asSizeT())
        }
    }
    return result
}

// Updated callback: now simply returns the persistent pinnedName.
private fun mergeOperatorNameCallback(state: CPointer<out CPointed>?): CPointer<ByteVarOf<Byte>>? {
    return try {
        val instance = state?.asStableRef<MergeOperator>()?.get()
        instance?.pinnedName() ?: fallbackMergeOperatorName
    } catch (_: Throwable) {
        fallbackMergeOperatorName
    }
}

private fun deleteValueCallback(
    state: CPointer<out CPointed>?,
    value: CPointer<ByteVarOf<Byte>>?,
    valueLen: size_t
) {
    try {
        state?.asStableRef<MergeOperator>()?.get()?.deleteValue(value, valueLen)
    } catch (_: Throwable) {
    }
}
