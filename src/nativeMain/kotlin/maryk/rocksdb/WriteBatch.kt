package maryk.rocksdb

import maryk.toByteArray
import rocksdb.RocksDBWriteBatch

actual class WriteBatch(internal val native: RocksDBWriteBatch) : AbstractWriteBatch(native) {
    actual constructor() : this(RocksDBWriteBatch())

    actual fun getDataSize() = native.dataSize().toLong()

    actual fun getWalTerminationPoint(): WriteBatchSavePoint {
        val terminationPoint = native.getWalTerminationPoint()
        return WriteBatchSavePoint(
            terminationPoint.size.toLong(),
            terminationPoint.count.toLong(),
            terminationPoint.contentFlags.toLong()
        )
    }

    actual fun data() = native.data().toByteArray()

    actual fun hasPut() = native.hasPut()

    actual fun hasDelete() = native.hasDelete()

    actual fun hasSingleDelete() = native.hasSingleDelete()

    actual fun hasDeleteRange() = native.hasDeleteRange()

    actual fun hasMerge() = native.hasMerge()

    actual fun hasBeginPrepare() = native.hasBeginPrepare()

    actual fun hasEndPrepare() = native.hasEndPrepare()

    actual fun hasCommit() = native.hasCommit()

    actual fun hasRollback() = native.hasRollback()

    actual fun markWalTerminationPoint() {
        native.markWalTerminationPoint()
    }

    override fun getWriteBatch(): WriteBatch {
        return this
    }
}
