package maryk.rocksdb

actual enum class WriteType(
    private val value: Byte
) {
    PUT(0x0),
    MERGE(0x1),
    DELETE(0x2),
    SINGLE_DELETE(0x3),
    DELETE_RANGE(0x4),
    LOG(0x5),
    XID(0x6);
}
