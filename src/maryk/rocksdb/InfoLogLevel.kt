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
}
