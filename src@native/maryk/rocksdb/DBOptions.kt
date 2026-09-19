@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT
import maryk.asUInt64
import maryk.sizeTToLong
import maryk.toBoolean
import maryk.toCheckedLong
import maryk.toUByte
import rocksdb.rocksdb_options_add_eventlistener
import rocksdb.rocksdb_options_create
import rocksdb.rocksdb_options_destroy
import rocksdb.rocksdb_options_get_WAL_size_limit_MB
import rocksdb.rocksdb_options_get_create_if_missing
import rocksdb.rocksdb_options_get_create_missing_column_families
import rocksdb.rocksdb_options_get_error_if_exists
import rocksdb.rocksdb_options_get_info_log_level
import rocksdb.rocksdb_options_get_keep_log_file_num
import rocksdb.rocksdb_options_get_log_file_time_to_roll
import rocksdb.rocksdb_options_get_max_log_file_size
import rocksdb.rocksdb_options_get_paranoid_checks
import rocksdb.rocksdb_options_get_wal_recovery_mode
import rocksdb.rocksdb_options_get_use_fsync
import rocksdb.rocksdb_options_set_WAL_size_limit_MB
import rocksdb.rocksdb_options_set_create_if_missing
import rocksdb.rocksdb_options_set_create_missing_column_families
import rocksdb.rocksdb_options_set_error_if_exists
import rocksdb.rocksdb_options_set_info_log_level
import rocksdb.rocksdb_options_set_keep_log_file_num
import rocksdb.rocksdb_options_set_log_file_time_to_roll
import rocksdb.rocksdb_options_set_max_log_file_size
import rocksdb.rocksdb_options_set_paranoid_checks
import rocksdb.rocksdb_options_set_use_fsync
import rocksdb.rocksdb_options_set_wal_recovery_mode
import rocksdb.rocksdb_options_set_env

actual fun DBOptions.addEventListener(listener: EventListener): DBOptions {
    checkOwningHandle()
    check(listener.disownHandle()) { "EventListener is already closed or registered." }
    rocksdb_options_add_eventlistener(native, listener.native)
    return this
}

actual fun Options.addEventListener(listener: EventListener): Options {
    checkOwningHandle()
    check(listener.disownHandle()) { "EventListener is already closed or registered." }
    rocksdb_options_add_eventlistener(native, listener.native)
    return this
}

actual class DBOptions internal constructor(
    internal val native: CPointer<rocksdb_options_t>
) : RocksObject(), DBOptionsInterface<DBOptions> {
    private var statistics: Statistics? = null
    private var env: Env? = null

    actual constructor() : this(requireNotNull(rocksdb_options_create()) { "Unable to allocate RocksDB DB options" })

    override fun close() {
        if (tryClose()) {
            statistics?.disconnectFromNative(native)
            rocksdb_options_destroy(native)
            statistics = null
            env = null
            super.close()
        }
    }

    actual fun setCreateIfMissing(flag: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_create_if_missing(native, flag.toUByte())
        return this
    }

    actual fun createIfMissing(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_create_if_missing(native).toBoolean()
    }

    actual fun setCreateMissingColumnFamilies(flag: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_create_missing_column_families(native, flag.toUByte())
        return this
    }

    actual fun createMissingColumnFamilies(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_create_missing_column_families(native).toBoolean()
    }

    actual fun setErrorIfExists(errorIfExists: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_error_if_exists(native, errorIfExists.toUByte())
        return this
    }

    actual fun errorIfExists(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_error_if_exists(native).toBoolean()
    }

    actual fun setParanoidChecks(paranoidChecks: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_paranoid_checks(native, paranoidChecks.toUByte())
        return this
    }

    actual fun paranoidChecks(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_paranoid_checks(native).toBoolean()
    }

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_info_log_level(native, infoLogLevel.value.toInt())
        return this
    }

    actual fun infoLogLevel(): InfoLogLevel {
        checkOwningHandle()
        return getInfoLogLevel(
            rocksdb_options_get_info_log_level(native).toUByte()
        )
    }

    actual fun setStatistics(statistics: Statistics): DBOptions {
        checkOwningHandle()
        this.statistics?.disconnectFromNative(native)
        this.statistics = statistics
        statistics.connectWithNative(native)
        return this
    }

    actual fun statistics(): Statistics? {
        checkOwningHandle()
        return this.statistics
    }

    actual override fun setEnv(env: Env): DBOptions {
        checkOwningHandle()
        env.checkOwningHandle()
        rocksdb_options_set_env(native, env.native)
        this.env = env
        return this
    }

    actual override fun getEnv(): Env {
        checkOwningHandle()
        if (env == null) {
            env = getDefaultEnv()
        }
        return requireNotNull(env)
    }

    internal fun retainedNativeReferences(): List<Any> =
        listOfNotNull(env, statistics)

    actual fun setUseFsync(useFsync: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_use_fsync(native, if (useFsync) 1 else 0)
        return this
    }

    actual fun useFsync(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_fsync(native) == 1
    }

    actual fun setMaxLogFileSize(maxLogFileSize: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_log_file_size(native, maxLogFileSize.asSizeT())
        return this
    }

    actual fun maxLogFileSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_max_log_file_size(native), "DB options max log file size")
    }

    actual fun setLogFileTimeToRoll(logFileTimeToRoll: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_log_file_time_to_roll(native, logFileTimeToRoll.asSizeT())
        return this
    }

    actual fun logFileTimeToRoll(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_log_file_time_to_roll(native), "DB options log file time to roll")
    }

    actual fun setKeepLogFileNum(keepLogFileNum: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_keep_log_file_num(native, keepLogFileNum.asSizeT())
        return this
    }

    actual fun keepLogFileNum(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_keep_log_file_num(native), "DB options keep log file count")
    }

    actual fun setWalSizeLimitMB(sizeLimitMB: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_WAL_size_limit_MB(native, sizeLimitMB.asUInt64())
        return this
    }

    actual fun walSizeLimitMB(): Long {
        checkOwningHandle()
        return rocksdb_options_get_WAL_size_limit_MB(native).toCheckedLong("DB options WAL size limit MB")
    }

    actual fun setWalRecoveryMode(mode: WALRecoveryMode): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_wal_recovery_mode(native, mode.getValue().toInt())
        return this
    }

    actual fun walRecoveryMode(): WALRecoveryMode {
        checkOwningHandle()
        return walRecoveryModeFromValue(rocksdb_options_get_wal_recovery_mode(native).toByte())
    }
}
