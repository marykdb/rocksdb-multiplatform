@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_comparator_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.usePinned
import maryk.ByteBuffer
import maryk.DirectByteBuffer
import maryk.asSizeT
import maryk.sizeTToInt
import platform.posix.memcpy
import rocksdb.rocksdb_comparator_destroy
import rocksdb.rocksdb_comparator_get_comparator_type
import kotlin.concurrent.AtomicReference

private val fallbackComparatorName: CPointer<ByteVar> = run {
    val nameBytes = "KotlinComparator\u0000".encodeToByteArray()
    val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)
    nameBytes.usePinned { pinned ->
        memcpy(mem, pinned.addressOf(0), nameBytes.size.asSizeT())
    }
    mem
}

actual abstract class AbstractComparator
    protected actual constructor(val copt: ComparatorOptions?)
: RocksCallbackObject() {
    protected actual constructor() : this(null)

    private val pinnedName = AtomicReference<CPointer<ByteVar>?>(null)

    private fun pinnedName(): CPointer<ByteVar> {
        pinnedName.value?.let { return it }
        val actualName = try {
            name()
        } catch (_: Throwable) {
            "KotlinComparator"
        }
        val nameBytes = (actualName + "\u0000").encodeToByteArray()
        val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)

        nameBytes.usePinned { pinned ->
            memcpy(mem, pinned.addressOf(0), nameBytes.size.asSizeT())
        }
        return if (pinnedName.compareAndSet(null, mem)) {
            mem
        } else {
            nativeHeap.free(mem.rawValue)
            requireNotNull(pinnedName.value) { "Comparator name was cleared while being initialized." }
        }
    }

    private val nameCallback = staticCFunction<COpaquePointer?, CPointer<ByteVar>?> { statePtr ->
        try {
            val comparator = statePtr?.asStableRef<AbstractComparator>()?.get()
                ?: return@staticCFunction fallbackComparatorName
            comparator.pinnedName()
        } catch (_: Throwable) {
            fallbackComparatorName
        }
    }

    private var stableRef: StableRef<AbstractComparator>? = null

    private val destructorCallback = staticCFunction<COpaquePointer?, Unit> { ref ->
        try {
            ref?.asStableRef<AbstractComparator>()?.let { stableRef ->
                try {
                    stableRef.get().destroyFromNative()
                } finally {
                    stableRef.dispose()
                }
            }
        } catch (_: Throwable) {
        }
    }

    private val nativeRef = lazy {
        val ref = StableRef.create(this)
        try {
            val created = rocksdb.rocksdb_comparator_create(
                name = nameCallback,
                state = ref.asCPointer(),
                compare = staticCFunction { statePtr, aPtr, aLen, bPtr, bLen ->
                    try {
                        val comparator = statePtr?.asStableRef<AbstractComparator>()?.get()
                            ?: return@staticCFunction 0

                        if (aPtr == null || bPtr == null) {
                            return@staticCFunction 0
                        }

                        comparator.compare(
                            DirectByteBuffer(aPtr, sizeTToInt(aLen, "comparator key")),
                            DirectByteBuffer(bPtr, sizeTToInt(bLen, "comparator key")),
                        )
                    } catch (_: Throwable) {
                        0
                    }
                },
                destructor = destructorCallback,
            ) ?: error("Unable to create RocksDB comparator")
            stableRef = ref
            created
        } catch (throwable: Throwable) {
            ref.dispose()
            throw throwable
        }
    }

    // This intentionally does not check ownership: option/DB cleanup still needs
    // the initialized pointer after ownership has moved out of the user object.
    val native: CPointer<rocksdb_comparator_t>
        get() = nativeRef.value

    actual override fun close() {
        if (tryClose()) {
            destroyFromNative()
            if (nativeRef.isInitialized()) {
                rocksdb_comparator_destroy(native)
            } else {
                stableRef?.dispose()
                stableRef = null
            }
            super.close()
        }
    }

    internal fun transferOwnershipToOptions(): CPointer<rocksdb_comparator_t> {
        checkOwningHandle()
        val nativeHandle = native
        check(disownHandle()) { "Comparator is already closed or registered." }
        return nativeHandle
    }

    internal fun closeFromOptions() {
        if (tryCloseTransferred()) {
            if (nativeRef.isInitialized()) {
                rocksdb_comparator_destroy(native)
            } else {
                stableRef?.dispose()
                stableRef = null
            }
        }
    }

    internal fun destroyFromNative() {
        pinnedName.value?.let {
            if (pinnedName.compareAndSet(it, null)) {
                nativeHeap.free(it.rawValue)
            }
        }
    }

    actual abstract fun name(): String

    actual abstract fun compare(a: ByteBuffer, b: ByteBuffer): Int

    actual open fun findShortestSeparator(start: ByteBuffer, limit: ByteBuffer) {
        // no opp
    }

    actual open fun findShortSuccessor(key: ByteBuffer) {
        // no opp
    }
}

actual fun AbstractComparator.getComparatorType(): ComparatorType {
    checkOwningHandle()
    val value = rocksdb_comparator_get_comparator_type(native)
    return when (value) {
        0 -> ComparatorType.JAVA_COMPARATOR
        1 -> ComparatorType.JAVA_NATIVE_COMPARATOR_WRAPPER
        else -> ComparatorType.JAVA_COMPARATOR
    }
}
