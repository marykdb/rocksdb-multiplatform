package maryk.rocksdb

actual enum class PerfLevel(internal val value: Byte) {
    UNINITIALIZED(0),
    DISABLE(1),
    ENABLE_COUNT(2),
    ENABLE_TIME_EXCEPT_FOR_MUTEX(3),
    ENABLE_TIME_AND_CPU_TIME_EXCEPT_FOR_MUTEX(4),
    ENABLE_TIME(5),
    OUT_OF_BOUNDS(6);

    actual fun getValue(): Byte = value
}

actual fun perfLevelFromValue(value: Byte): PerfLevel =
    PerfLevel.values().firstOrNull { it.value == value }
        ?: throw IllegalArgumentException("Unknown PerfLevel constant $value")
