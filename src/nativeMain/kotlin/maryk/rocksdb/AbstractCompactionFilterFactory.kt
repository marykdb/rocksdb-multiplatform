@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_compactionfiltercontext_t
import cnames.structs.rocksdb_compactionfilter_t
import cnames.structs.rocksdb_compactionfilterfactory_t
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
import maryk.asSizeT
import maryk.toBoolean
import platform.posix.memcpy
import kotlin.concurrent.AtomicReference

private val fallbackCompactionFilterFactoryName: CPointer<ByteVar> = run {
    val nameBytes = "KotlinCompactionFilterFactory\u0000".encodeToByteArray()
    val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)
    nameBytes.usePinned { pinned ->
        memcpy(mem, pinned.addressOf(0), nameBytes.size.asSizeT())
    }
    mem
}

actual abstract class AbstractCompactionFilterFactory<T : AbstractCompactionFilter<*>> actual constructor() :
    RocksCallbackObject() {
    private val pinnedName = AtomicReference<CPointer<ByteVar>?>(null)
    private var stableRef: StableRef<AbstractCompactionFilterFactory<T>>? = null

    private val nativeRef = lazy {
        val ref = StableRef.create(this)
        try {
            val created = rocksdb.rocksdb_compactionfilterfactory_create(
                state = ref.asCPointer(),
                destructor = staticCFunction(::compactionFilterFactoryDestructor),
                create_compaction_filter = staticCFunction(::createCompactionFilterCallback),
                name = staticCFunction(::compactionFilterFactoryNameCallback),
            ) ?: error("Unable to create RocksDB compaction filter factory")
            stableRef = ref
            created
        } catch (throwable: Throwable) {
            ref.dispose()
            throw throwable
        }
    }

    internal val native: CPointer<rocksdb_compactionfilterfactory_t>
        get() = nativeRef.value

    actual abstract fun name(): String

    actual abstract fun createCompactionFilter(context: AbstractCompactionFilterContext): T

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
            requireNotNull(pinnedName.value) { "Compaction filter factory name was cleared while being initialized." }
        }
    }

    internal fun namePointer(): CPointer<ByteVar> = pinnedName()

    internal fun transferOwnershipToNative(): CPointer<rocksdb_compactionfilterfactory_t> {
        checkOwningHandle()
        val nativeHandle = native
        check(disownHandle()) { "Compaction filter factory is already closed or registered." }
        return nativeHandle
    }

    internal fun destroyFromNative() {
        pinnedName.value?.let {
            if (pinnedName.compareAndSet(it, null)) {
                nativeHeap.free(it.rawValue)
            }
        }
        tryCloseTransferred()
    }

    override fun close() {
        if (tryClose()) {
            destroyNativeState()
            if (nativeRef.isInitialized()) {
                rocksdb.rocksdb_compactionfilterfactory_destroy(native)
            } else {
                stableRef?.dispose()
                stableRef = null
            }
            super.close()
        }
    }

    private fun destroyNativeState() {
        pinnedName.value?.let {
            if (pinnedName.compareAndSet(it, null)) {
                nativeHeap.free(it.rawValue)
            }
        }
    }
}

private fun compactionFilterFactoryDestructor(state: COpaquePointer?) {
    try {
        state?.asStableRef<AbstractCompactionFilterFactory<*>>()?.let { stableRef ->
            try {
                stableRef.get().destroyFromNative()
            } finally {
                stableRef.dispose()
            }
        }
    } catch (_: Throwable) {
    }
}

private fun createCompactionFilterCallback(
    state: COpaquePointer?,
    context: CPointer<rocksdb_compactionfiltercontext_t>?,
): CPointer<rocksdb_compactionfilter_t>? {
    return try {
        val factory = state?.asStableRef<AbstractCompactionFilterFactory<*>>()?.get()
            ?: return null
        val fullCompaction = context?.let { rocksdb.rocksdb_compactionfiltercontext_is_full_compaction(it).toBoolean() } ?: false
        val manualCompaction = context?.let { rocksdb.rocksdb_compactionfiltercontext_is_manual_compaction(it).toBoolean() } ?: false
        val filter = factory.createCompactionFilter(AbstractCompactionFilterContext(fullCompaction, manualCompaction))
        filter.transferOwnershipToNative()
    } catch (_: Throwable) {
        null
    }
}

private fun compactionFilterFactoryNameCallback(state: COpaquePointer?): CPointer<ByteVar>? {
    return try {
        state?.asStableRef<AbstractCompactionFilterFactory<*>>()?.get()?.namePointer()
            ?: fallbackCompactionFilterFactoryName
    } catch (_: Throwable) {
        fallbackCompactionFilterFactoryName
    }
}
