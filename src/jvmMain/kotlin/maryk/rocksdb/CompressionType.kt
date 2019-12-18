package maryk.rocksdb

actual typealias CompressionType = org.rocksdb.CompressionType

actual fun getCompressionType(libraryName: String?) =
    CompressionType.getCompressionType(libraryName)

actual fun getCompressionType(byteIdentifier: Byte) =
    CompressionType.getCompressionType(byteIdentifier)
