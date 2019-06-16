package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun snapshot() {
        TransactionOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setSetSnapshot(boolValue)
            assertEquals(boolValue, opt.isSetSnapshot())
        }
    }

    @Test
    fun deadlockDetect() {
        TransactionOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setDeadlockDetect(boolValue)
            assertEquals(boolValue, opt.isDeadlockDetect())
        }
    }

    @Test
    fun lockTimeout() {
        TransactionOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setLockTimeout(longValue)
            assertEquals(longValue, opt.getLockTimeout())
        }
    }

    @Test
    fun expiration() {
        TransactionOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setExpiration(longValue)
            assertEquals(longValue, opt.getExpiration())
        }
    }

    @Test
    fun deadlockDetectDepth() {
        TransactionOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setDeadlockDetectDepth(longValue)
            assertEquals(longValue, opt.getDeadlockDetectDepth())
        }
    }

    @Test
    fun maxWriteBatchSize() {
        TransactionOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxWriteBatchSize(longValue)
            assertEquals(longValue, opt.getMaxWriteBatchSize())
        }
    }
}
