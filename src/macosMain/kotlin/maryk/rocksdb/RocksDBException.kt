package maryk.rocksdb

actual class RocksDBException
    actual constructor(msg: String, private val status: Status?)
: Exception(msg) {
    actual constructor(msg: String) : this(msg, null)

    actual constructor(status: Status) : this(
        status.getState() ?: status.getCodeString(),
        status
    )

    actual fun getStatus() = status
}
