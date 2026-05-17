package maryk.rocksdb

import cnames.structs.rocksdb_compactionjobinfo_t
import cnames.structs.rocksdb_eventlistener_t
import cnames.structs.rocksdb_externalfileingestioninfo_t
import cnames.structs.rocksdb_flushjobinfo_t
import cnames.structs.rocksdb_memtableinfo_t
import cnames.structs.rocksdb_subcompactionjobinfo_t
import cnames.structs.rocksdb_t
import cnames.structs.rocksdb_writestallinfo_t
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlinx.cinterop.ByteVar
import maryk.convertToStatus
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

    override fun close() {
        if (tryClose()) {
            rocksdb_eventlistener_destroy(native)
            super.close()
        }
    }
}

private fun eventListenerDestructor(state: COpaquePointer?) {
    state?.asStableRef<EventListener>()?.dispose()
}

private fun eventListenerOnFlushBegin(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_flushjobinfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val info = infoPtr?.let(::FlushJobInfo) ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    try {
        try {
            listener.onFlushBeginEvent(db, info)
        } catch (_: Throwable) {
        }
    } finally {
        db.closeNonOwningReferences()
    }
}

private fun eventListenerOnFlushCompleted(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_flushjobinfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val info = infoPtr?.let(::FlushJobInfo) ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    try {
        try {
            listener.onFlushCompletedEvent(db, info)
        } catch (_: Throwable) {
        }
    } finally {
        db.closeNonOwningReferences()
    }
}

private fun eventListenerOnCompactionBegin(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_compactionjobinfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val info = infoPtr?.let(::CompactionJobInfo) ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    try {
        try {
            listener.onCompactionBeginEvent(db, info)
        } catch (_: Throwable) {
        }
    } finally {
        db.closeNonOwningReferences()
    }
}

private fun eventListenerOnCompactionCompleted(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_compactionjobinfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val info = infoPtr?.let(::CompactionJobInfo) ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    try {
        try {
            listener.onCompactionCompletedEvent(db, info)
        } catch (_: Throwable) {
        }
    } finally {
        db.closeNonOwningReferences()
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
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val info = infoPtr?.let(::ExternalFileIngestionInfo) ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    try {
        try {
            listener.onExternalFileIngested(db, info)
        } catch (_: Throwable) {
        }
    } finally {
        db.closeNonOwningReferences()
    }
}

private fun eventListenerOnBackgroundError(
    state: COpaquePointer?,
    reasonValue: UInt,
    statusPointer: CPointer<ByteVar>?,
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val reason = backgroundErrorReasonFromValue(reasonValue)
    val status = statusPointer?.toKString()?.let(::convertToStatus)
    try {
        listener.onBackgroundErrorEvent(reason, status)
    } catch (_: Throwable) {
    }
}

private fun eventListenerOnStallConditionsChanged(
    state: COpaquePointer?,
    infoPtr: CPointer<rocksdb_writestallinfo_t>?,
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val info = infoPtr?.let(::WriteStallInfo) ?: return
    try {
        listener.onStallConditionsChanged(info)
    } catch (_: Throwable) {
    }
}

private fun eventListenerOnMemTableSealed(
    state: COpaquePointer?,
    infoPtr: CPointer<rocksdb_memtableinfo_t>?,
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val info = infoPtr?.let(::MemTableInfo) ?: return
    try {
        listener.onMemTableSealed(info)
    } catch (_: Throwable) {
    }
}

private fun wrapDb(native: CPointer<rocksdb_t>): RocksDB {
    val db = RocksDB(native)
    db.disownHandle()
    return db
}
