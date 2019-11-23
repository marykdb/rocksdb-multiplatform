package maryk.rocksdb

import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DBOptionsTest {
    @Test
    fun createIfMissing() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setCreateIfMissing(boolValue)
            assertEquals(boolValue, opt.createIfMissing())
        }
    }

    @Test
    fun createMissingColumnFamilies() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setCreateMissingColumnFamilies(boolValue)
            assertEquals(boolValue, opt.createMissingColumnFamilies())
        }
    }

    @Test
    fun errorIfExists() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setErrorIfExists(boolValue)
            assertEquals(boolValue, opt.errorIfExists())
        }
    }

    @Test
    fun paranoidChecks() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setParanoidChecks(boolValue)
            assertEquals(boolValue, opt.paranoidChecks())
        }
    }

    @Test
    fun useFsync() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseFsync(boolValue)
            assertEquals(boolValue, opt.useFsync())
        }
    }

    @Test
    fun maxLogFileSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxLogFileSize(longValue)
            assertEquals(longValue, opt.maxLogFileSize())
        }
    }

    @Test
    fun logFileTimeToRoll() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setLogFileTimeToRoll(longValue)
            assertEquals(longValue, opt.logFileTimeToRoll())
        }
    }

    @Test
    fun keepLogFileNum() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setKeepLogFileNum(longValue)
            assertEquals(longValue, opt.keepLogFileNum())
        }
    }

    @Test
    fun walSizeLimitMB() {
        DBOptions().use { opt ->
            val longValue = Random.nextUInt().toLong()
            opt.setWalSizeLimitMB(longValue)
            assertEquals(longValue, opt.walSizeLimitMB())
        }
    }

    @Test
    fun statistics() {
        DBOptions().use { options ->
            val statistics = options.statistics()
            assertNull(statistics)
        }

        Statistics().use { statistics ->
            Options().setStatistics(statistics).use { options ->
                options.statistics().use { stats ->
                    assertNotNull(stats)
                }
            }
        }
    }
}
