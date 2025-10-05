package maryk.rocksdb

/**
 * Helpers for reading RocksDB options files into live option instances.
 */
expect object OptionsUtil {
    @Throws(RocksDBException::class)
    fun loadLatestOptions(
        configOptions: ConfigOptions,
        dbPath: String,
        dbOptions: DBOptions,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>
    )

    @Throws(RocksDBException::class)
    fun loadOptionsFromFile(
        configOptions: ConfigOptions,
        optionsFilePath: String,
        dbOptions: DBOptions,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>
    )

    @Throws(RocksDBException::class)
    fun getLatestOptionsFileName(dbPath: String, env: Env): String
}
