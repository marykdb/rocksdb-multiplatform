package maryk.rocksdb

actual class SstFileMetaData(
    val fileName: String,
    val path: String,
    val size: ULong,
    val smallestKey: ByteArray,
    val largestKey: ByteArray,
) {
    actual fun fileName() = fileName

    actual fun path() = path

    actual fun size() = size.toLong()

    actual fun smallestKey(): ByteArray = smallestKey

    actual fun largestKey(): ByteArray = largestKey
}
