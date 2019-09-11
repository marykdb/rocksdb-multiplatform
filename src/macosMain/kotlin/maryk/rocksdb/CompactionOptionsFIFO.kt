package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class CompactionOptionsFIFO actual constructor() : RocksObject(newCompactionOptionsFIFO()) {
    actual fun setMaxTableFilesSize(maxTableFilesSize: Long): CompactionOptionsFIFO {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxTableFilesSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowCompaction(allowCompaction: Boolean): CompactionOptionsFIFO {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowCompaction(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newCompactionOptionsFIFO(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
