package maryk.rocksdb

import java.nio.file.Paths

actual typealias DbPath = org.rocksdb.DbPath

@Suppress("FunctionName")
actual fun DbPath(path: String, targetSize: Long) =
    DbPath(Paths.get("/a"), targetSize)
