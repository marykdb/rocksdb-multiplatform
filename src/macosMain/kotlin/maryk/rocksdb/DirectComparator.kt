package maryk.rocksdb

import rocksdb.RocksDBComparator

actual abstract class DirectComparator internal constructor() : AbstractComparator<DirectSlice>() {
    @Suppress("LeakingThis")
    override val native = RocksDBComparator(name()) { a, b ->
        this.compare(DirectSlice(a!!), DirectSlice(b!!))
    }

    actual constructor(copt: ComparatorOptions) : this()
}
