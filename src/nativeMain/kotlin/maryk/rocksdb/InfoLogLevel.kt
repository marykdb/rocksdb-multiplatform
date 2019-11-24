package maryk.rocksdb

import rocksdb.RocksDBLogLevel
import rocksdb.RocksDBLogLevelDebug
import rocksdb.RocksDBLogLevelError
import rocksdb.RocksDBLogLevelFatal
import rocksdb.RocksDBLogLevelHeader
import rocksdb.RocksDBLogLevelInfo
import rocksdb.RocksDBLogLevelNumInfoLogLevels
import rocksdb.RocksDBLogLevelWarn

actual enum class InfoLogLevel(
    internal val value: RocksDBLogLevel
) {
    DEBUG_LEVEL(RocksDBLogLevelDebug),
    INFO_LEVEL(RocksDBLogLevelInfo),
    WARN_LEVEL(RocksDBLogLevelWarn),
    ERROR_LEVEL(RocksDBLogLevelError),
    FATAL_LEVEL(RocksDBLogLevelFatal),
    HEADER_LEVEL(RocksDBLogLevelHeader),
    NUM_INFO_LOG_LEVELS(RocksDBLogLevelNumInfoLogLevels);
}

/**
 * Get InfoLogLevel by byte value.
 *
 * @param value byte representation of InfoLogLevel.
 *
 * @return [InfoLogLevel] instance.
 * @throws IllegalArgumentException if an invalid
 * value is provided.
 */
fun getInfoLogLevel(value: RocksDBLogLevel): InfoLogLevel {
    for (infoLogLevel in InfoLogLevel.values()) {
        if (infoLogLevel.value == value) {
            return infoLogLevel
        }
    }
    throw IllegalArgumentException("Illegal value provided for InfoLogLevel.")
}
