package maryk.rocksdb

actual class RestoreOptions actual constructor(
    val keepLogFiles: Boolean
) : RocksObject()
