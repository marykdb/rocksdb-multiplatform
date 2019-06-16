package maryk.rocksdb

/**
 * Open a TransactionDB, similar to [RocksDB.open].
 *
 * @param options [org.rocksdb.Options] instance.
 * @param transactionDbOptions [org.rocksdb.TransactionDBOptions]
 * instance.
 * @param path the path to the rocksdb.
 *
 * @return a [TransactionDB] instance on success, null if the specified
 * [TransactionDB] can not be opened.
 *
 * @throws RocksDBException if an error occurs whilst opening the database.
 */
expect fun openTransactionDB(
    options: Options,
    transactionDbOptions: TransactionDBOptions,
    path: String
): TransactionDB

/**
 * Open a TransactionDB, similar to
 * [RocksDB.open].
 *
 * @param dbOptions [org.rocksdb.DBOptions] instance.
 * @param transactionDbOptions [org.rocksdb.TransactionDBOptions]
 * instance.
 * @param path the path to the rocksdb.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 *
 * @return a [TransactionDB] instance on success, null if the specified
 * [TransactionDB] can not be opened.
 *
 * @throws RocksDBException if an error occurs whilst opening the database.
 */
expect fun openTransactionDB(
    dbOptions: DBOptions,
    transactionDbOptions: TransactionDBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): TransactionDB
