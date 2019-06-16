package maryk.rocksdb

/**
 * IngestExternalFileOptions is used by
 * {@link RocksDB#ingestExternalFile(ColumnFamilyHandle, List, IngestExternalFileOptions)}.
 */
expect class IngestExternalFileOptions() : RocksObject {
    /**
     * @param moveFiles [.setMoveFiles]
     * @param snapshotConsistency [.setSnapshotConsistency]
     * @param allowGlobalSeqNo [.setAllowGlobalSeqNo]
     * @param allowBlockingFlush [.setAllowBlockingFlush]
     */
    constructor(
        moveFiles: Boolean,
        snapshotConsistency: Boolean, allowGlobalSeqNo: Boolean,
        allowBlockingFlush: Boolean
    )

    /**
     * Can be set to true to move the files instead of copying them.
     *
     * @return true if files will be moved
     */
    fun moveFiles(): Boolean

    /**
     * Can be set to true to move the files instead of copying them.
     *
     * @param moveFiles true if files should be moved instead of copied
     *
     * @return the reference to the current IngestExternalFileOptions.
     */
    fun setMoveFiles(moveFiles: Boolean): IngestExternalFileOptions

    /**
     * If set to false, an ingested file keys could appear in existing snapshots
     * that where created before the file was ingested.
     *
     * @return true if snapshot consistency is assured
     */
    fun snapshotConsistency(): Boolean

    /**
     * If set to false, an ingested file keys could appear in existing snapshots
     * that where created before the file was ingested.
     *
     * @param snapshotConsistency true if snapshot consistency is required
     *
     * @return the reference to the current IngestExternalFileOptions.
     */
    fun setSnapshotConsistency(
        snapshotConsistency: Boolean
    ): IngestExternalFileOptions

    /**
     * If set to false, [RocksDB.ingestExternalFile]
     * will fail if the file key range overlaps with existing keys or tombstones in the DB.
     *
     * @return true if global seq numbers are assured
     */
    fun allowGlobalSeqNo(): Boolean

    /**
     * If set to false, [RocksDB.ingestExternalFile]
     * will fail if the file key range overlaps with existing keys or tombstones in the DB.
     *
     * @param allowGlobalSeqNo true if global seq numbers are required
     *
     * @return the reference to the current IngestExternalFileOptions.
     */
    fun setAllowGlobalSeqNo(
        allowGlobalSeqNo: Boolean
    ): IngestExternalFileOptions

    /**
     * If set to false and the file key range overlaps with the memtable key range
     * (memtable flush required), IngestExternalFile will fail.
     *
     * @return true if blocking flushes may occur
     */
    fun allowBlockingFlush(): Boolean

    /**
     * If set to false and the file key range overlaps with the memtable key range
     * (memtable flush required), IngestExternalFile will fail.
     *
     * @param allowBlockingFlush true if blocking flushes are allowed
     *
     * @return the reference to the current IngestExternalFileOptions.
     */
    fun setAllowBlockingFlush(
        allowBlockingFlush: Boolean
    ): IngestExternalFileOptions

    /**
     * Returns true if duplicate keys in the file being ingested are
     * to be skipped rather than overwriting existing data under that key.
     *
     * @return true if duplicate keys in the file being ingested are to be
     * skipped, false otherwise.
     */
    fun ingestBehind(): Boolean

    /**
     * Set to true if you would like duplicate keys in the file being ingested
     * to be skipped rather than overwriting existing data under that key.
     *
     * Usecase: back-fill of some historical data in the database without
     * over-writing existing newer version of data.
     *
     * This option could only be used if the DB has been running
     * with DBOptions#allowIngestBehind() == true since the dawn of time.
     *
     * All files will be ingested at the bottommost level with seqno=0.
     *
     * Default: false
     *
     * @param ingestBehind true if you would like duplicate keys in the file being
     * ingested to be skipped.
     *
     * @return the reference to the current IngestExternalFileOptions.
     */
    fun setIngestBehind(ingestBehind: Boolean): IngestExternalFileOptions

    /**
     * Returns true write if the global_seqno is written to a given offset
     * in the external SST file for backward compatibility.
     *
     * See [.setWriteGlobalSeqno].
     *
     * @return true if the global_seqno is written to a given offset,
     * false otherwise.
     */
    fun writeGlobalSeqno(): Boolean

    /**
     * Set to true if you would like to write the global_seqno to a given offset
     * in the external SST file for backward compatibility.
     *
     * Older versions of RocksDB write the global_seqno to a given offset within
     * the ingested SST files, and new versions of RocksDB do not.
     *
     * If you ingest an external SST using new version of RocksDB and would like
     * to be able to downgrade to an older version of RocksDB, you should set
     * [.writeGlobalSeqno] to true.
     *
     * If your service is just starting to use the new RocksDB, we recommend that
     * you set this option to false, which brings two benefits:
     * 1. No extra random write for global_seqno during ingestion.
     * 2. Without writing external SST file, it's possible to do checksum.
     *
     * We have a plan to set this option to false by default in the future.
     *
     * Default: true
     *
     * @param writeGlobalSeqno true to write the gloal_seqno to a given offset,
     * false otherwise
     *
     * @return the reference to the current IngestExternalFileOptions.
     */
    fun setWriteGlobalSeqno(
        writeGlobalSeqno: Boolean
    ): IngestExternalFileOptions
}
