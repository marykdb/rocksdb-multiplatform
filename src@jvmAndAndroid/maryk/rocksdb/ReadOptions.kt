package maryk.rocksdb

actual typealias ReadOptions = org.rocksdb.ReadOptions

actual fun ReadOptions.setTableFilter(tableFilter: AbstractTableFilter): ReadOptions =
    this.apply { setTableFilter(tableFilter as org.rocksdb.AbstractTableFilter) }
