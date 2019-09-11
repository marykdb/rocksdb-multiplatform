package maryk.rocksdb

actual object OptionsUtil {
    actual fun loadLatestOptions(
        dbPath: String,
        env: Env,
        dbOptions: DBOptions,
        cfDescs: List<ColumnFamilyDescriptor>,
        ignoreUnknownOptions: Boolean
    ) {
    }

    actual fun loadOptionsFromFile(
        optionsFileName: String,
        env: Env,
        dbOptions: DBOptions,
        cfDescs: List<ColumnFamilyDescriptor>,
        ignoreUnknownOptions: Boolean
    ) {
    }

    actual fun getLatestOptionsFileName(dbPath: String, env: Env): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
