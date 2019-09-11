package maryk.rocksdb

actual fun openOptimisticTransactionDB(
    options: Options,
    path: String
): OptimisticTransactionDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openOptimisticTransactionDB(
    dbOptions: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): OptimisticTransactionDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
