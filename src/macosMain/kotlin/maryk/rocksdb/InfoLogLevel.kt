package maryk.rocksdb

actual enum class InfoLogLevel(
    private val value: Byte
) {
    DEBUG_LEVEL(0x0),
    INFO_LEVEL(0x1),
    WARN_LEVEL(0x2),
    ERROR_LEVEL(0x3),
    FATAL_LEVEL(0x4),
    HEADER_LEVEL(0x5),
    NUM_INFO_LOG_LEVELS(0x6);

    actual fun getValue() = value
}

actual fun getInfoLogLevel(value: Byte): InfoLogLevel {
    for (infoLogLevel in InfoLogLevel.values()) {
        if (infoLogLevel.getValue() == value) {
            return infoLogLevel
        }
    }
    throw IllegalArgumentException("Illegal value provided for InfoLogLevel.")
}
