@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_mergeoperator_t
import kotlinx.cinterop.*
import maryk.asSizeT
import platform.posix.memcpy
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.experimental.ExperimentalNativeApi

/**
 * Base (actual) merge operator implementation.
 *
 * This implementation now uses a persistent pinned name similar to AbstractComparator.
 */
actual abstract class MergeOperator : RocksObject() {
    // Allocate and persist the operator's name on the native heap.
    private var pinnedName: CPointer<ByteVar>? = null

    internal fun pinnedName(): CPointer<ByteVar> {
        pinnedName?.let { return it }
        val actualName = name()
        // Append a null terminator.
        val nameBytes = (actualName + "\u0000").encodeToByteArray()
        val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)
        nameBytes.usePinned { pinned ->
            memcpy(mem, pinned.addressOf(0), nameBytes.size.convert())
        }
        pinnedName = mem
        return mem
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
        pinnedName?.let {
            nativeHeap.free(it.rawValue)
            pinnedName = null
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
            val operandLen = (operandsLengths + lastIndex)!!.pointed.value
            val merged = nativeHeap.allocArray<ByteVar>(operandLen.toInt())
            memcpy(merged, operandPtr, operandLen)
            return Pair(true, Pair(merged, operandLen))
        } else if (existingValue != null) {
            val merged = nativeHeap.allocArray<ByteVar>(existingValueLen.toInt())
            memcpy(merged, existingValue, existingValueLen)
            return Pair(true, Pair(merged, existingValueLen))
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
            val operandLen = (operandsLengths + lastIndex)!!.pointed.value
            val merged = nativeHeap.allocArray<ByteVar>(operandLen.toInt())
            memcpy(merged, operandPtr, operandLen)
            return Pair(true, Pair(merged, operandLen))
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
    state?.asStableRef<MergeOperator>()?.let { stableRef ->
        stableRef.get().run {
            try {
                destructor()
            } catch (_: Throwable) {
            } finally {
                destroyFromNative()
            }
        }
        stableRef.dispose()
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
    val instance = state?.asStableRef<MergeOperator>()?.get() ?: return null
    val (succeeded, resultPair) = try {
        instance.fullMerge(key, keyLen, existingValue, existingValueLen, operands, operandsLengths, numOperands)
    } catch (_: Throwable) {
        success?.pointed?.value = 0u
        newValueLength?.pointed?.value = 0.asSizeT()
        return null
    }
    val value = resultPair.first
    val valueLength = resultPair.second
    if (!succeeded || value == null) {
        success?.pointed?.value = 0u
        newValueLength?.pointed?.value = 0.asSizeT()
        if (value != null) {
            try {
                instance.deleteValue(value, valueLength)
            } catch (_: Throwable) {
            }
        }
        return null
    }
    success?.pointed?.value = 1u
    newValueLength?.pointed?.value = valueLength
    return value
}

private fun partialMergeCallback(
    state: CPointer<out CPointed>?,
    key: CPointer<ByteVarOf<Byte>>?, keyLen: size_t,
    operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
    operandsLengths: CPointer<size_tVar>?, numOperands: Int,
    success: CPointer<UByteVarOf<UByte>>?, newValueLength: CPointer<size_tVar>?
): CPointer<ByteVarOf<Byte>>? {
    val instance = state?.asStableRef<MergeOperator>()?.get() ?: return null
    val (succeeded, resultPair) = try {
        instance.partialMerge(key, keyLen, operands, operandsLengths, numOperands)
    } catch (_: Throwable) {
        success?.pointed?.value = 0u
        newValueLength?.pointed?.value = 0.asSizeT()
        return null
    }
    val value = resultPair.first
    val valueLength = resultPair.second
    if (!succeeded || value == null) {
        success?.pointed?.value = 0u
        newValueLength?.pointed?.value = 0.asSizeT()
        if (value != null) {
            try {
                instance.deleteValue(value, valueLength)
            } catch (_: Throwable) {
            }
        }
        return null
    }
    success?.pointed?.value = 1u
    newValueLength?.pointed?.value = valueLength
    return value
}

// Updated callback: now simply returns the persistent pinnedName.
private fun mergeOperatorNameCallback(state: CPointer<out CPointed>?): CPointer<ByteVarOf<Byte>>? {
    val instance = state?.asStableRef<MergeOperator>()?.get()
    return try {
        instance?.pinnedName()
    } catch (_: Throwable) {
        null
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
