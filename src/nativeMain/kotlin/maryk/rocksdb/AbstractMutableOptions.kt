package maryk.rocksdb

actual open class AbstractMutableOptions : RocksMutableObject() {
    protected open fun dispose() {}

    final override fun disposeInternal() {
        dispose()
    }
}
