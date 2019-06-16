package maryk.rocksdb

actual typealias ThreadStatus = org.rocksdb.ThreadStatus

actual fun microsToString(operationElapsedTime: Long) =
    ThreadStatus.microsToString(operationElapsedTime)
