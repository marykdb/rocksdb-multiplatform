package maryk.rocksdb

actual abstract class EventListener : org.rocksdb.AbstractEventListener() {
    actual open fun onFlushBeginEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {}

    actual open fun onFlushCompletedEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {}

    actual open fun onCompactionBeginEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {}

    actual open fun onCompactionCompletedEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {}

    actual open fun onExternalFileIngested(db: RocksDB, ingestionInfo: ExternalFileIngestionInfo) {}

    actual open fun onBackgroundErrorEvent(reason: BackgroundErrorReason, status: Status?) {}

    actual open fun onStallConditionsChanged(info: WriteStallInfo) {}

    actual open fun onMemTableSealed(info: MemTableInfo) {}

    final override fun onFlushBegin(db: org.rocksdb.RocksDB, flushJobInfo: org.rocksdb.FlushJobInfo) {
        onFlushBeginEvent(db, FlushJobInfo(flushJobInfo))
    }

    final override fun onFlushCompleted(db: org.rocksdb.RocksDB, flushJobInfo: org.rocksdb.FlushJobInfo) {
        onFlushCompletedEvent(db, FlushJobInfo(flushJobInfo))
    }

    final override fun onCompactionBegin(
        db: org.rocksdb.RocksDB,
        compactionJobInfo: org.rocksdb.CompactionJobInfo
    ) {
        onCompactionBeginEvent(db, CompactionJobInfo(compactionJobInfo))
    }

    final override fun onCompactionCompleted(
        db: org.rocksdb.RocksDB,
        compactionJobInfo: org.rocksdb.CompactionJobInfo
    ) {
        onCompactionCompletedEvent(db, CompactionJobInfo(compactionJobInfo))
    }

    final override fun onExternalFileIngested(
        db: org.rocksdb.RocksDB,
        ingestionInfo: org.rocksdb.ExternalFileIngestionInfo
    ) {
        onExternalFileIngested(db, ExternalFileIngestionInfo(ingestionInfo))
    }

    final override fun onBackgroundError(
        reason: org.rocksdb.BackgroundErrorReason,
        backgroundError: org.rocksdb.Status
    ) {
        onBackgroundErrorEvent(reason, backgroundError)
    }

    final override fun onStallConditionsChanged(writeStallInfo: org.rocksdb.WriteStallInfo) {
        onStallConditionsChanged(WriteStallInfo(writeStallInfo))
    }

    final override fun onMemTableSealed(memTableInfo: org.rocksdb.MemTableInfo) {
        onMemTableSealed(MemTableInfo(memTableInfo))
    }
}
