package maryk.rocksdb

actual typealias MutableColumnFamilyOptions = org.rocksdb.MutableColumnFamilyOptions

actual typealias MemtableOption = org.rocksdb.MutableColumnFamilyOptions.MemtableOption
actual typealias MiscOption = org.rocksdb.MutableColumnFamilyOptions.MiscOption
actual typealias CompactionOption = org.rocksdb.MutableColumnFamilyOptions.CompactionOption

actual typealias MutableColumnFamilyOptionsBuilder = org.rocksdb.MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder

actual fun mutableColumnFamilyOptionsBuilder() = MutableColumnFamilyOptions.builder()
actual fun mutableColumnFamilyOptionsParse(str: String) = MutableColumnFamilyOptions.parse(str)
