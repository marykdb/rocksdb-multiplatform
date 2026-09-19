@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_compactionfilter_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.UByteVarOf
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.usePinned
import maryk.asSizeT
import maryk.sizeTToInt
import platform.posix.memcpy
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.concurrent.AtomicReference

private val fallbackCompactionFilterName: CPointer<ByteVar> = run {
    val nameBytes = "KotlinCompactionFilter\u0000".encodeToByteArray()
    val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)
    nameBytes.usePinned { pinned ->
        memcpy(mem, pinned.addressOf(0), nameBytes.size.asSizeT())
    }
    mem
}

/**
 * Note that disposeInternal should be called only after all
 * RocksDB instances referencing the compaction filter are closed.
 * Otherwise an undefined behavior will occur.
 */
actual abstract class AbstractCompactionFilter<T : AbstractSlice<*>> protected constructor() : RocksObject() {
    private val pinnedName = AtomicReference<CPointer<ByteVar>?>(null)
    private var stableRef: StableRef<AbstractCompactionFilter<T>>? = null

    private val nativeRef = lazy {
        val ref = StableRef.create(this)
        try {
            val created = rocksdb.rocksdb_compactionfilter_create(
                state = ref.asCPointer(),
                destructor = staticCFunction(::compactionFilterDestructor),
                filter = staticCFunction(::compactionFilterCallback),
                name = staticCFunction(::compactionFilterNameCallback),
            ) ?: error("Unable to create RocksDB compaction filter")
            stableRef = ref
            created
        } catch (throwable: Throwable) {
            ref.dispose()
            throw throwable
        }
    }
    internal val native: CPointer<rocksdb_compactionfilter_t>
        get() = nativeRef.value

    private fun pinnedName(): CPointer<ByteVar> {
        pinnedName.value?.let { return it }
        val nameBytes = (name() + "\u0000").encodeToByteArray()
        val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)
        nameBytes.usePinned { pinned ->
            memcpy(mem, pinned.addressOf(0), nameBytes.size.asSizeT())
        }
        return if (pinnedName.compareAndSet(null, mem)) {
            mem
        } else {
            nativeHeap.free(mem.rawValue)
            requireNotNull(pinnedName.value) { "Compaction filter name was cleared while being initialized." }
        }
    }

    internal fun namePointer(): CPointer<ByteVar> = pinnedName()

    internal open fun name(): String = this::class.simpleName ?: "KotlinCompactionFilter"

    internal open fun filter(
        level: Int,
        key: CPointer<ByteVar>?,
        keyLength: size_t,
        existingValue: CPointer<ByteVar>?,
        valueLength: size_t,
        newValue: CPointer<CPointerVarOf<CPointer<ByteVar>>>?,
        newValueLength: CPointer<size_tVar>?,
        valueChanged: CPointer<UByteVarOf<UByte>>?,
    ): Boolean = false

    internal fun transferOwnershipToNative(): CPointer<rocksdb_compactionfilter_t> {
        checkOwningHandle()
        val nativeHandle = native
        check(disownHandle()) { "Compaction filter is already closed or registered." }
        return nativeHandle
    }

    internal fun closeFromNative() {
        destroyNativeState()
        tryCloseTransferred()
    }

    override fun close() {
        if (tryClose()) {
            destroyNativeState()
            if (nativeRef.isInitialized()) {
                rocksdb.rocksdb_compactionfilter_destroy(native)
            } else {
                stableRef?.dispose()
                stableRef = null
            }
            super.close()
        }
    }

    internal fun destroyNativeState() {
        pinnedName.value?.let {
            if (pinnedName.compareAndSet(it, null)) {
                nativeHeap.free(it.rawValue)
            }
        }
    }
}

actual open class AbstractCompactionFilterContext(
    private val fullCompaction: Boolean,
    private val manualCompaction: Boolean
) {
    actual fun isFullCompaction() = fullCompaction

    actual fun isManualCompaction() = manualCompaction
}

private fun compactionFilterDestructor(state: COpaquePointer?) {
    try {
        state?.asStableRef<AbstractCompactionFilter<*>>()?.let { stableRef ->
            try {
                stableRef.get().closeFromNative()
            } finally {
                stableRef.dispose()
            }
        }
    } catch (_: Throwable) {
    }
}

private fun compactionFilterCallback(
    state: COpaquePointer?,
    level: Int,
    key: CPointer<ByteVar>?,
    keyLength: size_t,
    existingValue: CPointer<ByteVar>?,
    valueLength: size_t,
    newValue: CPointer<CPointerVarOf<CPointer<ByteVar>>>?,
    newValueLength: CPointer<size_tVar>?,
    valueChanged: CPointer<UByteVarOf<UByte>>?,
): UByte {
    return try {
        val filter = state?.asStableRef<AbstractCompactionFilter<*>>()?.get()
            ?: return 0u
        if (filter.filter(level, key, keyLength, existingValue, valueLength, newValue, newValueLength, valueChanged)) {
            1u
        } else {
            0u
        }
    } catch (_: Throwable) {
        0u
    }
}

private fun compactionFilterNameCallback(state: COpaquePointer?): CPointer<ByteVar>? {
    return try {
        state?.asStableRef<AbstractCompactionFilter<*>>()?.get()?.namePointer()
            ?: fallbackCompactionFilterName
    } catch (_: Throwable) {
        fallbackCompactionFilterName
    }
}
