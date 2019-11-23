package maryk.rocksdb

import rocksdb.RocksDBDatabaseOptions

actual class DBOptions internal constructor(
    internal val native: RocksDBDatabaseOptions
) : RocksObject() {
    private var statistics: Statistics? = null

    actual constructor() : this(RocksDBDatabaseOptions())

    actual fun setCreateIfMissing(flag: Boolean): DBOptions {
        native.createIfMissing = flag
        return this
    }

    actual fun createIfMissing() = native.createIfMissing()

    actual fun setCreateMissingColumnFamilies(flag: Boolean): DBOptions {
        native.createMissingColumnFamilies = flag
        return this
    }

    actual fun createMissingColumnFamilies() = native.createMissingColumnFamilies()

    actual fun setErrorIfExists(errorIfExists: Boolean): DBOptions {
        native.errorIfExists = errorIfExists
        return this
    }

    actual fun errorIfExists() = native.errorIfExists()

    actual fun setParanoidChecks(paranoidChecks: Boolean): DBOptions {
        native.paranoidChecks = paranoidChecks
        return this
    }

    actual fun paranoidChecks() = native.paranoidChecks()

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel): DBOptions {
        native.setInfoLogLevel(infoLogLevel.value)
        return this
    }

    actual fun infoLogLevel() = getInfoLogLevel(native.infoLogLevel())

    actual fun setStatistics(statistics: Statistics): DBOptions {
        this.statistics = statistics
        native.statistics = statistics.native
        return this
    }

    actual fun statistics(): Statistics? {
        return this.statistics ?:
            native.statistics?.let { Statistics(it) }
    }

    actual fun setUseFsync(useFsync: Boolean): DBOptions {
        native.useFSync = useFsync
        return this
    }

    actual fun useFsync() = native.useFSync()

    actual fun setMaxLogFileSize(maxLogFileSize: Long): DBOptions {
        native.setMaxLogFileSize(maxLogFileSize.toULong())
        return this
    }

    actual fun maxLogFileSize(): Long {
        return native.maxLogFileSize.toLong()
    }

    actual fun setLogFileTimeToRoll(logFileTimeToRoll: Long): DBOptions {
        native.setLogFileTimeToRoll(logFileTimeToRoll.toULong())
        return this
    }

    actual fun logFileTimeToRoll(): Long {
        return native.logFileTimeToRoll.toLong()
    }

    actual fun setKeepLogFileNum(keepLogFileNum: Long): DBOptions {
        native.setKeepLogFileNum(keepLogFileNum.toULong())
        return this
    }

    actual fun keepLogFileNum(): Long {
        return native.keepLogFileNum.toLong()
    }

    actual fun setWalSizeLimitMB(sizeLimitMB: Long): DBOptions {
        native.setMaxWriteAheadLogSize(sizeLimitMB.toULong() * 1024uL * 1024uL)
        return this
    }

    actual fun walSizeLimitMB(): Long {
        return (native.maxWriteAheadLogSize / 1024uL / 1024uL).toLong()
    }
}
