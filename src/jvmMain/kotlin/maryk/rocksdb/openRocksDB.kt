package maryk.rocksdb

actual fun openRocksDB(path: String) = RocksDB.open(path)

actual fun openRocksDB(options: Options, path: String) = RocksDB.open(options, path)

actual fun openRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: List<ColumnFamilyHandle>
) = RocksDB.open(path, columnFamilyDescriptors, columnFamilyHandles)

actual fun openRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: List<ColumnFamilyHandle>
) = RocksDB.open(options, path, columnFamilyDescriptors, columnFamilyHandles)

actual fun openReadOnlyRocksDB(path: String) =
    RocksDB.openReadOnly(path)

actual fun openReadOnlyRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
) = RocksDB.openReadOnly(path, columnFamilyDescriptors, columnFamilyHandles)

actual fun openReadOnlyRocksDB(options: Options, path: String) =
    RocksDB.openReadOnly(options, path)

actual fun openReadOnlyRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
) = RocksDB.openReadOnly(options, path, columnFamilyDescriptors, columnFamilyHandles)
