package maryk.rocksdb

/** RocksDB variant with time-to-live support. */
expect class TtlDB : RocksDB {
    fun createColumnFamilyWithTtl(
        columnFamilyDescriptor: ColumnFamilyDescriptor,
        ttl: Int
    ): ColumnFamilyHandle
}

expect fun openTtlDB(options: Options, dbPath: String): TtlDB

expect fun openTtlDB(options: Options, dbPath: String, ttl: Int, readOnly: Boolean): TtlDB

expect fun openTtlDB(
    options: DBOptions,
    dbPath: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>,
    ttlValues: List<Int>,
    readOnly: Boolean
): TtlDB
