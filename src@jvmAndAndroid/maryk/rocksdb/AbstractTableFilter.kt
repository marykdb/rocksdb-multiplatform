package maryk.rocksdb

actual abstract class AbstractTableFilter protected actual constructor() :
    org.rocksdb.AbstractTableFilter() {
    final override fun filter(tableProperties: org.rocksdb.TableProperties): Boolean =
        filter(TableProperties(tableProperties))

    protected actual abstract fun filter(tableProperties: TableProperties): Boolean
}
