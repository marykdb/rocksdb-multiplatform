package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class WriteBatch : AbstractWriteBatch {
    actual constructor() : this(0)

    actual constructor(reserved_bytes: Int) : super(newWriteBatch(reserved_bytes))

    actual constructor(serialized: ByteArray) : super(newWriteBatch(serialized, serialized.size))

    actual fun getDataSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getWalTerminationPoint(): WriteBatchSavePoint {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun iterate(handler: WriteBatchHandler) {
    }

    actual fun data(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasPut(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasDelete(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasSingleDelete(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasDeleteRange(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasMerge(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasBeginPrepare(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasEndPrepare(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasCommit(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hasRollback(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun markWalTerminationPoint() {
    }

    override fun singleDelete(key: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun newWriteBatch(reservedBytes: Int): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newWriteBatch(reservedBytes: ByteArray, size: Int): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
