package maryk.rocksdb

actual typealias CompactionStopStyle = org.rocksdb.CompactionStopStyle

actual fun getCompactionStopStyle(value: Byte) =
    CompactionStopStyle.getCompactionStopStyle(value)
