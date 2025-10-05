package maryk.rocksdb

/** Metadata describing an external SST file that was ingested into a RocksDB instance. */
expect class ExternalFileIngestionInfo {
    /** Column family the file was ingested into. */
    fun columnFamilyName(): String

    /** Absolute path of the file before ingestion. */
    fun externalFilePath(): String

    /** Relative path of the file within the RocksDB data directory. */
    fun internalFilePath(): String

    /** Global sequence number assigned to keys contained in the file. */
    fun globalSequenceNumber(): Long

    /** Table properties associated with the ingested file. */
    fun tableProperties(): TableProperties?
}
