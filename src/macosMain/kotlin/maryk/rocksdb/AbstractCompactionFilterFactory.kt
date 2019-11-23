package maryk.rocksdb

actual abstract class AbstractCompactionFilterFactory<T : AbstractCompactionFilter<*>> actual constructor() :
    RocksCallbackObject() {
    actual abstract fun name(): String

    actual abstract fun createCompactionFilter(context: AbstractCompactionFilterContext): T
}
