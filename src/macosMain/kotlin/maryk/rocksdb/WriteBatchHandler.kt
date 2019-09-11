package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class WriteBatchHandler actual constructor() : RocksCallbackObject() {
    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>) = createNewHandler0()

    actual abstract fun put(columnFamilyId: Int, key: ByteArray, value: ByteArray)
    actual abstract fun put(key: ByteArray, value: ByteArray)
    actual abstract fun merge(columnFamilyId: Int, key: ByteArray, value: ByteArray)
    actual abstract fun merge(key: ByteArray, value: ByteArray)
    actual abstract fun delete(columnFamilyId: Int, key: ByteArray)
    actual abstract fun delete(key: ByteArray)
    actual abstract fun singleDelete(columnFamilyId: Int, key: ByteArray)
    actual abstract fun singleDelete(key: ByteArray)
    actual abstract fun deleteRange(columnFamilyId: Int, beginKey: ByteArray, endKey: ByteArray)
    actual abstract fun deleteRange(beginKey: ByteArray, endKey: ByteArray)
    actual abstract fun logData(blob: ByteArray)
    actual abstract fun putBlobIndex(columnFamilyId: Int, key: ByteArray, value: ByteArray)
    actual abstract fun markBeginPrepare()
    actual abstract fun markEndPrepare(xid: ByteArray)
    actual abstract fun markNoop(emptyBatch: Boolean)
    actual abstract fun markRollback(xid: ByteArray)
    actual abstract fun markCommit(xid: ByteArray)
    actual fun shouldContinue(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun createNewHandler0(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
