package maryk.rocksdb

/**
 * Logger used by RocksDB for info and debug output.
 * Close all databases, options and persistent caches using the logger before closing it.
 */
expect abstract class Logger : RocksCallbackObject

/**
 * Creates a logger receiving messages at or above [logLevel], including from
 * background threads. The callback must be thread-safe; callback exceptions are
 * contained at the native boundary and do not escape into RocksDB.
 */
expect fun createLogger(logLevel: InfoLogLevel, callback: (InfoLogLevel, String) -> Unit): Logger
