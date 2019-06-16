package maryk.rocksdb.util

import maryk.rocksdb.WriteBatchHandler

class WriteBatchGetter(private val key: ByteArray) : WriteBatchHandler() {
    private var columnFamilyId = -1
    var value: ByteArray? = null
        private set

    override fun put(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    ) {
        if (this.key.contentEquals(key)) {
            this.columnFamilyId = columnFamilyId
            this.value = value
        }
    }

    override fun put(key: ByteArray, value: ByteArray) {
        if (this.key.contentEquals(key)) {
            this.value = value
        }
    }

    override fun merge(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    ) {
        if (this.key.contentEquals(key)) {
            this.columnFamilyId = columnFamilyId
            this.value = value
        }
    }

    override fun merge(key: ByteArray, value: ByteArray) {
        if (this.key.contentEquals(key)) {
            this.value = value
        }
    }

    override fun delete(columnFamilyId: Int, key: ByteArray) {
        if (this.key.contentEquals(key)) {
            this.columnFamilyId = columnFamilyId
            this.value = null
        }
    }

    override fun delete(key: ByteArray) {
        if (this.key.contentEquals(key)) {
            this.value = null
        }
    }

    override fun singleDelete(columnFamilyId: Int, key: ByteArray) {
        if (this.key.contentEquals(key)) {
            this.columnFamilyId = columnFamilyId
            this.value = null
        }
    }

    override fun singleDelete(key: ByteArray) {
        if (this.key.contentEquals(key)) {
            this.value = null
        }
    }

    override fun deleteRange(
        columnFamilyId: Int, beginKey: ByteArray,
        endKey: ByteArray
    ) {
        throw UnsupportedOperationException()
    }

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        throw UnsupportedOperationException()
    }

    override fun logData(blob: ByteArray) {
        throw UnsupportedOperationException()
    }

    override fun putBlobIndex(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    ) {
        if (this.key.contentEquals(key)) {
            this.columnFamilyId = columnFamilyId
            this.value = value
        }
    }

    // @Throws(RocksDBException::class)
    override fun markBeginPrepare() {
        throw UnsupportedOperationException()
    }

    // @Throws(RocksDBException::class)
    override fun markEndPrepare(xid: ByteArray) {
        throw UnsupportedOperationException()
    }

    // @Throws(RocksDBException::class)
    override fun markNoop(emptyBatch: Boolean) {
        throw UnsupportedOperationException()
    }

    // @Throws(RocksDBException::class)
    override fun markRollback(xid: ByteArray) {
        throw UnsupportedOperationException()
    }

    // @Throws(RocksDBException::class)
    override fun markCommit(xid: ByteArray) {
        throw UnsupportedOperationException()
    }
}
