package maryk.rocksdb

/**
 * This class provides a custom logger functionality
 * in Java which wraps {@code RocksDB} logging facilities.
 *
 * Using this class RocksDB can log with common
 * Java logging APIs like Log4j or Slf4j without keeping
 * database logs in the filesystem.
 *
 * # Performance
 * There are certain performance penalties using a Java
 * {@code Logger} implementation within production code.
 *
 * A log level can be set using {@link org.rocksdb.Options} or
 * {@link Logger#setInfoLogLevel(InfoLogLevel)}. The set log level
 * influences the underlying native code. Each log message is
 * checked against the set log level and if the log level is more
 * verbose as the set log level, native allocations will be made
 * and data structures are allocated.
 *
 * Every log message which will be emitted by native code will
 * trigger expensive native to Java transitions. So the preferred
 * setting for production use is either
 * {@link InfoLogLevel#ERROR_LEVEL} or
 * {@link InfoLogLevel#FATAL_LEVEL}.
 */
expect abstract class Logger : RocksCallbackObject {
    /**
     * AbstractLogger constructor.
     *
     * **Important:** the log level set within
     * the [org.rocksdb.Options] instance will be used as
     * maximum log level of RocksDB.
     *
     * @param options [org.rocksdb.Options] instance.
     */
    constructor(options: Options)
    /**
     * AbstractLogger constructor.
     *
     * **Important:** the log level set within
     * the [org.rocksdb.DBOptions] instance will be used
     * as maximum log level of RocksDB.
     *
     * @param dboptions [org.rocksdb.DBOptions] instance.
     */
    constructor(dboptions: DBOptions)

    /**
     * Set [org.rocksdb.InfoLogLevel] to AbstractLogger.
     *
     * @param infoLogLevel [org.rocksdb.InfoLogLevel] instance.
     */
    fun setInfoLogLevel(infoLogLevel: InfoLogLevel)

    /**
     * Return the loggers log level.
     *
     * @return [org.rocksdb.InfoLogLevel] instance.
     */
    fun infoLogLevel(): InfoLogLevel

    protected abstract fun log(
        infoLogLevel: InfoLogLevel,
        logMsg: String
    )
}
