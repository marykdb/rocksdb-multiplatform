package maryk.rocksdb

import maryk.rocksdb.util.DirectBytewiseComparator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class OptimisticTransactionOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun setSnapshot() {
        OptimisticTransactionOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setSetSnapshot(boolValue)
            assertEquals(boolValue, opt.isSetSnapshot())
        }
    }

    @Test
    fun comparator() {
        OptimisticTransactionOptions().use { opt ->
            ComparatorOptions().use { copt ->
                DirectBytewiseComparator(copt).use { comparator ->
                    opt.setComparator(comparator)
                }
            }
        }
    }
}
