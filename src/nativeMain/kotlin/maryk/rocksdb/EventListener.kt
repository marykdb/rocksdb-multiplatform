package maryk.rocksdb

import cnames.structs.rocksdb_compactionjobinfo_t
import cnames.structs.rocksdb_eventlistener_t
import cnames.structs.rocksdb_externalfileingestioninfo_t
import cnames.structs.rocksdb_flushjobinfo_t
import cnames.structs.rocksdb_memtableinfo_t
import cnames.structs.rocksdb_status_ptr_t
import cnames.structs.rocksdb_subcompactionjobinfo_t
import cnames.structs.rocksdb_t
import cnames.structs.rocksdb_writestallinfo_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import maryk.convertToStatus
import rocksdb.rocksdb_eventlistener_create
import rocksdb.rocksdb_eventlistener_destroy
import rocksdb.rocksdb_free
import rocksdb.rocksdb_status_ptr_get_error

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
    statusPointer: CPointer<ByteVar>?,
) {
    try {
        val listener = state?.asStableRef<EventListener>()?.get() ?: return
        val reason = backgroundErrorReasonFromValue(reasonValue)
        val status = statusPointer?.let(::backgroundErrorStatus)
        listener.onBackgroundErrorEvent(reason, status)
    } catch (_: Throwable) {
    }
}

private fun backgroundErrorStatus(statusPointer: CPointer<ByteVar>): Status? = memScoped {
    val errorRef = alloc<CPointerVar<ByteVar>>()
    errorRef.value = null
    rocksdb_status_ptr_get_error(statusPointer.reinterpret<rocksdb_status_ptr_t>(), errorRef.ptr)
    val errorPtr = errorRef.value ?: return@memScoped null
    try {
        convertToStatus(errorPtr.toKString())
    } finally {
        rocksdb_free(errorPtr)
    }
}

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
