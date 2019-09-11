package maryk.rocksdb

actual abstract class AbstractNativeReference : AutoCloseable {
    protected actual abstract fun isOwningHandle(): Boolean
}
