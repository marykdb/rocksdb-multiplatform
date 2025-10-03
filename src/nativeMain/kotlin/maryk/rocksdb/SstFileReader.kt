package maryk.rocksdb

actual class SstFileReader actual constructor(
    @Suppress("UNUSED_PARAMETER") options: Options
) : RocksObject() {
    actual fun open(@Suppress("UNUSED_PARAMETER") filePath: String) {
        throw RocksDBException("SstFileReader is not yet supported on Kotlin/Native; update the RocksDB C API to enable it.")
    }

    actual fun verifyChecksum() {
        throw RocksDBException("SstFileReader is not yet supported on Kotlin/Native; update the RocksDB C API to enable it.")
    }
}
