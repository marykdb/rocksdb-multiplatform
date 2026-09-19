package maryk.rocksdb

actual typealias MutableDBOptions = org.rocksdb.MutableDBOptions

actual typealias MutableDBOptionsBuilder =
    org.rocksdb.MutableDBOptions.MutableDBOptionsBuilder

actual fun mutableDBOptionsBuilder(): MutableDBOptionsBuilder =
    org.rocksdb.MutableDBOptions.builder()

actual fun parseMutableDBOptions(
    str: String,
    ignoreUnknown: Boolean
): MutableDBOptionsBuilder =
    org.rocksdb.MutableDBOptions.parse(str, ignoreUnknown)
