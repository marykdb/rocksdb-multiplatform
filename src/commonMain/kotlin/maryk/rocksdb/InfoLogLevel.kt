package maryk.rocksdb

/** RocksDB log levels. */
expect enum class InfoLogLevel {
    DEBUG_LEVEL,
    INFO_LEVEL,
    WARN_LEVEL,
    ERROR_LEVEL,
    FATAL_LEVEL,
    HEADER_LEVEL,
    NUM_INFO_LOG_LEVELS;

    /** Returns the byte value of the enumerations value */
    fun getValue(): Byte
}

/**
 * Get InfoLogLevel by byte value.
 *
 * @param value byte representation of InfoLogLevel.
 *
 * @return [org.rocksdb.InfoLogLevel] instance.
 * @throws java.lang.IllegalArgumentException if an invalid
 * value is provided.
 */
expect fun getInfoLogLevel(value: Byte): InfoLogLevel
