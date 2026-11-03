@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_compactionjobinfo_t
import cnames.structs.rocksdb_event_info_t
import cnames.structs.rocksdb_eventlistener_t
import cnames.structs.rocksdb_externalfileingestioninfo_t
import cnames.structs.rocksdb_flushjobinfo_t
import cnames.structs.rocksdb_memtableinfo_t
import cnames.structs.rocksdb_subcompactionjobinfo_t
import cnames.structs.rocksdb_t
import cnames.structs.rocksdb_writestallinfo_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import maryk.convertToStatus
import maryk.toByteArray
import maryk.toCheckedLong
import platform.posix.size_t
import platform.posix.size_tVar
import rocksdb.rocksdb_eventlistener_create
import rocksdb.rocksdb_eventlistener_destroy

actual abstract class EventListener : RocksCallbackObject() {
    internal val native: CPointer<rocksdb_eventlistener_t>
    private val stableRef = StableRef.create(this)

    init {
        try {
            native = rocksdb_eventlistener_create(
                stableRef.asCPointer(),
                staticCFunction(::eventListenerDestructor),
                staticCFunction(::eventListenerOnFlushBegin),
                staticCFunction(::eventListenerOnFlushCompleted),
                staticCFunction(::eventListenerOnCompactionBegin),
                staticCFunction(::eventListenerOnCompactionCompleted),
                staticCFunction(::eventListenerOnSubcompactionBegin),
                staticCFunction(::eventListenerOnSubcompactionCompleted),
                staticCFunction(::eventListenerOnExternalFileIngested),
                staticCFunction(::eventListenerOnBackgroundError),
                staticCFunction(::eventListenerOnStallConditionsChanged),
                staticCFunction(::eventListenerOnMemTableSealed)
            ) ?: error("Failed to allocate RocksDB event listener")
            rocksdb.rocksdb_eventlistener_set_extended_callbacks(
                native, staticCFunction(::eventListenerOnExtendedEvent),
                staticCFunction(::eventListenerShouldNotifyFileIO),
                staticCFunction(::eventListenerOnRecoveryBegin),
            )
        } catch (throwable: Throwable) {
            stableRef.dispose()
            throw throwable
        }
    }

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

    actual open fun shouldBeNotifiedOnFileIO(): Boolean = false

    actual open fun onErrorRecoveryBeginEvent(reason: BackgroundErrorReason, backgroundError: Status): Boolean = true


    override fun close() {
        if (tryClose()) {
            rocksdb_eventlistener_destroy(native)
            super.close()
        }
    }
}

private fun eventListenerDestructor(state: COpaquePointer?) {
    try {
        state?.asStableRef<EventListener>()?.dispose()
    } catch (_: Throwable) {
    }
}

private fun eventListenerOnFlushBegin(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_flushjobinfo_t>?
) {
    var db: RocksDB? = null
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val info = infoPtr?.let(::FlushJobInfo) ?: return
        db = dbPtr?.let(::wrapDb) ?: return
        listener.onFlushBeginEvent(db, info)
    } catch (_: Throwable) {
    } finally {
        db?.closeNonOwningReferences()
    }
}

private fun eventListenerOnFlushCompleted(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_flushjobinfo_t>?
) {
    var db: RocksDB? = null
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val info = infoPtr?.let(::FlushJobInfo) ?: return
        db = dbPtr?.let(::wrapDb) ?: return
        listener.onFlushCompletedEvent(db, info)
    } catch (_: Throwable) {
    } finally {
        db?.closeNonOwningReferences()
    }
}

private fun eventListenerOnCompactionBegin(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_compactionjobinfo_t>?
) {
    var db: RocksDB? = null
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val info = infoPtr?.let(::CompactionJobInfo) ?: return
        db = dbPtr?.let(::wrapDb) ?: return
        listener.onCompactionBeginEvent(db, info)
    } catch (_: Throwable) {
    } finally {
        db?.closeNonOwningReferences()
    }
}

private fun eventListenerOnCompactionCompleted(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_compactionjobinfo_t>?
) {
    var db: RocksDB? = null
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val info = infoPtr?.let(::CompactionJobInfo) ?: return
        db = dbPtr?.let(::wrapDb) ?: return
        listener.onCompactionCompletedEvent(db, info)
    } catch (_: Throwable) {
    } finally {
        db?.closeNonOwningReferences()
    }
}

private fun eventListenerOnSubcompactionBegin(
    state: COpaquePointer?,
    infoPtr: CPointer<rocksdb_subcompactionjobinfo_t>?,
) {
}

private fun eventListenerOnSubcompactionCompleted(
    state: COpaquePointer?,
    infoPtr: CPointer<rocksdb_subcompactionjobinfo_t>?,
) {
}


private fun eventListenerOnExternalFileIngested(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_externalfileingestioninfo_t>?
) {
    var db: RocksDB? = null
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val info = infoPtr?.let(::ExternalFileIngestionInfo) ?: return
        db = dbPtr?.let(::wrapDb) ?: return
        listener.onExternalFileIngested(db, info)
    } catch (_: Throwable) {
    } finally {
        db?.closeNonOwningReferences()
    }
}

private fun eventListenerOnBackgroundError(
    state: COpaquePointer?,
    reasonValue: UInt,
    errorMessage: CPointer<ByteVar>?,
) {
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val reason = backgroundErrorReasonFromValue(reasonValue)
        val status = decodeBackgroundErrorStatus(errorMessage)
        listener.onBackgroundErrorEvent(reason, status)
    } catch (_: Throwable) {
    }
}

internal fun decodeBackgroundErrorStatus(errorMessage: CPointer<ByteVar>?): Status? =
    errorMessage?.toKString()?.let(::convertToStatus)

private fun eventListenerOnStallConditionsChanged(
    state: COpaquePointer?,
    infoPtr: CPointer<rocksdb_writestallinfo_t>?,
) {
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val info = infoPtr?.let(::WriteStallInfo) ?: return
        listener.onStallConditionsChanged(info)
    } catch (_: Throwable) {
    }
}

private fun eventListenerOnMemTableSealed(
    state: COpaquePointer?,
    infoPtr: CPointer<rocksdb_memtableinfo_t>?,
) {
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val info = infoPtr?.let(::MemTableInfo) ?: return
        listener.onMemTableSealed(info)
    } catch (_: Throwable) {
    }
}

private fun wrapDb(native: CPointer<rocksdb_t>): RocksDB {
    val db = RocksDB(native)
    db.disownHandle()
    return db
}

private fun eventListenerShouldNotifyFileIO(state: COpaquePointer?): UByte = try {
    if (state?.asStableRef<EventListener>()?.get()?.shouldBeNotifiedOnFileIO() == true) 1u else 0u
} catch (_: Throwable) { 0u }

private fun eventListenerOnRecoveryBegin(
    state: COpaquePointer?, reason: UInt, status: CPointer<ByteVar>?, length: size_t,
): UByte = try {
    // Java 11.8.1 has only the first four reasons. Do not relabel newer engine reasons.
    if (reason > 3u) {
        1u
    } else {
        val listener = state?.asStableRef<EventListener>()?.get()
        val error = copyEventStatus(status?.toByteArray(length)?.decodeToString() ?: "")
        if (listener == null || listener.onErrorRecoveryBeginEvent(backgroundErrorReasonFromValue(reason), error)) 1u else 0u
    }
} catch (_: Throwable) { 1u }

private fun copyEventStatus(text: String): Status =
    convertToStatus(text) ?: Status(StatusCode.Undefined, StatusSubCode.None, text)

private fun eventInfoBytes(info: CPointer<rocksdb_event_info_t>, field: UInt): ByteArray = memScoped {
    val length = alloc<size_tVar>()
    rocksdb.rocksdb_event_info_string(info, field, length.ptr)?.toByteArray(length.value) ?: ByteArray(0)
}

private fun eventInfoString(info: CPointer<rocksdb_event_info_t>, field: UInt): String =
    eventInfoBytes(info, field).decodeToString()

private fun eventInfoNumber(info: CPointer<rocksdb_event_info_t>, field: UInt): Long =
    rocksdb.rocksdb_event_info_number(info, field).toCheckedLong("event metadata number")

private fun eventCreationReason(info: CPointer<rocksdb_event_info_t>): TableFileCreationReason =
    TableFileCreationReason.entries.getOrElse(eventInfoNumber(info, 1u).toInt()) { TableFileCreationReason.MISC }

private fun eventListenerOnExtendedEvent(
    state: COpaquePointer?, kind: UByte, info: CPointer<rocksdb_event_info_t>?,
) {
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        if (info == null) return
        when (kind.toInt()) {
            0 -> listener.onTableFileCreationStartedEvent(TableFileCreationBriefInfo(
                eventInfoString(info, 0u), eventInfoString(info, 1u), eventInfoString(info, 2u),
                rocksdb.rocksdb_event_info_number(info, 0u).toInt(), eventCreationReason(info),
            ))
            1 -> listener.onTableFileCreatedEvent(TableFileCreationInfo(
                eventInfoString(info, 0u), eventInfoString(info, 1u), eventInfoString(info, 2u),
                rocksdb.rocksdb_event_info_number(info, 0u).toInt(), eventCreationReason(info), eventInfoNumber(info, 2u),
                TableProperties(requireNotNull(rocksdb.rocksdb_event_info_table_properties(info))),
                copyEventStatus(eventInfoString(info, 3u)),
            ))
            2 -> listener.onTableFileDeletedEvent(TableFileDeletionInfo(
                eventInfoString(info, 0u), eventInfoString(info, 2u),
                rocksdb.rocksdb_event_info_number(info, 0u).toInt(), copyEventStatus(eventInfoString(info, 3u)),
            ))
            3 -> listener.onColumnFamilyHandleDeletionStartedEvent()
            in 4..10 -> {
                val hasRange = kind.toInt() in listOf(4, 5, 8)
                val operation = FileOperationInfo(
                    eventInfoString(info, 2u), if (hasRange) eventInfoNumber(info, 3u) else 0,
                    if (hasRange) eventInfoNumber(info, 4u) else 0,
                    eventInfoNumber(info, 5u), eventInfoNumber(info, 6u), copyEventStatus(eventInfoString(info, 3u)),
                )
                when (kind.toInt()) {
                    4 -> listener.onFileReadFinishEvent(operation)
                    5 -> listener.onFileWriteFinishEvent(operation)
                    6 -> listener.onFileFlushFinishEvent(operation)
                    7 -> listener.onFileSyncFinishEvent(operation)
                    8 -> listener.onFileRangeSyncFinishEvent(operation)
                    9 -> listener.onFileTruncateFinishEvent(operation)
                    10 -> listener.onFileCloseFinishEvent(operation)
                }
            }
            11 -> listener.onErrorRecoveryCompletedEvent(copyEventStatus(eventInfoString(info, 3u)))
        }
    } catch (_: Throwable) {
        // C++ callbacks cannot propagate Kotlin exceptions across the ABI boundary.
    }
}
