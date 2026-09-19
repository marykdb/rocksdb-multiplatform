package maryk.rocksdb

actual interface DBOptionsInterface<T : DBOptionsInterface<T>> {
    actual fun setEnv(env: Env): T
    actual fun getEnv(): Env
}
