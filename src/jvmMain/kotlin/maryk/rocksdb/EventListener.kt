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

    actual open fun onTableFileCreationStartedEvent(info: TableFileCreationBriefInfo) {}
    actual open fun onTableFileCreatedEvent(info: TableFileCreationInfo) {}
    actual open fun onTableFileDeletedEvent(info: TableFileDeletionInfo) {}
    actual open fun onColumnFamilyHandleDeletionStartedEvent() {}
    actual open fun onFileReadFinishEvent(info: FileOperationInfo) {}
    actual open fun onFileWriteFinishEvent(info: FileOperationInfo) {}
    actual open fun onFileFlushFinishEvent(info: FileOperationInfo) {}
    actual open fun onFileSyncFinishEvent(info: FileOperationInfo) {}
    actual open fun onFileRangeSyncFinishEvent(info: FileOperationInfo) {}
    actual open fun onFileTruncateFinishEvent(info: FileOperationInfo) {}
    actual open fun onFileCloseFinishEvent(info: FileOperationInfo) {}
    actual open fun onErrorRecoveryCompletedEvent(oldBackgroundError: Status) {}

    actual override fun shouldBeNotifiedOnFileIO(): Boolean = false

    actual open fun onErrorRecoveryBeginEvent(reason: BackgroundErrorReason, backgroundError: Status): Boolean = true


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

    final override fun onTableFileCreationStarted(info: org.rocksdb.TableFileCreationBriefInfo) {
        onTableFileCreationStartedEvent(TableFileCreationBriefInfo(
            info.dbName, info.columnFamilyName, info.filePath, info.jobId,
            TableFileCreationReason.valueOf(info.reason.name),
        ))
    }

    final override fun onTableFileCreated(info: org.rocksdb.TableFileCreationInfo) {
        onTableFileCreatedEvent(TableFileCreationInfo(
            info.dbName, info.columnFamilyName, info.filePath, info.jobId,
            TableFileCreationReason.valueOf(info.reason.name), info.fileSize,
            TableProperties(info.tableProperties), info.status,
        ))
    }

    final override fun onTableFileDeleted(info: org.rocksdb.TableFileDeletionInfo) {
        onTableFileDeletedEvent(TableFileDeletionInfo(info.dbName, info.filePath, info.jobId, info.status))
    }

    final override fun onColumnFamilyHandleDeletionStarted(handle: org.rocksdb.ColumnFamilyHandle) {
        // JNI passes a borrowed handle whose name/ID accessors fail with assertions enabled.
        // Forward the notification without querying or retaining the dying handle.
        onColumnFamilyHandleDeletionStartedEvent()
    }

    final override fun onFileReadFinish(info: org.rocksdb.FileOperationInfo) {
        onFileReadFinishEvent(copyFileOperationInfo(info))
    }

    final override fun onFileWriteFinish(info: org.rocksdb.FileOperationInfo) {
        onFileWriteFinishEvent(copyFileOperationInfo(info))
    }

    final override fun onFileFlushFinish(info: org.rocksdb.FileOperationInfo) {
        onFileFlushFinishEvent(copyFileOperationInfo(info, hasRange = false))
    }

    final override fun onFileSyncFinish(info: org.rocksdb.FileOperationInfo) {
        onFileSyncFinishEvent(copyFileOperationInfo(info, hasRange = false))
    }

    final override fun onFileRangeSyncFinish(info: org.rocksdb.FileOperationInfo) {
        onFileRangeSyncFinishEvent(copyFileOperationInfo(info))
    }

    final override fun onFileTruncateFinish(info: org.rocksdb.FileOperationInfo) {
        onFileTruncateFinishEvent(copyFileOperationInfo(info, hasRange = false))
    }

    final override fun onFileCloseFinish(info: org.rocksdb.FileOperationInfo) {
        onFileCloseFinishEvent(copyFileOperationInfo(info, hasRange = false))
    }

    final override fun onErrorRecoveryBegin(reason: org.rocksdb.BackgroundErrorReason, backgroundError: org.rocksdb.Status): Boolean =
        onErrorRecoveryBeginEvent(reason, backgroundError)

    final override fun onErrorRecoveryCompleted(oldBackgroundError: org.rocksdb.Status) {
        onErrorRecoveryCompletedEvent(oldBackgroundError)
    }
}

private fun copyFileOperationInfo(info: org.rocksdb.FileOperationInfo, hasRange: Boolean = true): FileOperationInfo = FileOperationInfo(
    info.path, if (hasRange) info.offset else 0, if (hasRange) info.length else 0,
    info.startTimestamp, info.duration, info.status,
)
