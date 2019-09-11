package maryk.rocksdb

actual enum class AccessHint(
    private val value: Byte
) {
    NONE(0),
    NORMAL(1),
    SEQUENTIAL(2),
    WILLNEED(3);

    actual fun getValue() = value
}
