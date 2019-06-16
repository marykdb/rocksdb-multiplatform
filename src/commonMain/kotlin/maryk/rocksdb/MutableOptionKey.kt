package maryk.rocksdb

expect interface MutableOptionKey {
    fun getValueType(): MutableOptionKeyValueType
    fun name(): String
}

expect enum class MutableOptionKeyValueType {
    DOUBLE,
    LONG,
    INT,
    BOOLEAN,
    INT_ARRAY,
    ENUM
}
