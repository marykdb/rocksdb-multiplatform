package maryk.rocksdb

actual fun openOptimisticTransactionDB(
    options: Options,
    path: String
): OptimisticTransactionDB = OptimisticTransactionDB.open(options, path)

actual fun openOptimisticTransactionDB(
    dbOptions: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): OptimisticTransactionDB = OptimisticTransactionDB.open(dbOptions, path, columnFamilyDescriptors, columnFamilyHandles)
