package maryk.rocksdb

/**
 * Controls which performance counters RocksDB collects.
 */
expect enum class PerfLevel {
    UNINITIALIZED,
    DISABLE,
    ENABLE_COUNT,
    ENABLE_TIME_EXCEPT_FOR_MUTEX,
    ENABLE_TIME_AND_CPU_TIME_EXCEPT_FOR_MUTEX,
    ENABLE_TIME,
    OUT_OF_BOUNDS;

    fun getValue(): Byte
}

expect fun perfLevelFromValue(value: Byte): PerfLevel
