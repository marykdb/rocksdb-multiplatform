package maryk.rocksdb

import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun writeBufferSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setWriteBufferSize(longValue)
            assertEquals(longValue, opt.writeBufferSize())
        }
    }

    @Test
    fun maxWriteBufferNumber() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxWriteBufferNumber(intValue)
            assertEquals(intValue, opt.maxWriteBufferNumber())
        }
    }

    @Test
    fun minWriteBufferNumberToMerge() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setMinWriteBufferNumberToMerge(intValue)
            assertEquals(intValue, opt.minWriteBufferNumberToMerge())
        }
    }

    @Test
    fun numLevels() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setNumLevels(intValue)
            assertEquals(intValue, opt.numLevels())
        }
    }

    @Test
    fun targetFileSizeBase() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setTargetFileSizeBase(longValue)
            assertEquals(longValue, opt.targetFileSizeBase())
        }
    }

    @Test
    fun targetFileSizeMultiplier() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setTargetFileSizeMultiplier(intValue)
            assertEquals(intValue, opt.targetFileSizeMultiplier())
        }
    }

    @Test
    fun maxBytesForLevelBase() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxBytesForLevelBase(longValue)
            assertEquals(longValue, opt.maxBytesForLevelBase())
        }
    }

    @Test
    fun maxBytesForLevelMultiplier() {
        Options().use { opt ->
            val doubleValue = Random.nextDouble()
            opt.setMaxBytesForLevelMultiplier(doubleValue)
            assertEquals(doubleValue, opt.maxBytesForLevelMultiplier())
        }
    }

    @Test
    fun level0FileNumCompactionTrigger() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevel0FileNumCompactionTrigger(intValue)
            assertEquals(intValue, opt.level0FileNumCompactionTrigger())
        }
    }

    @Test
    fun level0SlowdownWritesTrigger() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevel0SlowdownWritesTrigger(intValue)
            assertEquals(intValue, opt.level0SlowdownWritesTrigger())
        }
    }

    @Test
    fun level0StopWritesTrigger() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevel0StopWritesTrigger(intValue)
            assertEquals(intValue, opt.level0StopWritesTrigger())
        }
    }

    @Test
    fun disableAutoCompactions() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setDisableAutoCompactions(boolValue)
            assertEquals(boolValue, opt.disableAutoCompactions())
        }
    }

    @Test
    fun bloomLocality() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setBloomLocality(intValue)
            assertEquals(intValue, opt.bloomLocality())
        }
    }

    @Test
    fun createIfMissing() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setCreateIfMissing(boolValue)
            assertEquals(boolValue, opt.createIfMissing())
        }
    }

    @Test
    fun createMissingColumnFamilies() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setCreateMissingColumnFamilies(boolValue)
            assertEquals(boolValue, opt.createMissingColumnFamilies())
        }
    }

    @Test
    fun errorIfExists() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setErrorIfExists(boolValue)
            assertEquals(boolValue, opt.errorIfExists())
        }
    }

    @Test
    fun paranoidChecks() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setParanoidChecks(boolValue)
            assertEquals(boolValue, opt.paranoidChecks())
        }
    }

    @Test
    fun maxOpenFiles() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxOpenFiles(intValue)
            assertEquals(intValue, opt.maxOpenFiles())
        }
    }

    @Test
    fun useFsync() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseFsync(boolValue)
            assertEquals(boolValue, opt.useFsync())
        }
    }

    @Test
    fun maxLogFileSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxLogFileSize(longValue)
            assertEquals(longValue, opt.maxLogFileSize())
        }
    }

    @Test
    fun logFileTimeToRoll() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setLogFileTimeToRoll(longValue)
            assertEquals(longValue, opt.logFileTimeToRoll())
        }
    }

    @Test
    fun keepLogFileNum() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setKeepLogFileNum(longValue)
            assertEquals(longValue, opt.keepLogFileNum())
        }
    }

    @Test
    fun walSizeLimitMB() {
        Options().use { opt ->
            val longValue = Random.nextUInt().toLong()
            opt.setWalSizeLimitMB(longValue)
            assertEquals(longValue, opt.walSizeLimitMB())
        }
    }

    @Test
    fun bytesPerSync() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setBytesPerSync(longValue)
            assertEquals(longValue, opt.bytesPerSync())
        }
    }

    @Test
    fun compressionTypes() {
        Options().use { options ->
            for (compressionType in CompressionType.entries) {
                options.setCompressionType(compressionType)
                assertEquals(compressionType, options.compressionType())
                assertEquals(CompressionType.NO_COMPRESSION, CompressionType.valueOf("NO_COMPRESSION"))
            }
        }
    }

    @Test
    fun statistics() {
        Options().use { options ->
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
