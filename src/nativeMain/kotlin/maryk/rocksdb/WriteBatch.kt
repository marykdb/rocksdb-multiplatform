package maryk.rocksdb

import cnames.structs.rocksdb_writebatch_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toByteArray
import platform.posix.uint64_tVar
import rocksdb.rocksdb_writebatch_create

actual class WriteBatch(
    internal val native: CPointer<rocksdb_writebatch_t>
) : AbstractWriteBatch(native) {
    actual constructor() : this(rocksdb_writebatch_create()!!)

    actual fun getDataSize(): Long = throw NotImplementedError("DO SOMETHING")

    actual fun getWalTerminationPoint(): WriteBatchSavePoint {
        throw NotImplementedError("DO SOMETHING")
//        val terminationPoint = native.getWalTerminationPoint()
//        return WriteBatchSavePoint(
//            terminationPoint.size.toLong(),
//            terminationPoint.count.toLong(),
//            terminationPoint.contentFlags.toLong()
//        )
    }

    actual fun data(): ByteArray {
        val length = nativeHeap.alloc<uint64_tVar>()
        return rocksdb.rocksdb_writebatch_data(native, length.ptr)!!.toByteArray(length.value)
    }

    actual fun hasPut(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasDelete(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasSingleDelete(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasDeleteRange(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasMerge(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasBeginPrepare(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasEndPrepare(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasCommit(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun hasRollback(): Boolean = throw NotImplementedError("DO SOMETHING")

    actual fun markWalTerminationPoint() {
        throw NotImplementedError("DO SOMETHING")
    }

    override fun getWriteBatch(): WriteBatch {
        return this
    }
}
