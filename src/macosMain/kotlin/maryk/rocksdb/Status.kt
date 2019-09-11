package maryk.rocksdb

import maryk.rocksdb.StatusSubCode.None

actual class Status actual constructor(
    private val code: StatusCode,
    private val subCode: StatusSubCode?,
    private val state: String?
) {

    actual fun getCode() = code

    actual fun getSubCode() = subCode

    actual fun getState() = state

    actual fun getCodeString() =
        code.name + if (subCode != null && subCode != None) {
            "(${subCode.name})"
        } else ""
}

actual enum class StatusCode(
    private val value: Byte
) {
    Ok(0x0),
    NotFound(0x1),
    Corruption(0x2),
    NotSupported(0x3),
    InvalidArgument(0x4),
    IOError(0x5),
    MergeInProgress(0x6),
    Incomplete(0x7),
    ShutdownInProgress(0x8),
    TimedOut(0x9),
    Aborted(0xA),
    Busy(0xB),
    Expired(0xC),
    TryAgain(0xD),
    Undefined(0x7F)
}

actual enum class StatusSubCode(
    private val value: Byte
) {
    None(0x0),
    MutexTimeout(0x1),
    LockTimeout(0x2),
    LockLimit(0x3),
    NoSpace(0x4),
    Deadlock(0x5),
    StaleFile(0x6),
    MemoryLimit(0x7),
    Undefined(0x7F)
}
