package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class ComparatorOptions
    actual constructor()
: RocksObject(newComparatorOptions()) {
    actual fun useAdaptiveMutex(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseAdaptiveMutex(useAdaptiveMutex: Boolean): ComparatorOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newComparatorOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
