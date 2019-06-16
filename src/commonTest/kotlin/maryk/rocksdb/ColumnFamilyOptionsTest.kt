package maryk.rocksdb

import maryk.assertContainsExactly
import maryk.assertContentEquals
import maryk.rocksdb.test.RemoveEmptyValueCompactionFilterFactory
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColumnFamilyOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun copyConstructor() {
        val origOpts = ColumnFamilyOptions()
        origOpts.setNumLevels(Random.nextInt(8))
        origOpts.setTargetFileSizeMultiplier(Random.nextInt(100))
        origOpts.setLevel0StopWritesTrigger(Random.nextInt(50))
        val copyOpts = ColumnFamilyOptions(origOpts)
        assertEquals(copyOpts.numLevels(), origOpts.numLevels())
        assertEquals(copyOpts.targetFileSizeMultiplier(), origOpts.targetFileSizeMultiplier())
        assertEquals(copyOpts.level0StopWritesTrigger(), origOpts.level0StopWritesTrigger())
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
    fun levelZeroFileNumCompactionTrigger() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevelZeroFileNumCompactionTrigger(intValue)
            assertEquals(intValue, opt.levelZeroFileNumCompactionTrigger())
        }
    }

    @Test
    fun levelZeroSlowdownWritesTrigger() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevelZeroSlowdownWritesTrigger(intValue)
            assertEquals(intValue, opt.levelZeroSlowdownWritesTrigger())
        }
    }

    @Test
    fun levelZeroStopWritesTrigger() {
        ColumnFamilyOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevelZeroStopWritesTrigger(intValue)
            assertEquals(intValue, opt.levelZeroStopWritesTrigger())
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
    fun levelCompactionDynamicLevelBytes() {
        ColumnFamilyOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setLevelCompactionDynamicLevelBytes(boolValue)
            assertEquals(boolValue, opt.levelCompactionDynamicLevelBytes())
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
    fun maxBytesForLevelMultiplierAdditional() {
        ColumnFamilyOptions().use { opt ->
            val intValue1 = Random.nextInt()
            val intValue2 = Random.nextInt()
            val ints = intArrayOf(intValue1, intValue2)
            opt.setMaxBytesForLevelMultiplierAdditional(ints)
            assertContentEquals(ints, opt.maxBytesForLevelMultiplierAdditional())
        }
    }

    @Test
    fun maxCompactionBytes() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxCompactionBytes(longValue)
            assertEquals(longValue, opt.maxCompactionBytes())
        }
    }

    @Test
    fun softPendingCompactionBytesLimit() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setSoftPendingCompactionBytesLimit(longValue)
            assertEquals(longValue, opt.softPendingCompactionBytesLimit())
        }
    }

    @Test
    fun hardPendingCompactionBytesLimit() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setHardPendingCompactionBytesLimit(longValue)
            assertEquals(longValue, opt.hardPendingCompactionBytesLimit())
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
    fun inplaceUpdateSupport() {
        ColumnFamilyOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setInplaceUpdateSupport(boolValue)
            assertEquals(boolValue, opt.inplaceUpdateSupport())
        }
    }

    @Test
    fun inplaceUpdateNumLocks() {
        ColumnFamilyOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setInplaceUpdateNumLocks(longValue)
            assertEquals(longValue, opt.inplaceUpdateNumLocks())
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
    fun optimizeFiltersForHits() {
        ColumnFamilyOptions().use { opt ->
            val aBoolean = Random.nextBoolean()
            opt.setOptimizeFiltersForHits(aBoolean)
            assertEquals(aBoolean, opt.optimizeFiltersForHits())
        }
    }

    @Test
    fun memTable() {
        ColumnFamilyOptions().use { opt ->
            opt.setMemTableConfig(HashLinkedListMemTableConfig())
            assertEquals("HashLinkedListRepFactory", opt.memTableFactoryName())
        }
    }

    @Test
    fun comparator() {
        ColumnFamilyOptions().use { opt -> opt.setComparator(BuiltinComparator.BYTEWISE_COMPARATOR) }
    }

    @Test
    fun linkageOfPrepMethods() {
        ColumnFamilyOptions().use { options ->
            options.optimizeUniversalStyleCompaction()
            options.optimizeUniversalStyleCompaction(4000)
            options.optimizeLevelStyleCompaction()
            options.optimizeLevelStyleCompaction(3000)
            options.optimizeForPointLookup(10)
            options.optimizeForSmallDb()
        }
    }

    @Test
    fun shouldSetTestPrefixExtractor() {
        ColumnFamilyOptions().use { options ->
            options.useFixedLengthPrefixExtractor(100)
            options.useFixedLengthPrefixExtractor(10)
        }
    }

    @Test
    fun shouldSetTestCappedPrefixExtractor() {
        ColumnFamilyOptions().use { options ->
            options.useCappedPrefixExtractor(100)
            options.useCappedPrefixExtractor(10)
        }
    }

    @Test
    fun compressionTypes() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            for (compressionType in CompressionType.values()) {
                columnFamilyOptions.setCompressionType(compressionType)
                assertEquals(compressionType, columnFamilyOptions.compressionType())
                assertEquals(CompressionType.NO_COMPRESSION, CompressionType.valueOf("NO_COMPRESSION"))
            }
        }
    }

    @Test
    fun compressionPerLevel() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            assertTrue(columnFamilyOptions.compressionPerLevel().isEmpty())
            val compressionTypeList = mutableListOf<CompressionType>()
            for (i in 0 until columnFamilyOptions.numLevels()) {
                compressionTypeList.add(CompressionType.NO_COMPRESSION)
            }
            columnFamilyOptions.setCompressionPerLevel(compressionTypeList)
            val compressionTypeList2 = columnFamilyOptions.compressionPerLevel()
            for (compressionType in compressionTypeList2) {
                assertEquals(CompressionType.NO_COMPRESSION, compressionType)
            }
        }
    }

    @Test
    fun differentCompressionsPerLevel() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            columnFamilyOptions.setNumLevels(3)

            assertTrue(columnFamilyOptions.compressionPerLevel().isEmpty())
            val compressionTypeList = mutableListOf<CompressionType>()

            compressionTypeList.add(CompressionType.BZLIB2_COMPRESSION)
            compressionTypeList.add(CompressionType.SNAPPY_COMPRESSION)
            compressionTypeList.add(CompressionType.LZ4_COMPRESSION)

            columnFamilyOptions.setCompressionPerLevel(compressionTypeList)
            val compressionTypeList2 = columnFamilyOptions.compressionPerLevel()

            assertEquals(3, compressionTypeList2.size)
            assertContainsExactly(compressionTypeList2,
                CompressionType.BZLIB2_COMPRESSION,
                CompressionType.SNAPPY_COMPRESSION,
                CompressionType.LZ4_COMPRESSION
            )
        }
    }

    @Test
    fun bottommostCompressionType() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            assertEquals(CompressionType.DISABLE_COMPRESSION_OPTION, columnFamilyOptions.bottommostCompressionType())

            for (compressionType in CompressionType.values()) {
                columnFamilyOptions.setBottommostCompressionType(compressionType)
                assertEquals(compressionType, columnFamilyOptions.bottommostCompressionType())
            }
        }
    }

    @Test
    fun bottommostCompressionOptions() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            CompressionOptions()
                .setMaxDictBytes(123).use { bottommostCompressionOptions ->

                    columnFamilyOptions.setBottommostCompressionOptions(
                        bottommostCompressionOptions
                    )
                    assertEquals(bottommostCompressionOptions, columnFamilyOptions.bottommostCompressionOptions())
                    assertEquals(123, columnFamilyOptions.bottommostCompressionOptions().maxDictBytes())
                }
        }
    }

    @Test
    fun compressionOptions() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            CompressionOptions()
                .setMaxDictBytes(123).use { compressionOptions ->
                    columnFamilyOptions.setCompressionOptions(compressionOptions)
                    assertEquals(compressionOptions, columnFamilyOptions.compressionOptions())
                    assertEquals(123, columnFamilyOptions.compressionOptions().maxDictBytes())
                }
        }
    }

    @Test
    fun compactionStyles() {
        ColumnFamilyOptions().use { columnFamilyOptions ->
            for (compactionStyle in CompactionStyle.values()) {
                columnFamilyOptions.setCompactionStyle(compactionStyle)
                assertEquals(compactionStyle, columnFamilyOptions.compactionStyle())
                assertEquals(CompactionStyle.FIFO, CompactionStyle.valueOf("FIFO"))
            }
        }
    }

    @Test
    fun maxTableFilesSizeFIFO() {
        ColumnFamilyOptions().use { opt ->
            var longValue = Random.nextLong()
            // Size has to be positive
            longValue = if (longValue < 0) -longValue else longValue
            longValue = if (longValue == 0L) longValue + 1 else longValue
            opt.setMaxTableFilesSizeFIFO(longValue)
            assertEquals(longValue, opt.maxTableFilesSizeFIFO())
        }
    }

    @Test
    fun maxWriteBufferNumberToMaintain() {
        ColumnFamilyOptions().use { opt ->
            var intValue = Random.nextInt()
            // Size has to be positive
            intValue = if (intValue < 0) -intValue else intValue
            intValue = if (intValue == 0) intValue + 1 else intValue
            opt.setMaxWriteBufferNumberToMaintain(intValue)
            assertEquals(intValue, opt.maxWriteBufferNumberToMaintain())
        }
    }

    @Test
    fun compactionPriorities() {
        ColumnFamilyOptions().use { opt ->
            for (compactionPriority in CompactionPriority.values()) {
                opt.setCompactionPriority(compactionPriority)
                assertEquals(compactionPriority, opt.compactionPriority())
            }
        }
    }

    @Test
    fun reportBgIoStats() {
        ColumnFamilyOptions().use { opt ->
            val booleanValue = true
            opt.setReportBgIoStats(booleanValue)
            assertEquals(booleanValue, opt.reportBgIoStats())
        }
    }

    @Test
    fun ttl() {
        ColumnFamilyOptions().use { options ->
            options.setTtl((1000 * 60).toLong())
            assertEquals((1000 * 60).toLong(), options.ttl())
        }
    }

    @Test
    fun compactionOptionsUniversal() {
        ColumnFamilyOptions().use { opt ->
            CompactionOptionsUniversal()
                .setCompressionSizePercent(7).use { optUni ->
                    opt.setCompactionOptionsUniversal(optUni)
                    assertEquals(optUni, opt.compactionOptionsUniversal())
                    assertEquals(7, opt.compactionOptionsUniversal().compressionSizePercent())
                }
        }
    }

    @Test
    fun compactionOptionsFIFO() {
        ColumnFamilyOptions().use { opt ->
            CompactionOptionsFIFO()
                .setMaxTableFilesSize(2000).use { optFifo ->
                    opt.setCompactionOptionsFIFO(optFifo)
                    assertEquals(optFifo, opt.compactionOptionsFIFO())
                    assertEquals(2000, opt.compactionOptionsFIFO().maxTableFilesSize())
                }
        }
    }

    @Test
    fun forceConsistencyChecks() {
        ColumnFamilyOptions().use { opt ->
            val booleanValue = true
            opt.setForceConsistencyChecks(booleanValue)
            assertEquals(booleanValue, opt.forceConsistencyChecks())
        }
    }

    @Test
    fun compactionFilter() {
        ColumnFamilyOptions().use { options ->
            RemoveEmptyValueCompactionFilter().use { cf ->
                options.setCompactionFilter(cf)
                assertEquals(cf, options.compactionFilter())
            }
        }
    }

    @Test
    fun compactionFilterFactory() {
        ColumnFamilyOptions().use { options ->
            RemoveEmptyValueCompactionFilterFactory().use { cff ->
                options.setCompactionFilterFactory(cff)
                assertEquals(cff, options.compactionFilterFactory())
            }
        }
    }
}
