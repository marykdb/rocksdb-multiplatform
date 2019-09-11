package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractTraceWriter actual constructor() : RocksCallbackObject(), TraceWriter {
    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>) = createNewTraceWriter()
}

private fun createNewTraceWriter(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
