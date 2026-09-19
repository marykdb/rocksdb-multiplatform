package maryk.rocksdb

/**
 * Helper to build external SST files for ingestion.
 */
expect class SstFileWriter(envOptions: EnvOptions, options: Options) : RocksObject {
    fun open(filePath: String)

    fun put(key: ByteArray, value: ByteArray)

    fun merge(key: ByteArray, value: ByteArray)

    fun delete(key: ByteArray)

    fun finish()

    fun fileSize(): Long
}
