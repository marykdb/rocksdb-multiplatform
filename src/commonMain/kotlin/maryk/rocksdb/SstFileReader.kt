package maryk.rocksdb

/**
 * Helper to inspect external SST files prior to ingestion.
 */
expect class SstFileReader(options: Options) : RocksObject {
    fun open(filePath: String)

    fun verifyChecksum()
}
