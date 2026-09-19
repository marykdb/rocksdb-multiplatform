package maryk.rocksdb

/**
 * Represents the status returned by a function call in RocksDB.
 *
 * Currently only used with {@link RocksDBException} when the
 * status is not {@link Code#Ok}
 */
expect class Status(code: StatusCode, subCode: StatusSubCode?, state: String?) {
    fun getCode(): StatusCode

    fun getSubCode(): StatusSubCode?

    fun getState(): String?

    fun getCodeString(): String
}

// should stay in sync with /include/rocksdb/status.h:Code and /java/rocksjni/portal.h:toJavaStatusCode
expect enum class StatusCode {
    Ok,
    NotFound,
    Corruption,
    NotSupported,
    InvalidArgument,
    IOError,
    MergeInProgress,
    Incomplete,
    ShutdownInProgress,
    TimedOut,
    Aborted,
    Busy,
    Expired,
    TryAgain,
    Undefined
}

// should stay in sync with /include/rocksdb/status.h:SubCode and /java/rocksjni/portal.h:toJavaStatusSubCode
expect enum class StatusSubCode {
    None,
    MutexTimeout,
    LockTimeout,
    LockLimit,
    NoSpace,
    Deadlock,
    StaleFile,
    MemoryLimit,
    Undefined
}
