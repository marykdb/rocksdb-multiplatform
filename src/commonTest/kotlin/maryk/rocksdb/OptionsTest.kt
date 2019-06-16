package maryk.rocksdb

import maryk.assertContainsExactly
import maryk.assertContentEquals
import maryk.rocksdb.CompressionType.BZLIB2_COMPRESSION
import maryk.rocksdb.CompressionType.LZ4_COMPRESSION
import maryk.rocksdb.CompressionType.SNAPPY_COMPRESSION
import maryk.rocksdb.WalProcessingOption.CONTINUE_PROCESSING
import maryk.rocksdb.test.RemoveEmptyValueCompactionFilterFactory
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class OptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun copyConstructor() {
        val origOpts = Options()
        origOpts.setNumLevels(Random.nextInt(8))
        origOpts.setTargetFileSizeMultiplier(Random.nextInt(100))
        origOpts.setLevel0StopWritesTrigger(Random.nextInt(50))
        val copyOpts = Options(origOpts)
        assertEquals(copyOpts.numLevels(), origOpts.numLevels())
        assertEquals(copyOpts.targetFileSizeMultiplier(), origOpts.targetFileSizeMultiplier())
        assertEquals(copyOpts.level0StopWritesTrigger(), origOpts.level0StopWritesTrigger())
    }

    @Test
    fun setIncreaseParallelism() {
        Options().use { opt ->
            val threads = 4
            opt.setIncreaseParallelism(threads)
        }
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
    fun levelZeroFileNumCompactionTrigger() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevelZeroFileNumCompactionTrigger(intValue)
            assertEquals(intValue, opt.levelZeroFileNumCompactionTrigger())
        }
    }

    @Test
    fun levelZeroSlowdownWritesTrigger() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevelZeroSlowdownWritesTrigger(intValue)
            assertEquals(intValue, opt.levelZeroSlowdownWritesTrigger())
        }
    }

    @Test
    fun levelZeroStopWritesTrigger() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setLevelZeroStopWritesTrigger(intValue)
            assertEquals(intValue, opt.levelZeroStopWritesTrigger())
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
    fun levelCompactionDynamicLevelBytes() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setLevelCompactionDynamicLevelBytes(boolValue)
            assertEquals(boolValue, opt.levelCompactionDynamicLevelBytes())
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
    fun maxBytesForLevelMultiplierAdditional() {
        Options().use { opt ->
            val intValue1 = Random.nextInt()
            val intValue2 = Random.nextInt()
            val ints = intArrayOf(intValue1, intValue2)
            opt.setMaxBytesForLevelMultiplierAdditional(ints)
            assertContentEquals(ints, opt.maxBytesForLevelMultiplierAdditional())
        }
    }

    @Test
    fun maxCompactionBytes() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxCompactionBytes(longValue)
            assertEquals(longValue, opt.maxCompactionBytes())
        }
    }

    @Test
    fun softPendingCompactionBytesLimit() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setSoftPendingCompactionBytesLimit(longValue)
            assertEquals(longValue, opt.softPendingCompactionBytesLimit())
        }
    }

    @Test
    fun hardPendingCompactionBytesLimit() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setHardPendingCompactionBytesLimit(longValue)
            assertEquals(longValue, opt.hardPendingCompactionBytesLimit())
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
    fun arenaBlockSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setArenaBlockSize(longValue)
            assertEquals(longValue, opt.arenaBlockSize())
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
    fun maxSequentialSkipInIterations() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxSequentialSkipInIterations(longValue)
            assertEquals(longValue, opt.maxSequentialSkipInIterations())
        }
    }

    @Test
    fun inplaceUpdateSupport() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setInplaceUpdateSupport(boolValue)
            assertEquals(boolValue, opt.inplaceUpdateSupport())
        }
    }

    @Test
    fun inplaceUpdateNumLocks() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setInplaceUpdateNumLocks(longValue)
            assertEquals(longValue, opt.inplaceUpdateNumLocks())
        }
    }

    @Test
    fun memtablePrefixBloomSizeRatio() {
        Options().use { opt ->
            val doubleValue = Random.nextDouble()
            opt.setMemtablePrefixBloomSizeRatio(doubleValue)
            assertEquals(doubleValue, opt.memtablePrefixBloomSizeRatio())
        }
    }

    @Test
    fun memtableHugePageSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMemtableHugePageSize(longValue)
            assertEquals(longValue, opt.memtableHugePageSize())
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
    fun maxSuccessiveMerges() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxSuccessiveMerges(longValue)
            assertEquals(longValue, opt.maxSuccessiveMerges())
        }
    }

    @Test
    fun optimizeFiltersForHits() {
        Options().use { opt ->
            val aBoolean = Random.nextBoolean()
            opt.setOptimizeFiltersForHits(aBoolean)
            assertEquals(aBoolean, opt.optimizeFiltersForHits())
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
    fun maxTotalWalSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxTotalWalSize(longValue)
            assertEquals(longValue, opt.maxTotalWalSize())
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
    fun maxFileOpeningThreads() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxFileOpeningThreads(intValue)
            assertEquals(intValue, opt.maxFileOpeningThreads())
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
    fun dbPaths() {
        val dbPaths = listOf(
            DbPath("/a", 10),
            DbPath("/b", 100),
            DbPath("/c", 1000)
        )

        Options().use { opt ->
            assertEquals(emptyList<Any>(), opt.dbPaths())

            opt.setDbPaths(dbPaths)

            assertEquals(dbPaths, opt.dbPaths())
        }
    }

    @Test
    fun dbLogDir() {
        Options().use { opt ->
            val str = "path/to/DbLogDir"
            opt.setDbLogDir(str)
            assertEquals(str, opt.dbLogDir())
        }
    }

    @Test
    fun walDir() {
        Options().use { opt ->
            val str = "path/to/WalDir"
            opt.setWalDir(str)
            assertEquals(str, opt.walDir())
        }
    }

    @Test
    fun deleteObsoleteFilesPeriodMicros() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setDeleteObsoleteFilesPeriodMicros(longValue)
            assertEquals(longValue, opt.deleteObsoleteFilesPeriodMicros())
        }
    }

    @Test
    fun maxSubcompactions() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxSubcompactions(intValue)
            assertEquals(intValue, opt.maxSubcompactions())
        }
    }

    @Test
    fun maxBackgroundJobs() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxBackgroundJobs(intValue)
            assertEquals(intValue, opt.maxBackgroundJobs())
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
    fun recycleLogFileNum() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setRecycleLogFileNum(longValue)
            assertEquals(longValue, opt.recycleLogFileNum())
        }
    }

    @Test
    fun maxManifestFileSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxManifestFileSize(longValue)
            assertEquals(longValue, opt.maxManifestFileSize())
        }
    }

    @Test
    fun tableCacheNumshardbits() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setTableCacheNumshardbits(intValue)
            assertEquals(intValue, opt.tableCacheNumshardbits())
        }
    }

    @Test
    fun walSizeLimitMB() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setWalSizeLimitMB(longValue)
            assertEquals(longValue, opt.walSizeLimitMB())
        }
    }

    @Test
    fun walTtlSeconds() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setWalTtlSeconds(longValue)
            assertEquals(longValue, opt.walTtlSeconds())
        }
    }

    @Test
    fun manifestPreallocationSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setManifestPreallocationSize(longValue)
            assertEquals(longValue, opt.manifestPreallocationSize())
        }
    }

    @Test
    fun useDirectReads() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseDirectReads(boolValue)
            assertEquals(boolValue, opt.useDirectReads())
        }
    }

    @Test
    fun useDirectIoForFlushAndCompaction() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseDirectIoForFlushAndCompaction(boolValue)
            assertEquals(boolValue, opt.useDirectIoForFlushAndCompaction())
        }
    }

    @Test
    fun allowFAllocate() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowFAllocate(boolValue)
            assertEquals(boolValue, opt.allowFAllocate())
        }
    }

    @Test
    fun allowMmapReads() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowMmapReads(boolValue)
            assertEquals(boolValue, opt.allowMmapReads())
        }
    }

    @Test
    fun allowMmapWrites() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowMmapWrites(boolValue)
            assertEquals(boolValue, opt.allowMmapWrites())
        }
    }

    @Test
    fun isFdCloseOnExec() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setIsFdCloseOnExec(boolValue)
            assertEquals(boolValue, opt.isFdCloseOnExec())
        }
    }

    @Test
    fun statsDumpPeriodSec() {
        Options().use { opt ->
            val intValue = Random.nextInt()
            opt.setStatsDumpPeriodSec(intValue)
            assertEquals(intValue, opt.statsDumpPeriodSec())
        }
    }

    @Test
    fun adviseRandomOnOpen() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAdviseRandomOnOpen(boolValue)
            assertEquals(boolValue, opt.adviseRandomOnOpen())
        }
    }

    @Test
    fun dbWriteBufferSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setDbWriteBufferSize(longValue)
            assertEquals(longValue, opt.dbWriteBufferSize())
        }
    }

    @Test
    fun setWriteBufferManager() {
        Options().use { opt ->
            LRUCache((1 * 1024 * 1024).toLong()).use { cache ->
                WriteBufferManager(2000L, cache).use { writeBufferManager ->
                    opt.setWriteBufferManager(writeBufferManager)
                    assertEquals(writeBufferManager, opt.writeBufferManager())
                }
            }
        }
    }

    @Test
    fun setWriteBufferManagerWithZeroBufferSize() {
        Options().use { opt ->
            LRUCache((1 * 1024 * 1024).toLong()).use { cache ->
                WriteBufferManager(0L, cache).use { writeBufferManager ->
                    opt.setWriteBufferManager(writeBufferManager)
                    assertEquals(writeBufferManager, opt.writeBufferManager())
                }
            }
        }
    }

    @Test
    fun accessHintOnCompactionStart() {
        Options().use { opt ->
            val accessHint = AccessHint.SEQUENTIAL
            opt.setAccessHintOnCompactionStart(accessHint)
            assertEquals(accessHint, opt.accessHintOnCompactionStart())
        }
    }

    @Test
    fun newTableReaderForCompactionInputs() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setNewTableReaderForCompactionInputs(boolValue)
            assertEquals(boolValue, opt.newTableReaderForCompactionInputs())
        }
    }

    @Test
    fun compactionReadaheadSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setCompactionReadaheadSize(longValue)
            assertEquals(longValue, opt.compactionReadaheadSize())
        }
    }

    @Test
    fun randomAccessMaxBufferSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setRandomAccessMaxBufferSize(longValue)
            assertEquals(longValue, opt.randomAccessMaxBufferSize())
        }
    }

    @Test
    fun writableFileMaxBufferSize() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setWritableFileMaxBufferSize(longValue)
            assertEquals(longValue, opt.writableFileMaxBufferSize())
        }
    }

    @Test
    fun useAdaptiveMutex() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseAdaptiveMutex(boolValue)
            assertEquals(boolValue, opt.useAdaptiveMutex())
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
    fun walBytesPerSync() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setWalBytesPerSync(longValue)
            assertEquals(longValue, opt.walBytesPerSync())
        }
    }

    @Test
    fun enableThreadTracking() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setEnableThreadTracking(boolValue)
            assertEquals(boolValue, opt.enableThreadTracking())
        }
    }

    @Test
    fun delayedWriteRate() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setDelayedWriteRate(longValue)
            assertEquals(longValue, opt.delayedWriteRate())
        }
    }

    @Test
    fun enablePipelinedWrite() {
        Options().use { opt ->
            assertFalse(opt.enablePipelinedWrite())
            opt.setEnablePipelinedWrite(true)
            assertTrue(opt.enablePipelinedWrite())
        }
    }

    @Test
    fun allowConcurrentMemtableWrite() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowConcurrentMemtableWrite(boolValue)
            assertEquals(boolValue, opt.allowConcurrentMemtableWrite())
        }
    }

    @Test
    fun enableWriteThreadAdaptiveYield() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setEnableWriteThreadAdaptiveYield(boolValue)
            assertEquals(boolValue, opt.enableWriteThreadAdaptiveYield())
        }
    }

    @Test
    fun writeThreadMaxYieldUsec() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setWriteThreadMaxYieldUsec(longValue)
            assertEquals(longValue, opt.writeThreadMaxYieldUsec())
        }
    }

    @Test
    fun writeThreadSlowYieldUsec() {
        Options().use { opt ->
            val longValue = Random.nextLong()
            opt.setWriteThreadSlowYieldUsec(longValue)
            assertEquals(longValue, opt.writeThreadSlowYieldUsec())
        }
    }

    @Test
    fun skipStatsUpdateOnDbOpen() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setSkipStatsUpdateOnDbOpen(boolValue)
            assertEquals(boolValue, opt.skipStatsUpdateOnDbOpen())
        }
    }

    @Test
    fun walRecoveryMode() {
        Options().use { opt ->
            for (walRecoveryMode in WALRecoveryMode.values()) {
                opt.setWalRecoveryMode(walRecoveryMode)
                assertEquals(walRecoveryMode, opt.walRecoveryMode())
            }
        }
    }

    @Test
    fun allow2pc() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllow2pc(boolValue)
            assertEquals(boolValue, opt.allow2pc())
        }
    }

    @Test
    fun rowCache() {
        Options().use { opt ->
            assertNull(opt.rowCache())

            LRUCache(1000).use { lruCache ->
                opt.setRowCache(lruCache)
                assertEquals(lruCache, opt.rowCache())
            }

            ClockCache(1000).use { clockCache ->
                opt.setRowCache(clockCache)
                assertEquals(clockCache, opt.rowCache())
            }
        }
    }

    @Test
    fun walFilter() {
        Options().use { opt ->
            assertNull(opt.walFilter())

            object : AbstractWalFilter() {
                override fun columnFamilyLogNumberMap(
                    cfLognumber: Map<Int, Long>,
                    cfNameId: Map<String, Int>
                ) {
                    // no-op
                }

                override fun logRecordFound(
                    logNumber: Long,
                    logFileName: String, batch: WriteBatch,
                    newBatch: WriteBatch
                ) = LogRecordFoundResult(
                    CONTINUE_PROCESSING, false
                )

                override fun name(): String {
                    return "test-wal-filter"
                }
            }.use { walFilter ->
                opt.setWalFilter(walFilter)
                assertEquals(walFilter, opt.walFilter())
            }
        }
    }

    @Test
    fun failIfOptionsFileError() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setFailIfOptionsFileError(boolValue)
            assertEquals(boolValue, opt.failIfOptionsFileError())
        }
    }

    @Test
    fun dumpMallocStats() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setDumpMallocStats(boolValue)
            assertEquals(boolValue, opt.dumpMallocStats())
        }
    }

    @Test
    fun avoidFlushDuringRecovery() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAvoidFlushDuringRecovery(boolValue)
            assertEquals(boolValue, opt.avoidFlushDuringRecovery())
        }
    }

    @Test
    fun avoidFlushDuringShutdown() {
        Options().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAvoidFlushDuringShutdown(boolValue)
            assertEquals(boolValue, opt.avoidFlushDuringShutdown())
        }
    }


    @Test
    fun allowIngestBehind() {
        Options().use { opt ->
            assertFalse(opt.allowIngestBehind())
            opt.setAllowIngestBehind(true)
            assertTrue(opt.allowIngestBehind())
        }
    }

    @Test
    fun preserveDeletes() {
        Options().use { opt ->
            assertFalse(opt.preserveDeletes())
            opt.setPreserveDeletes(true)
            assertTrue(opt.preserveDeletes())
        }
    }

    @Test
    fun twoWriteQueues() {
        Options().use { opt ->
            assertFalse(opt.twoWriteQueues())
            opt.setTwoWriteQueues(true)
            assertTrue(opt.twoWriteQueues())
        }
    }

    @Test
    fun manualWalFlush() {
        Options().use { opt ->
            assertFalse(opt.manualWalFlush())
            opt.setManualWalFlush(true)
            assertTrue(opt.manualWalFlush())
        }
    }

    @Test
    fun atomicFlush() {
        Options().use { opt ->
            assertFalse(opt.atomicFlush())
            opt.setAtomicFlush(true)
            assertTrue(opt.atomicFlush())
        }
    }

    @Test
    fun env() {
        Options().use { options ->
            getDefaultEnv().use { env ->
                options.setEnv(env)
                assertSame(options.getEnv(), env)
            }
        }
    }

    @Test
    fun linkageOfPrepMethods() {
        Options().use { options ->
            options.optimizeUniversalStyleCompaction()
            options.optimizeUniversalStyleCompaction(4000)
            options.optimizeLevelStyleCompaction()
            options.optimizeLevelStyleCompaction(3000)
            options.optimizeForPointLookup(10)
            options.optimizeForSmallDb()
            options.prepareForBulkLoad()
        }
    }

    @Test
    fun compressionTypes() {
        Options().use { options ->
            for (compressionType in CompressionType.values()) {
                options.setCompressionType(compressionType)
                assertEquals(compressionType, options.compressionType())
                assertEquals(CompressionType.NO_COMPRESSION, CompressionType.valueOf("NO_COMPRESSION"))
            }
        }
    }

    @Test
    fun compressionPerLevel() {
        Options().use { options ->
            assertTrue(options.compressionPerLevel().isEmpty())
            val compressionTypeList = mutableListOf<CompressionType>()
            for (i in 0 until options.numLevels()) {
                compressionTypeList.add(CompressionType.NO_COMPRESSION)
            }
            options.setCompressionPerLevel(compressionTypeList)
            val compressionTypeList2 = options.compressionPerLevel()
            for (compressionType in compressionTypeList2) {
                assertEquals(CompressionType.NO_COMPRESSION, compressionType)
            }
        }
    }

    @Test
    fun differentCompressionsPerLevel() {
        Options().use { options ->
            options.setNumLevels(3)

            assertTrue(options.compressionPerLevel().isEmpty())
            val compressionTypeList = listOf(
                BZLIB2_COMPRESSION, SNAPPY_COMPRESSION, LZ4_COMPRESSION
            )

            options.setCompressionPerLevel(compressionTypeList)
            val compressionTypeList2 = options.compressionPerLevel()

            assertEquals(3, compressionTypeList2.size)
            assertContainsExactly(
                compressionTypeList2,
                BZLIB2_COMPRESSION,
                SNAPPY_COMPRESSION,
                LZ4_COMPRESSION
            )
        }
    }

    @Test
    fun bottommostCompressionType() {
        Options().use { options ->
            assertEquals(CompressionType.DISABLE_COMPRESSION_OPTION, options.bottommostCompressionType())

            for (compressionType in CompressionType.values()) {
                options.setBottommostCompressionType(compressionType)
                assertEquals(compressionType, options.bottommostCompressionType())
            }
        }
    }

    @Test
    fun bottommostCompressionOptions() {
        Options().use { options ->
            CompressionOptions()
                .setMaxDictBytes(123).use { bottommostCompressionOptions ->
                    options.setBottommostCompressionOptions(bottommostCompressionOptions)
                    assertEquals(bottommostCompressionOptions, options.bottommostCompressionOptions())
                    assertEquals(123, options.bottommostCompressionOptions().maxDictBytes())
                }
        }
    }

    @Test
    fun compressionOptions() {
        Options().use { options ->
            CompressionOptions()
                .setMaxDictBytes(123).use { compressionOptions ->

                    options.setCompressionOptions(compressionOptions)
                    assertEquals(compressionOptions, options.compressionOptions())
                    assertEquals(123, options.compressionOptions().maxDictBytes())
                }
        }
    }

    @Test
    fun compactionStyles() {
        Options().use { options ->
            for (compactionStyle in CompactionStyle.values()) {
                options.setCompactionStyle(compactionStyle)
                assertEquals(compactionStyle, options.compactionStyle())
                assertEquals(CompactionStyle.FIFO, CompactionStyle.valueOf("FIFO"))
            }
        }
    }

    @Test
    fun maxTableFilesSizeFIFO() {
        Options().use { opt ->
            var longValue = Random.nextLong()
            // Size has to be positive
            longValue = if (longValue < 0) -longValue else longValue
            longValue = if (longValue == 0L) longValue + 1 else longValue
            opt.setMaxTableFilesSizeFIFO(longValue)
            assertEquals(longValue, opt.maxTableFilesSizeFIFO())
        }
    }

    @Test
    fun rateLimiter() {
        Options().use { options ->
            Options().use { anotherOptions ->
                RateLimiter(1000, (100 * 1000).toLong(), 1).use { rateLimiter ->
                    options.setRateLimiter(rateLimiter)
                    // Test with parameter initialization
                    anotherOptions.setRateLimiter(
                        RateLimiter(1000)
                    )
                }
            }
        }
    }

    @Test
    fun sstFileManager() {
        Options().use { options ->
            SstFileManager(getDefaultEnv()).use { sstFileManager ->
                options.setSstFileManager(
                    sstFileManager
                )
            }
        }
    }

    @Test
    fun shouldSetTestPrefixExtractor() {
        Options().use { options ->
            options.useFixedLengthPrefixExtractor(100)
            options.useFixedLengthPrefixExtractor(10)
        }
    }

    @Test
    fun shouldSetTestCappedPrefixExtractor() {
        Options().use { options ->
            options.useCappedPrefixExtractor(100)
            options.useCappedPrefixExtractor(10)
        }
    }

    @Test
    fun shouldTestMemTableFactoryName() {
        Options().use { options ->
            options.setMemTableConfig(VectorMemTableConfig())
            assertEquals("VectorRepFactory", options.memTableFactoryName())
            options.setMemTableConfig(
                HashLinkedListMemTableConfig()
            )
            assertEquals("HashLinkedListRepFactory", options.memTableFactoryName())
        }
    }

    @Test
    fun statistics() {
        Options().use { options ->
            val statistics = options.statistics()
            assertNull(statistics)
        }

        Statistics().use { statistics ->
            Options().setStatistics(statistics)
                .use { options -> options.statistics().use { stats -> assertNotNull(stats) } }
        }
    }

    @Test
    fun maxWriteBufferNumberToMaintain() {
        Options().use { options ->
            var intValue = Random.nextInt()
            // Size has to be positive
            intValue = if (intValue < 0) -intValue else intValue
            intValue = if (intValue == 0) intValue + 1 else intValue
            options.setMaxWriteBufferNumberToMaintain(intValue)
            assertEquals(intValue, options.maxWriteBufferNumberToMaintain())
        }
    }

    @Test
    fun compactionPriorities() {
        Options().use { options ->
            for (compactionPriority in CompactionPriority.values()) {
                options.setCompactionPriority(compactionPriority)
                assertEquals(compactionPriority, options.compactionPriority())
            }
        }
    }

    @Test
    fun reportBgIoStats() {
        Options().use { options ->
            val booleanValue = true
            options.setReportBgIoStats(booleanValue)
            assertEquals(booleanValue, options.reportBgIoStats())
        }
    }

    @Test
    fun ttl() {
        Options().use { options ->
            options.setTtl((1000 * 60).toLong())
            assertEquals((1000 * 60).toLong(), options.ttl())
        }
    }

    @Test
    fun compactionOptionsUniversal() {
        Options().use { options ->
            CompactionOptionsUniversal()
                .setCompressionSizePercent(7).use { optUni ->
                    options.setCompactionOptionsUniversal(optUni)
                    assertEquals(optUni, options.compactionOptionsUniversal())
                    assertEquals(7, options.compactionOptionsUniversal().compressionSizePercent())
                }
        }
    }

    @Test
    fun compactionOptionsFIFO() {
        Options().use { options ->
            CompactionOptionsFIFO()
                .setMaxTableFilesSize(2000).use { optFifo ->
                    options.setCompactionOptionsFIFO(optFifo)
                    assertEquals(optFifo, options.compactionOptionsFIFO())
                    assertEquals(2000, options.compactionOptionsFIFO().maxTableFilesSize())
                }
        }
    }

    @Test
    fun forceConsistencyChecks() {
        Options().use { options ->
            val booleanValue = true
            options.setForceConsistencyChecks(booleanValue)
            assertEquals(booleanValue, options.forceConsistencyChecks())
        }
    }

    @Test
    fun compactionFilter() {
        Options().use { options ->
            RemoveEmptyValueCompactionFilter().use { cf ->
                options.setCompactionFilter(cf)
                assertEquals(cf, options.compactionFilter())
            }
        }
    }

    @Test
    fun compactionFilterFactory() {
        Options().use { options ->
            RemoveEmptyValueCompactionFilterFactory().use { cff ->
                options.setCompactionFilterFactory(cff)
                assertEquals(cff, options.compactionFilterFactory())
            }
        }
    }
}
