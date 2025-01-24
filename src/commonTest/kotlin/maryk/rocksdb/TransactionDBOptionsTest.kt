package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionDBOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @BeforeTest
    fun setup() {
        // Setup code if needed before each test
    }

    @AfterTest
    fun teardown() {
        // Teardown code if needed after each test
    }

    @Test
    fun maxNumLocks() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setMaxNumLocks(longValue)
            assertEquals(longValue, opt.getMaxNumLocks(), "MaxNumLocks should be equal to the set value")
        }
    }

    @Test
    fun maxNumStripes() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setNumStripes(longValue)
            assertEquals(longValue, opt.getNumStripes(), "NumStripes should be equal to the set value")
        }
    }

    @Test
    fun transactionLockTimeout() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setTransactionLockTimeout(longValue)
            assertEquals(longValue, opt.getTransactionLockTimeout(), "TransactionLockTimeout should be equal to the set value")
        }
    }

    @Test
    fun defaultLockTimeout() {
        TransactionDBOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setDefaultLockTimeout(longValue)
            assertEquals(longValue, opt.getDefaultLockTimeout(), "DefaultLockTimeout should be equal to the set value")
        }
    }

    @Test
    fun writePolicy() {
        TransactionDBOptions().use { opt ->
            val writePolicy = TxnDBWritePolicy.WRITE_UNPREPARED // non-default
            opt.setWritePolicy(writePolicy)
            assertEquals(writePolicy, opt.getWritePolicy(), "WritePolicy should be equal to the set value")
        }
    }
}
