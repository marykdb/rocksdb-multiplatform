package maryk.rocksdb

actual enum class WalProcessingOption(
    private val value: Byte
) {
    CONTINUE_PROCESSING(0x0),
    IGNORE_CURRENT_RECORD(0x1),
    STOP_REPLAY(0x2),
    CORRUPTED_RECORD(0x3);
}
