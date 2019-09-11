package maryk.rocksdb

actual fun openTransactionDB(
    options: Options,
    transactionDbOptions: TransactionDBOptions,
    path: String
): TransactionDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openTransactionDB(
    dbOptions: DBOptions,
    transactionDbOptions: TransactionDBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): TransactionDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
