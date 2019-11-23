package maryk.rocksdb

import rocksdb.RocksDBComparator

actual abstract class Comparator internal constructor() : AbstractComparator<Slice>() {
    @Suppress("LeakingThis")
    override val native = RocksDBComparator(name()) { a, b ->
        this.compare(Slice(a!!), Slice(b!!))
    }

    actual constructor(copt: ComparatorOptions) : this()
}
