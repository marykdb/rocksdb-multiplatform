package maryk.rocksdb

actual typealias MutableDBOptions = org.rocksdb.MutableDBOptions

actual typealias DBOption = org.rocksdb.MutableDBOptions.DBOption

actual typealias MutableDBOptionsBuilder = org.rocksdb.MutableDBOptions.MutableDBOptionsBuilder

actual fun mutableDBOptionsBuilder() = MutableDBOptions.builder()

actual fun parseMutableDBOptionsBuilder(str: String) = MutableDBOptions.parse(str)
