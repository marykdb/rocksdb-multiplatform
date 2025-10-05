package maryk.rocksdb

actual typealias PerfLevel = org.rocksdb.PerfLevel

actual fun perfLevelFromValue(value: Byte): PerfLevel = PerfLevel.getPerfLevel(value)
