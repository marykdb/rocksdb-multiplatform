package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class IngestExternalFileOptions private constructor(nativeHandle: CPointer<*>) : RocksObject(nativeHandle) {
    actual constructor() : this(newIngestExternalFileOptions())

    actual constructor(
        moveFiles: Boolean,
        snapshotConsistency: Boolean,
        allowGlobalSeqNo: Boolean,
        allowBlockingFlush: Boolean
    ) : this(
        newIngestExternalFileOptions(
            moveFiles, snapshotConsistency,
            allowGlobalSeqNo, allowBlockingFlush
        )
    )

    actual fun moveFiles(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMoveFiles(moveFiles: Boolean): IngestExternalFileOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun snapshotConsistency(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSnapshotConsistency(snapshotConsistency: Boolean): IngestExternalFileOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowGlobalSeqNo(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowGlobalSeqNo(allowGlobalSeqNo: Boolean): IngestExternalFileOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowBlockingFlush(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowBlockingFlush(allowBlockingFlush: Boolean): IngestExternalFileOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ingestBehind(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIngestBehind(ingestBehind: Boolean): IngestExternalFileOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeGlobalSeqno(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteGlobalSeqno(writeGlobalSeqno: Boolean): IngestExternalFileOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun newIngestExternalFileOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newIngestExternalFileOptions(
    moveFiles: Boolean,
    snapshotConsistency: Boolean,
    allowGlobalSeqNo: Boolean,
    allowBlockingFlush: Boolean
): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
