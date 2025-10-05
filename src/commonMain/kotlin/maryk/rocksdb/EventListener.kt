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
}
