package maryk.rocksdb

@Throws(RocksDBException::class)
expect fun openOptimisticTransactionDB(options: Options, path: String): OptimisticTransactionDB

/**
 * Open an OptimisticTransactionDB similar to
 * {@link RocksDB#open(DBOptions, String, List, List)}.
 *
 * @param dbOptions {@link org.rocksdb.DBOptions} instance.
 * @param path the path to the rocksdb.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 *
 * @return a {@link OptimisticTransactionDB} instance on success, null if the
 *     specified {@link OptimisticTransactionDB} can not be opened.
 *
 * @throws RocksDBException if an error occurs whilst opening the database.
 */
@Throws(RocksDBException::class)
expect fun openOptimisticTransactionDB(
    dbOptions: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): OptimisticTransactionDB
