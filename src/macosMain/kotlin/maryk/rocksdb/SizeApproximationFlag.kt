package maryk.rocksdb

actual enum class SizeApproximationFlag(
    private val value: Byte
) {
    NONE(0x0),
    INCLUDE_MEMTABLES(0x1),
    INCLUDE_FILES(0x2)
}
