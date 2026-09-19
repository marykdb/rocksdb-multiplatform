package maryk.rocksdb

actual typealias MutableColumnFamilyOptions = org.rocksdb.MutableColumnFamilyOptions

actual typealias MutableColumnFamilyOptionsBuilder =
    org.rocksdb.MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder

actual fun mutableColumnFamilyOptionsBuilder(): MutableColumnFamilyOptionsBuilder =
    org.rocksdb.MutableColumnFamilyOptions.builder()

actual fun parseMutableColumnFamilyOptions(
    str: String,
    ignoreUnknown: Boolean
): MutableColumnFamilyOptionsBuilder =
    org.rocksdb.MutableColumnFamilyOptions.parse(str, ignoreUnknown)
