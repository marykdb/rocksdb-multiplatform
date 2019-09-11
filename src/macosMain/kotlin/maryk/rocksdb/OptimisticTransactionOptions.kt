package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class OptimisticTransactionOptions : RocksObject(newOptimisticTransactionOptions()) {
    actual fun isSetSnapshot(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSetSnapshot(setSnapshot: Boolean): OptimisticTransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setComparator(comparator: AbstractComparator<out AbstractSlice<*>>): OptimisticTransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newOptimisticTransactionOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
