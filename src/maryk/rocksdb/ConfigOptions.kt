package maryk.rocksdb

/**
 * Options for configuring how RocksDB parses options files and strings.
 */
expect class ConfigOptions() : RocksObject {
    fun setDelimiter(delimiter: String): ConfigOptions
    fun setIgnoreUnknownOptions(ignore: Boolean): ConfigOptions
    fun setEnv(env: Env): ConfigOptions
    fun setInputStringsEscaped(escaped: Boolean): ConfigOptions
    fun setSanityLevel(level: SanityLevel): ConfigOptions
}
