package maryk.rocksdb

import cnames.structs.rocksdb_compactionjobinfo_t
import cnames.structs.rocksdb_eventlistener_t
import cnames.structs.rocksdb_externalfileingestioninfo_t
import cnames.structs.rocksdb_flushjobinfo_t
import cnames.structs.rocksdb_t
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import rocksdb.rocksdb_eventlistener_create
import rocksdb.rocksdb_eventlistener_destroy

actual abstract class EventListener : RocksCallbackObject() {
    internal val native: CPointer<rocksdb_eventlistener_t>
    private val stableRef = StableRef.create(this)

    init {
        native = rocksdb_eventlistener_create(
            stableRef.asCPointer(),
            staticCFunction(::eventListenerDestructor),
            staticCFunction(::eventListenerOnFlushBegin),
            staticCFunction(::eventListenerOnFlushCompleted),
            staticCFunction(::eventListenerOnCompactionBegin),
            staticCFunction(::eventListenerOnCompactionCompleted),
            null,
            null,
            staticCFunction(::eventListenerOnExternalFileIngested),
            null,
            null,
            null
        ) ?: error("Failed to allocate RocksDB event listener")
    }

    actual open fun onFlushBeginEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {}

    actual open fun onFlushCompletedEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {}

    actual open fun onCompactionBeginEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {}

    actual open fun onCompactionCompletedEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {}

    actual open fun onExternalFileIngested(db: RocksDB, ingestionInfo: ExternalFileIngestionInfo) {}

    override fun close() {
        if (isOwningHandle()) {
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
    val db = dbPtr?.let(::wrapDb) ?: return
    val info = infoPtr?.let(::FlushJobInfo) ?: return
    listener.onFlushBeginEvent(db, info)
    db.close()
}

private fun eventListenerOnFlushCompleted(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_flushjobinfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    val info = infoPtr?.let(::FlushJobInfo) ?: return
    listener.onFlushCompletedEvent(db, info)
    db.close()
}

private fun eventListenerOnCompactionBegin(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_compactionjobinfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    val info = infoPtr?.let(::CompactionJobInfo) ?: return
    listener.onCompactionBeginEvent(db, info)
    db.close()
}

private fun eventListenerOnCompactionCompleted(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_compactionjobinfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    val info = infoPtr?.let(::CompactionJobInfo) ?: return
    listener.onCompactionCompletedEvent(db, info)
    db.close()
}


private fun eventListenerOnExternalFileIngested(
    state: COpaquePointer?,
    dbPtr: CPointer<rocksdb_t>?,
    infoPtr: CPointer<rocksdb_externalfileingestioninfo_t>?
) {
    val listener = state?.asStableRef<EventListener>()?.get() ?: return
    val db = dbPtr?.let(::wrapDb) ?: return
    val info = infoPtr?.let(::ExternalFileIngestionInfo) ?: return
    listener.onExternalFileIngested(db, info)
    db.close()
}

private fun wrapDb(native: CPointer<rocksdb_t>): RocksDB {
    val db = RocksDB(native)
    db.disownHandle()
    return db
}
