package maryk.rocksdb

import rocksdb.RocksDBComparatorType
import rocksdb.RocksDBComparatorType.RocksDBComparatorBytewiseDescending
import rocksdb.RocksDBComparatorType.RocksDBComparatorStringCompareAscending

actual enum class BuiltinComparator(
    internal val native: RocksDBComparatorType
) {
    BYTEWISE_COMPARATOR(RocksDBComparatorStringCompareAscending),
    REVERSE_BYTEWISE_COMPARATOR(RocksDBComparatorBytewiseDescending)
}
