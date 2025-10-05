@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_configoptions_t
import kotlinx.cinterop.CPointer
import maryk.toUByte
import rocksdb.rocksdb_configoptions_create
import rocksdb.rocksdb_configoptions_destroy
import rocksdb.rocksdb_configoptions_set_delimiter
import rocksdb.rocksdb_configoptions_set_env
import rocksdb.rocksdb_configoptions_set_ignore_unknown_options
import rocksdb.rocksdb_configoptions_set_input_strings_escaped
import rocksdb.rocksdb_configoptions_set_sanity_level

actual class ConfigOptions internal constructor(
    internal val native: CPointer<rocksdb_configoptions_t>?,
) : RocksObject() {
    actual constructor() : this(rocksdb_configoptions_create())

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_configoptions_destroy(native)
            super.close()
        }
    }

    actual fun setDelimiter(delimiter: String): ConfigOptions {
        rocksdb_configoptions_set_delimiter(native, delimiter)
        return this
    }

    actual fun setIgnoreUnknownOptions(ignore: Boolean): ConfigOptions {
        rocksdb_configoptions_set_ignore_unknown_options(native, ignore.toUByte())
        return this
    }

    actual fun setEnv(env: Env): ConfigOptions {
        rocksdb_configoptions_set_env(native, env.native)
        return this
    }

    actual fun setInputStringsEscaped(escaped: Boolean): ConfigOptions {
        rocksdb_configoptions_set_input_strings_escaped(native, escaped.toUByte())
        return this
    }

    actual fun setSanityLevel(level: SanityLevel): ConfigOptions {
        rocksdb_configoptions_set_sanity_level(native, level.toNativeValue())
        return this
    }
}

private fun SanityLevel.toNativeValue(): UByte = when (this) {
    SanityLevel.NONE -> 0u
    SanityLevel.LOOSELY_COMPATIBLE -> 1u
    SanityLevel.EXACT_MATCH -> 2u
}
