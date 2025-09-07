package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class ColumnFamilyOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun writeBufferSize() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setWriteBufferSize(longValue)
            assertEquals(longValue, opt.writeBufferSize())
        }
    }

    @Test
    fun maxWriteBufferNumber() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxWriteBufferNumber(intValue)
            assertEquals(intValue, opt.maxWriteBufferNumber())
        }
    }

    @Test
    fun minWriteBufferNumberToMerge() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setMinWriteBufferNumberToMerge(intValue)
            assertEquals(intValue, opt.minWriteBufferNumberToMerge())
        }
    }

    @Test
    fun numLevels() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setNumLevels(intValue)
            assertEquals(intValue, opt.numLevels())
        }
    }

    @Test
    fun targetFileSizeBase() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setTargetFileSizeBase(longValue)
            assertEquals(longValue, opt.targetFileSizeBase())
        }
    }

    @Test
    fun targetFileSizeMultiplier() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setTargetFileSizeMultiplier(intValue)
            assertEquals(intValue, opt.targetFileSizeMultiplier())
        }
    }

    @Test
    fun maxBytesForLevelBase() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxBytesForLevelBase(longValue)
            assertEquals(longValue, opt.maxBytesForLevelBase())
        }
    }

    @Test
    fun maxBytesForLevelMultiplier() {
        ColumnFamilyOptions().use { opt ->
            val doubleValue = Random.nextDouble()
            opt.setMaxBytesForLevelMultiplier(doubleValue)
            assertEquals(doubleValue, opt.maxBytesForLevelMultiplier())
        }
    }

    @Test
    fun level0FileNumCompactionTrigger() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevel0FileNumCompactionTrigger(intValue)
            assertEquals(intValue, opt.level0FileNumCompactionTrigger())
        }
    }

    @Test
    fun level0SlowdownWritesTrigger() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevel0SlowdownWritesTrigger(intValue)
            assertEquals(intValue, opt.level0SlowdownWritesTrigger())
        }
    }

    @Test
    fun level0StopWritesTrigger() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevel0StopWritesTrigger(intValue)
            assertEquals(intValue, opt.level0StopWritesTrigger())
        }
    }

    @Test
    fun arenaBlockSize() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setArenaBlockSize(longValue)
            assertEquals(longValue, opt.arenaBlockSize())
        }
    }

    @Test
    fun disableAutoCompactions() {
        ColumnFamilyOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setDisableAutoCompactions(boolValue)
            assertEquals(boolValue, opt.disableAutoCompactions())
        }
    }

    @Test
    fun maxSequentialSkipInIterations() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxSequentialSkipInIterations(longValue)
            assertEquals(longValue, opt.maxSequentialSkipInIterations())
        }
    }

    @Test
    fun memtablePrefixBloomSizeRatio() {
        ColumnFamilyOptions().use { opt ->
            val doubleValue = Random.nextDouble()
            opt.setMemtablePrefixBloomSizeRatio(doubleValue)
            assertEquals(doubleValue, opt.memtablePrefixBloomSizeRatio())
        }
    }

    @Test
    fun memtableHugePageSize() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMemtableHugePageSize(longValue)
            assertEquals(longValue, opt.memtableHugePageSize())
        }
    }

    @Test
    fun bloomLocality() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setBloomLocality(intValue)
            assertEquals(intValue, opt.bloomLocality())
        }
    }

    @Test
    fun maxSuccessiveMerges() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxSuccessiveMerges(longValue)
            assertEquals(longValue, opt.maxSuccessiveMerges())
        }
    }

    @Test
    fun comparator() {
        ColumnFamilyOptions().use { opt -> opt.setComparator(BuiltinComparator.BYTEWISE_COMPARATOR) }
    }

    @Test
    fun shouldSetTestPrefixExtractor() {
        ColumnFamilyOptions().use { options ->
            options.useFixedLengthPrefixExtractor(100)
            options.useFixedLengthPrefixExtractor(10)
        }
    }

    @Test
    fun compressionTypes() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            for (compressionType in CompressionType.entries) {
                columnFamilyOptions.setCompressionType(compressionType)
                assertEquals(compressionType, columnFamilyOptions.compressionType())
                assertEquals(CompressionType.NO_COMPRESSION, CompressionType.valueOf("NO_COMPRESSION"))
            }
        }
    }


    @Test
    fun compactionStyles() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            for (compactionStyle in CompactionStyle.entries) {
                columnFamilyOptions.setCompactionStyle(compactionStyle)
                assertEquals(compactionStyle, columnFamilyOptions.compactionStyle())
                assertEquals(CompactionStyle.FIFO, CompactionStyle.valueOf("FIFO"))
            }
        }
    }
}
