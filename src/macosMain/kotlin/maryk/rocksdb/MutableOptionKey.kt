package maryk.rocksdb

actual interface MutableOptionKey {
    actual fun getValueType(): MutableOptionKeyValueType
    actual fun name(): String
}

actual enum class MutableOptionKeyValueType {
    DOUBLE, LONG, INT, BOOLEAN, INT_ARRAY, ENUM
}
