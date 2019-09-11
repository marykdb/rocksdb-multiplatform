package maryk.rocksdb

actual enum class EncodingType(
    private val value: Byte
) {
    kPlain(0),
    kPrefix(1);

    actual fun getValue() = value
}
