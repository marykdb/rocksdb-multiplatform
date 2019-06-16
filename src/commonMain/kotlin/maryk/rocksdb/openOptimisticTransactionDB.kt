package maryk.rocksdb

/**
 * Open an OptimisticTransactionDB similar to
 * [RocksDB.open].
 *
 * @param options [org.rocksdb.Options] instance.
 * @param path the path to the rocksdb.
 *
 * @return a [OptimisticTransactionDB] instance on success, null if the
 * specified [OptimisticTransactionDB] can not be opened.
 *
 * @throws RocksDBException if an error occurs whilst opening the database.
 */
expect fun openOptimisticTransactionDB(
    options: Options,
    path: String
): OptimisticTransactionDB

/**
 * Open an OptimisticTransactionDB similar to
 * [RocksDB.open].
 *
 * @param dbOptions [org.rocksdb.DBOptions] instance.
 * @param path the path to the rocksdb.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 *
 * @return a [OptimisticTransactionDB] instance on success, null if the
 * specified [OptimisticTransactionDB] can not be opened.
 *
 * @throws RocksDBException if an error occurs whilst opening the database.
 */
expect fun openOptimisticTransactionDB(
    dbOptions: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): OptimisticTransactionDB
