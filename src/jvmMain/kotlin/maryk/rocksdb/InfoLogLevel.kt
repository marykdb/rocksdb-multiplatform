package maryk.rocksdb

actual typealias InfoLogLevel = org.rocksdb.InfoLogLevel

actual fun getInfoLogLevel(value: Byte) = InfoLogLevel.getInfoLogLevel(value)
