package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionDBOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun maxNumLocks() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxNumLocks(longValue)
            assertEquals(longValue, opt.getMaxNumLocks())
        }
    }

    @Test
    fun maxNumStripes() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setNumStripes(longValue)
            assertEquals(longValue, opt.getNumStripes())
        }
    }

    @Test
    fun transactionLockTimeout() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setTransactionLockTimeout(longValue)
            assertEquals(longValue, opt.getTransactionLockTimeout())
        }
    }

    @Test
    fun defaultLockTimeout() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setDefaultLockTimeout(longValue)
            assertEquals(longValue, opt.getDefaultLockTimeout())
        }
    }

    @Test
    fun writePolicy() {
        TransactionDBOptions().use { opt ->
            val writePolicy = TxnDBWritePolicy.WRITE_UNPREPARED  // non-default
            opt.setWritePolicy(writePolicy)
            assertEquals(writePolicy, opt.getWritePolicy())
        }
    }
}
