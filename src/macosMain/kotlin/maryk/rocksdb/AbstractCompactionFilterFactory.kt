package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractCompactionFilterFactory<T : AbstractCompactionFilter<*>> actual constructor() :
    RocksCallbackObject() {
    actual abstract fun name(): String

    actual abstract fun createCompactionFilter(context: AbstractCompactionFilterContext): T

    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>): CPointer<*> {
        TODO("IMPLEMENT")
    }
}
