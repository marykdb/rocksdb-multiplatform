package maryk.rocksdb

/** Receives callbacks for background RocksDB operations. */
expect abstract class EventListener : RocksCallbackObject {
    /** Invoked before a flush job starts. */
    open fun onFlushBeginEvent(db: RocksDB, flushJobInfo: FlushJobInfo)

    /** Invoked after a flush completes. */
    open fun onFlushCompletedEvent(db: RocksDB, flushJobInfo: FlushJobInfo)

    /** Invoked before a compaction job begins. */
    open fun onCompactionBeginEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo)

    /** Invoked when a compaction job finishes. */
    open fun onCompactionCompletedEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo)

    /** Invoked after an external SST file has been ingested into the database. */
    open fun onExternalFileIngested(db: RocksDB, ingestionInfo: ExternalFileIngestionInfo)

    /** Invoked when RocksDB reports a background error condition. */
    open fun onBackgroundErrorEvent(reason: BackgroundErrorReason, status: Status?)

    /** Invoked when the write controller transitions between stall conditions. */
    open fun onStallConditionsChanged(info: WriteStallInfo)

    /** Invoked when a memtable has been sealed. */
    open fun onMemTableSealed(info: MemTableInfo)
    /** Additional callbacks receive copied metadata. Background callbacks must not throw. */
    open fun onTableFileCreationStartedEvent(info: TableFileCreationBriefInfo)
    open fun onTableFileCreatedEvent(info: TableFileCreationInfo)
    open fun onTableFileDeletedEvent(info: TableFileDeletionInfo)
    /** Notification only: Java 11.8.1 cannot safely query the borrowed handle in this callback. */
    open fun onColumnFamilyHandleDeletionStartedEvent()
    open fun onFileReadFinishEvent(info: FileOperationInfo)
    open fun onFileWriteFinishEvent(info: FileOperationInfo)
    open fun onFileFlushFinishEvent(info: FileOperationInfo)
    open fun onFileSyncFinishEvent(info: FileOperationInfo)
    open fun onFileRangeSyncFinishEvent(info: FileOperationInfo)
    open fun onFileTruncateFinishEvent(info: FileOperationInfo)
    open fun onFileCloseFinishEvent(info: FileOperationInfo)
    open fun onErrorRecoveryCompletedEvent(oldBackgroundError: Status)

    /** File-I/O callbacks are opt-in; false avoids their instrumentation overhead. */
    open fun shouldBeNotifiedOnFileIO(): Boolean

    /**
     * Return false to prevent this automatic recovery attempt. Defaults to true.
     * Only the four reasons represented by Java/JNI 11.8.1 are forwarded.
     */
    open fun onErrorRecoveryBeginEvent(reason: BackgroundErrorReason, backgroundError: Status): Boolean
}
