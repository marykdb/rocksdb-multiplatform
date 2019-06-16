package maryk.rocksdb

import org.rocksdb.OptionsUtil

actual object OptionsUtil {
    actual fun loadLatestOptions(
        dbPath: String,
        env: Env,
        dbOptions: DBOptions,
        cfDescs: List<ColumnFamilyDescriptor>,
        ignoreUnknownOptions: Boolean
    ) {
        OptionsUtil.loadLatestOptions(dbPath, env, dbOptions, cfDescs, ignoreUnknownOptions)
    }

    actual fun loadOptionsFromFile(
        optionsFileName: String,
        env: Env,
        dbOptions: DBOptions,
        cfDescs: List<ColumnFamilyDescriptor>,
        ignoreUnknownOptions: Boolean
    ) {
        OptionsUtil.loadOptionsFromFile(optionsFileName, env, dbOptions, cfDescs, ignoreUnknownOptions)
    }

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
    actual fun getLatestOptionsFileName(dbPath: String, env: Env): String =
        OptionsUtil.getLatestOptionsFileName(dbPath, env)
}
