package maryk.rocksdb

actual abstract class RocksMutableObject : AbstractNativeReference() {
    protected abstract fun disposeInternal()
    actual override final fun close() {
        disposeInternal()
    }
}
