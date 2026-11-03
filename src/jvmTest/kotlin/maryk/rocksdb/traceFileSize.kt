package maryk.rocksdb

import java.io.File

internal actual fun traceFileSize(path: String): Long = File(path).length()
