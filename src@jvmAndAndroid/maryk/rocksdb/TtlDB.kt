package maryk.rocksdb

actual typealias TtlDB = org.rocksdb.TtlDB

actual fun openTtlDB(options: Options, dbPath: String): TtlDB =
    org.rocksdb.TtlDB.open(options, dbPath)

actual fun openTtlDB(options: Options, dbPath: String, ttl: Int, readOnly: Boolean): TtlDB =
    org.rocksdb.TtlDB.open(options, dbPath, ttl, readOnly)

actual fun openTtlDB(
    options: DBOptions,
    dbPath: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>,
    ttlValues: List<Int>,
    readOnly: Boolean
): TtlDB = org.rocksdb.TtlDB.open(
    options,
    dbPath,
    columnFamilyDescriptors,
    columnFamilyHandles,
    ttlValues,
    readOnly
)
