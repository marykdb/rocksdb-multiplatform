package maryk.rocksdb

actual typealias ThreadType = org.rocksdb.ThreadType

expect fun getThreadTypeName(threadType: ThreadType): String
