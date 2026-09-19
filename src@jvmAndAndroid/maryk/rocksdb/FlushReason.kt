package maryk.rocksdb

actual typealias FlushReason = org.rocksdb.FlushReason

actual fun FlushReason.value(): Byte = ordinal.toByte()

actual fun flushReasonFromValue(value: Byte): FlushReason =
    FlushReason.values().firstOrNull { it.ordinal.toByte() == value }
        ?: error("Unknown FlushReason value: $value")
