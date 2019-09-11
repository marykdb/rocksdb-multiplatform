package maryk.rocksdb

actual fun openRocksDB(path: String): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openRocksDB(options: Options, path: String): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: List<ColumnFamilyHandle>
): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: List<ColumnFamilyHandle>
): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openReadOnlyRocksDB(path: String): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openReadOnlyRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openReadOnlyRocksDB(options: Options, path: String): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openReadOnlyRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
