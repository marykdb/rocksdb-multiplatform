package maryk.rocksdb

import cnames.structs.rocksdb_ingestexternalfileoptions_t
import kotlinx.cinterop.CPointer
import maryk.toUByte
import rocksdb.rocksdb_ingestexternalfileoptions_create
import rocksdb.rocksdb_ingestexternalfileoptions_destroy
import rocksdb.rocksdb_ingestexternalfileoptions_set_allow_blocking_flush
import rocksdb.rocksdb_ingestexternalfileoptions_set_allow_global_seqno
import rocksdb.rocksdb_ingestexternalfileoptions_set_ingest_behind
import rocksdb.rocksdb_ingestexternalfileoptions_set_move_files
import rocksdb.rocksdb_ingestexternalfileoptions_set_snapshot_consistency

actual class IngestExternalFileOptions actual constructor() : RocksObject() {
    internal val native: CPointer<rocksdb_ingestexternalfileoptions_t> =
        rocksdb_ingestexternalfileoptions_create()!!

    private var moveFiles = false
    private var snapshotConsistency = true
    private var allowGlobalSeqNo = true
    private var allowBlockingFlush = true
    private var ingestBehind = false

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_ingestexternalfileoptions_destroy(native)
            super.close()
        }
    }

    actual fun setMoveFiles(moveFiles: Boolean): IngestExternalFileOptions {
        rocksdb_ingestexternalfileoptions_set_move_files(native, moveFiles.toUByte())
        this.moveFiles = moveFiles
        return this
    }

    actual fun moveFiles(): Boolean = moveFiles

    actual fun setSnapshotConsistency(snapshotConsistency: Boolean): IngestExternalFileOptions {
        rocksdb_ingestexternalfileoptions_set_snapshot_consistency(native, snapshotConsistency.toUByte())
        this.snapshotConsistency = snapshotConsistency
        return this
    }

    actual fun snapshotConsistency(): Boolean = snapshotConsistency

    actual fun setAllowGlobalSeqNo(allowGlobalSeqNo: Boolean): IngestExternalFileOptions {
        rocksdb_ingestexternalfileoptions_set_allow_global_seqno(native, allowGlobalSeqNo.toUByte())
        this.allowGlobalSeqNo = allowGlobalSeqNo
        return this
    }

    actual fun allowGlobalSeqNo(): Boolean = allowGlobalSeqNo

    actual fun setAllowBlockingFlush(allowBlockingFlush: Boolean): IngestExternalFileOptions {
        rocksdb_ingestexternalfileoptions_set_allow_blocking_flush(native, allowBlockingFlush.toUByte())
        this.allowBlockingFlush = allowBlockingFlush
        return this
    }

    actual fun allowBlockingFlush(): Boolean = allowBlockingFlush

    actual fun setIngestBehind(ingestBehind: Boolean): IngestExternalFileOptions {
        rocksdb_ingestexternalfileoptions_set_ingest_behind(native, ingestBehind.toUByte())
        this.ingestBehind = ingestBehind
        return this
    }

    actual fun ingestBehind(): Boolean = ingestBehind

}
