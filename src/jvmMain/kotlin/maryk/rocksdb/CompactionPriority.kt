package maryk.rocksdb

actual typealias CompactionPriority = org.rocksdb.CompactionPriority

actual fun getCompactionPriority(value: Byte) =
    CompactionPriority.getCompactionPriority(value)
