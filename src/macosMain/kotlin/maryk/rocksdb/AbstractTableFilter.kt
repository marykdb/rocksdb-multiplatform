package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractTableFilter actual constructor() : RocksCallbackObject(), TableFilter {
    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>) = createNewTableFilter()
}

private fun createNewTableFilter(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
