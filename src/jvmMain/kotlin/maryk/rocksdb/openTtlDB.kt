package maryk.rocksdb

actual typealias TtlDB = org.rocksdb.TtlDB

actual fun openTtlDB(options: Options, db_path: String) = TtlDB.open(options, db_path)

actual fun openTtlDB(
    options: Options,
    db_path: String,
    ttl: Int,
    readOnly: Boolean
) = TtlDB.open(options, db_path, ttl, readOnly)

actual fun openTtlDB(
    options: DBOptions,
    db_path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>,
    ttlValues: List<Int>,
    readOnly: Boolean
) = TtlDB.open(options, db_path, columnFamilyDescriptors, columnFamilyHandles, ttlValues, readOnly)
