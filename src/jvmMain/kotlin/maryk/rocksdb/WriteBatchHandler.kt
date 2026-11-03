package maryk.rocksdb

actual fun WriteBatch.iterate(handler: WriteBatchHandler) {
    check(isOwningHandle()) { "Write batch is closed or borrowed" }
    var failure: Throwable? = null
    fun visit(block: () -> Unit) {
        if (failure == null) {
            try { block() } catch (error: Throwable) { failure = error }
        }
    }
    val bridge = object : org.rocksdb.WriteBatch.Handler() {
        override fun put(columnFamilyId: Int, key: ByteArray, value: ByteArray) = visit { handler.put(columnFamilyId, key, value) }
        override fun put(key: ByteArray, value: ByteArray) = visit { handler.put(key, value) }
        override fun merge(columnFamilyId: Int, key: ByteArray, value: ByteArray) = visit { handler.merge(columnFamilyId, key, value) }
        override fun merge(key: ByteArray, value: ByteArray) = visit { handler.merge(key, value) }
        override fun delete(columnFamilyId: Int, key: ByteArray) = visit { handler.delete(columnFamilyId, key) }
        override fun delete(key: ByteArray) = visit { handler.delete(key) }
        override fun singleDelete(columnFamilyId: Int, key: ByteArray) = visit { handler.singleDelete(columnFamilyId, key) }
        override fun singleDelete(key: ByteArray) = visit { handler.singleDelete(key) }
        override fun deleteRange(columnFamilyId: Int, beginKey: ByteArray, endKey: ByteArray) = visit { handler.deleteRange(columnFamilyId, beginKey, endKey) }
        override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) = visit { handler.deleteRange(beginKey, endKey) }
        override fun logData(blob: ByteArray) = visit { handler.logData(blob) }
        override fun putBlobIndex(columnFamilyId: Int, key: ByteArray, value: ByteArray) = visit { handler.putBlobIndex(columnFamilyId, key, value) }
        override fun markBeginPrepare() = visit { handler.markBeginPrepare() }
        override fun markEndPrepare(xid: ByteArray) = visit { handler.markEndPrepare(xid) }
        override fun markNoop(emptyBatch: Boolean) = visit { handler.markNoop(emptyBatch) }
        override fun markRollback(xid: ByteArray) = visit { handler.markRollback(xid) }
        override fun markCommit(xid: ByteArray) = visit { handler.markCommit(xid) }
        override fun markCommitWithTimestamp(xid: ByteArray, timestamp: ByteArray) = visit { handler.markCommitWithTimestamp(xid, timestamp) }
        override fun shouldContinue(): Boolean {
            if (failure != null) return false
            return try { handler.shouldContinue() } catch (error: Throwable) {
                failure = error
                false
            }
        }
    }
    try {
        bridge.use { iterate(it) }
    } catch (error: Throwable) {
        failure?.let { throw it }
        throw error
    }
    failure?.let { throw it }
}

actual fun writeBatchFromData(serialized: ByteArray): WriteBatch {
    require(serialized.size >= 12) { "A serialized write batch needs its 12-byte header" }
    return WriteBatch(serialized)
}
