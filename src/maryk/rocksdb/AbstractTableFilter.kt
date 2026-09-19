package maryk.rocksdb

/** Base class for table filters implemented in Kotlin. */
expect abstract class AbstractTableFilter protected constructor() : RocksCallbackObject {
    protected abstract fun filter(tableProperties: TableProperties): Boolean
}
