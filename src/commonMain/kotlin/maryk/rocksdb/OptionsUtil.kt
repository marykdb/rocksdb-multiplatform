package maryk.rocksdb

expect object OptionsUtil {
    /**
     * A static method to construct the DBOptions and ColumnFamilyDescriptors by
     * loading the latest RocksDB options file stored in the specified rocksdb
     * database.
     *
     * Note that the all the pointer options (except table_factory, which will
     * be described in more details below) will be initialized with the default
     * values.  Developers can further initialize them after this function call.
     * Below is an example list of pointer options which will be initialized.
     *
     * - env
     * - memtable_factory
     * - compaction_filter_factory
     * - prefix_extractor
     * - comparator
     * - merge_operator
     * - compaction_filter
     *
     * For table_factory, this function further supports deserializing
     * BlockBasedTableFactory and its BlockBasedTableOptions except the
     * pointer options of BlockBasedTableOptions (flush_block_policy_factory,
     * block_cache, and block_cache_compressed), which will be initialized with
     * default values.  Developers can further specify these three options by
     * casting the return value of TableFactoroy::GetOptions() to
     * BlockBasedTableOptions and making necessary changes.
     *
     * @param dbPath the path to the RocksDB.
     * @param env [org.rocksdb.Env] instance.
     * @param dbOptions [org.rocksdb.DBOptions] instance. This will be
     * filled and returned.
     * @param cfDescs A list of [org.rocksdb.ColumnFamilyDescriptor]'s be
     * returned.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun loadLatestOptions(
        dbPath: String, env: Env, dbOptions: DBOptions,
        cfDescs: List<ColumnFamilyDescriptor>, ignoreUnknownOptions: Boolean = false
    )

    /**
     * Similar to LoadLatestOptions, this function constructs the DBOptions
     * and ColumnFamilyDescriptors based on the specified RocksDB Options file.
     * See LoadLatestOptions above.
     *
     * @param optionsFileName the RocksDB options file path.
     * @param env [org.rocksdb.Env] instance.
     * @param dbOptions [org.rocksdb.DBOptions] instance. This will be
     * filled and returned.
     * @param cfDescs A list of [org.rocksdb.ColumnFamilyDescriptor]'s be
     * returned.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun loadOptionsFromFile(
        optionsFileName: String, env: Env, dbOptions: DBOptions,
        cfDescs: List<ColumnFamilyDescriptor>, ignoreUnknownOptions: Boolean = false
    )

    /**
     * Returns the latest options file name under the specified RocksDB path.
     *
     * @param dbPath the path to the RocksDB.
     * @param env [org.rocksdb.Env] instance.
     * @return the latest options file name under the db path.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun getLatestOptionsFileName(dbPath: String, env: Env): String
}
