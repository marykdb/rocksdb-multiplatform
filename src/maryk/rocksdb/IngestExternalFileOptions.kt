package maryk.rocksdb

/**
 * Options controlling ingestion of external SST files.
 */
expect class IngestExternalFileOptions() : RocksObject {
    fun setMoveFiles(moveFiles: Boolean): IngestExternalFileOptions
    fun moveFiles(): Boolean

    fun setSnapshotConsistency(snapshotConsistency: Boolean): IngestExternalFileOptions
    fun snapshotConsistency(): Boolean

    fun setAllowGlobalSeqNo(allowGlobalSeqNo: Boolean): IngestExternalFileOptions
    fun allowGlobalSeqNo(): Boolean

    fun setAllowBlockingFlush(allowBlockingFlush: Boolean): IngestExternalFileOptions
    fun allowBlockingFlush(): Boolean

    fun setIngestBehind(ingestBehind: Boolean): IngestExternalFileOptions
    fun ingestBehind(): Boolean
}
