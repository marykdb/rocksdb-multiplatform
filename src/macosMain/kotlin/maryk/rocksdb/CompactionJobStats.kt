package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class CompactionJobStats actual constructor() : RocksObject(newCompactionJobStats()) {
    actual fun reset() {
    }

    actual fun add(compactionJobStats: CompactionJobStats) {
    }

    actual fun elapsedMicros(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numInputRecords(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numInputFiles(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numInputFilesAtOutputLevel(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numOutputRecords(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numOutputFiles(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isManualCompaction(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun totalInputBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun totalOutputBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numRecordsReplaced(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun totalInputRawKeyBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun totalInputRawValueBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numInputDeletionRecords(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numExpiredDeletionRecords(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numCorruptKeys(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun fileWriteNanos(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun fileRangeSyncNanos(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun fileFsyncNanos(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun filePrepareWriteNanos(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun smallestOutputKeyPrefix(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun largestOutputKeyPrefix(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numSingleDelFallthru(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numSingleDelMismatch(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

fun newCompactionJobStats(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
