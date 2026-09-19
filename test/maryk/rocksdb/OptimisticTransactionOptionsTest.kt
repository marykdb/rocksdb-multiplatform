package maryk.rocksdb

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
}
