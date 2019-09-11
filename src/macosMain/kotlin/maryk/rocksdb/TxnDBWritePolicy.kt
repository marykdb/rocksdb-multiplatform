package maryk.rocksdb

actual enum class TxnDBWritePolicy(
    private val value: Byte
) {
    WRITE_COMMITTED(0x0),
    WRITE_PREPARED(0x1),
    WRITE_UNPREPARED(0x2);

    actual fun getValue() = value
}
