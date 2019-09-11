package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class CompactionJobInfo actual constructor() : RocksObject(newCompactionJobInfo()) {
    actual fun columnFamilyName(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun status(): Status {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun threadId(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun jobId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun baseInputLevel(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun outputLevel(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun inputFiles(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun outputFiles(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tableProperties(): Map<String, TableProperties> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionReason(): CompactionReason {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compression(): CompressionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun stats(): CompactionJobStats? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newCompactionJobInfo(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
