package maryk.rocksdb

import cnames.structs.rocksdb_comparator_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.convert
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.usePinned
import maryk.ByteBuffer
import maryk.DirectByteBuffer
import platform.posix.memcpy
import rocksdb.rocksdb_comparator_destroy

actual abstract class AbstractComparator
    protected actual constructor(val copt: ComparatorOptions?)
: RocksCallbackObject() {
    protected actual constructor() : this(null)

    private val pinnedName: CPointer<ByteVar> by lazy {
        val actualName = name()
        val nameBytes = (actualName + "\u0000").encodeToByteArray()
        val mem = nativeHeap.allocArray<ByteVar>(nameBytes.size)

        nameBytes.usePinned { pinned: Pinned<ByteArray> ->
            memcpy(mem, pinned.addressOf(0), nameBytes.size.convert())
        }
        mem
    }

    private val nameCallback = staticCFunction<COpaquePointer?, CPointer<ByteVar>?> { statePtr ->
        val comparator = statePtr?.asStableRef<AbstractComparator>()?.get()
            ?: return@staticCFunction null
        comparator.pinnedName
    }

    private val stableRef = StableRef.create(this)

    private val destructorCallback = staticCFunction<COpaquePointer?, Unit> { ref ->
        ref?.asStableRef<AbstractComparator>()?.dispose()
    }

    val native: CPointer<rocksdb_comparator_t>? by lazy {
        rocksdb.rocksdb_comparator_create(
            name = nameCallback,
            state = stableRef.asCPointer(),
            compare = staticCFunction { statePtr, aPtr, aLen, bPtr, bLen ->
                val comparator = statePtr?.asStableRef<AbstractComparator>()?.get()
                    ?: return@staticCFunction 0

                if (aPtr == null || bPtr == null) {
                    return@staticCFunction 0
                }

                comparator.compare(DirectByteBuffer(aPtr, aLen.toInt()), DirectByteBuffer(bPtr, bLen.toInt()))
            },
            destructor = destructorCallback,
        )
    }

    actual override fun close() {
        if (isOwningHandle()) {
            nativeHeap.free(pinnedName.rawValue)
            rocksdb_comparator_destroy(native)
            super.close()
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
