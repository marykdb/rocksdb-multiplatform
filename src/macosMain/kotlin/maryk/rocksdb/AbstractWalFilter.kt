package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractWalFilter actual constructor() : RocksCallbackObject(), WalFilter {
    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>): CPointer<*> {
        return createNewWalFilter()
    }
}

private fun createNewWalFilter(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
