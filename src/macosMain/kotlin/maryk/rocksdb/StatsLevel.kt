package maryk.rocksdb

actual enum class StatsLevel(
    private val value: Byte
) {
    EXCEPT_DETAILED_TIMERS(0),
    EXCEPT_TIME_FOR_MUTEX(1),
    ALL(2);

    fun getValue(): Byte = value
}

actual fun getStatsLevel(value: Byte): StatsLevel {
    for (statsLevel in StatsLevel.values()) {
        if (statsLevel.getValue() == value) {
            return statsLevel
        }
    }
    throw IllegalArgumentException(
        "Illegal value provided for StatsLevel."
    )
}
