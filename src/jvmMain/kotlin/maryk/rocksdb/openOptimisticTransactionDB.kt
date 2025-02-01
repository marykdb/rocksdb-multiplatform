package maryk.rocksdb

@Throws(RocksDBException::class)
actual fun openOptimisticTransactionDB(
    dbOptions: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): OptimisticTransactionDB =
    org.rocksdb.OptimisticTransactionDB.open(dbOptions, path, columnFamilyDescriptors, columnFamilyHandles)

@Throws(RocksDBException::class)
actual fun openOptimisticTransactionDB(
    options: Options,
    path: String
): OptimisticTransactionDB =
    org.rocksdb.OptimisticTransactionDB.open(options, path)
