package maryk.rocksdb

actual enum class WalFileType(
    private val value: Byte
) {
    kArchivedLogFile(0x0),
    kAliveLogFile(0x1)
}
