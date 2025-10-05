@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_compression_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import rocksdb.rocksdb_compression_options_create
import rocksdb.rocksdb_compression_options_destroy
import rocksdb.rocksdb_compression_options_get_level
import rocksdb.rocksdb_compression_options_get_max_dict_bytes
import rocksdb.rocksdb_compression_options_get_strategy
import rocksdb.rocksdb_compression_options_get_window_bits
import rocksdb.rocksdb_compression_options_set_level
import rocksdb.rocksdb_compression_options_set_max_dict_bytes
import rocksdb.rocksdb_compression_options_set_strategy
import rocksdb.rocksdb_compression_options_set_window_bits

actual class CompressionOptions internal constructor(
    internal val native: CPointer<rocksdb_compression_options_t>
) : RocksObject() {
    actual constructor() : this(rocksdb_compression_options_create()!!)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_compression_options_destroy(native)
            super.close()
        }
    }

    actual fun setWindowBits(windowBits: Int): CompressionOptions {
        rocksdb_compression_options_set_window_bits(native, windowBits)
        return this
    }

    actual fun windowBits(): Int = rocksdb_compression_options_get_window_bits(native)

    actual fun setLevel(level: Int): CompressionOptions {
        rocksdb_compression_options_set_level(native, level)
        return this
    }

    actual fun level(): Int = rocksdb_compression_options_get_level(native)

    actual fun setStrategy(strategy: Int): CompressionOptions {
        rocksdb_compression_options_set_strategy(native, strategy)
        return this
    }

    actual fun strategy(): Int = rocksdb_compression_options_get_strategy(native)

    actual fun setMaxDictBytes(bytes: Int): CompressionOptions {
        rocksdb_compression_options_set_max_dict_bytes(native, bytes.toUInt())
        return this
    }

    actual fun maxDictBytes(): Int =
        rocksdb_compression_options_get_max_dict_bytes(native).toInt()
}
