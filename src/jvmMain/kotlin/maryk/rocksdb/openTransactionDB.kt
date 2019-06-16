package maryk.rocksdb

actual fun openTransactionDB(
    options: Options,
    transactionDbOptions: TransactionDBOptions,
    path: String
): TransactionDB = TransactionDB.open(options, transactionDbOptions, path)

actual fun openTransactionDB(
    dbOptions: DBOptions,
    transactionDbOptions: TransactionDBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): TransactionDB = TransactionDB.open(dbOptions, transactionDbOptions, path, columnFamilyDescriptors, columnFamilyHandles)
