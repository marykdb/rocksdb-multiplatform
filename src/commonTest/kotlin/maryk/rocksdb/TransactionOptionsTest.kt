package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionOptionsTest {
    companion object {
        init {
            loadRocksDBLibrary()
        }
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
    fun snapshot() {
        TransactionOptions().use { opt ->
            val boolValue = Random.Default.nextBoolean()
            opt.setSetSnapshot(boolValue)
            assertEquals(boolValue, opt.isSetSnapshot(), "Snapshot should be equal to the set value")
        }
    }

    @Test
    fun deadlockDetect() {
        TransactionOptions().use { opt ->
            val boolValue = Random.Default.nextBoolean()
            opt.setDeadlockDetect(boolValue)
            assertEquals(boolValue, opt.isDeadlockDetect(), "DeadlockDetect should be equal to the set value")
        }
    }

    @Test
    fun lockTimeout() {
        TransactionOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setLockTimeout(longValue)
            assertEquals(longValue, opt.getLockTimeout(), "LockTimeout should be equal to the set value")
        }
    }

    @Test
    fun expiration() {
        TransactionOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setExpiration(longValue)
            assertEquals(longValue, opt.getExpiration(), "Expiration should be equal to the set value")
        }
    }

    @Test
    fun deadlockDetectDepth() {
        TransactionOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setDeadlockDetectDepth(longValue)
            assertEquals(longValue, opt.getDeadlockDetectDepth(), "DeadlockDetectDepth should be equal to the set value")
        }
    }

    @Test
    fun maxWriteBatchSize() {
        TransactionOptions().use { opt ->
            val longValue = Random.Default.nextLong()
            opt.setMaxWriteBatchSize(longValue)
            assertEquals(longValue, opt.getMaxWriteBatchSize(), "MaxWriteBatchSize should be equal to the set value")
        }
    }
}
