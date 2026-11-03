package maryk.rocksdb

internal actual fun liveOptionsMaxOpenFiles(options: DBOptions): Int = options.maxOpenFiles()
internal actual fun liveOptionsBytesPerSync(options: DBOptions): Long = options.bytesPerSync()
