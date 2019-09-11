package maryk.rocksdb

actual interface TableFilter {
    actual fun filter(tableProperties: TableProperties): Boolean
}
