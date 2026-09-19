package maryk.rocksdb

actual typealias Env = org.rocksdb.Env

actual fun getDefaultEnv(): Env = Env.getDefault()
