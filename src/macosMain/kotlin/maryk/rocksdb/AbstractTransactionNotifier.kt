package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractTransactionNotifier protected actual constructor() : RocksCallbackObject() {
    actual abstract fun snapshotCreated(newSnapshot: Snapshot)

    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>) =
        createNewTransactionNotifier()
}

private fun createNewTransactionNotifier(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
