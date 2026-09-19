package maryk.rocksdb

actual object OptionsUtil {
    @Throws(RocksDBException::class)
    actual fun loadLatestOptions(
        configOptions: ConfigOptions,
        dbPath: String,
        dbOptions: DBOptions,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>
    ) {
        org.rocksdb.OptionsUtil.loadLatestOptions(
            configOptions,
            dbPath,
            dbOptions,
            columnFamilyDescriptors
        )
    }

    @Throws(RocksDBException::class)
    actual fun loadOptionsFromFile(
        configOptions: ConfigOptions,
        optionsFilePath: String,
        dbOptions: DBOptions,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>
    ) {
        org.rocksdb.OptionsUtil.loadOptionsFromFile(
            configOptions,
            optionsFilePath,
            dbOptions,
            columnFamilyDescriptors
        )
    }

    @Throws(RocksDBException::class)
    actual fun getLatestOptionsFileName(dbPath: String, env: Env): String {
        return org.rocksdb.OptionsUtil.getLatestOptionsFileName(dbPath, env)
    }
}
