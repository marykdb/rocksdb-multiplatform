package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class RestoreOptions actual constructor(keepLogFiles: Boolean)
    : RocksObject(newRestoreOptions(keepLogFiles))

fun newRestoreOptions(keepLogFiles: Boolean): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
