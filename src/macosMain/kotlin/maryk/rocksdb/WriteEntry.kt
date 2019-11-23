package maryk.rocksdb

actual class WriteEntry actual constructor(
    val type: WriteType,
    val key: DirectSlice,
    val value: DirectSlice?
) : AutoCloseable {
    actual fun getType() = type

    actual fun getKey() = key

    actual fun getValue() = value

    override fun close() {}

    override fun equals(other: Any?) = when {
        this === other -> true
        other !is WriteEntry -> false
        else -> type == other.type && key == other.key && value == other.value
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}
