package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DBOptionsTest {
    @Test
    fun copyConstructor() {
        val origOpts = DBOptions()
        origOpts.setCreateIfMissing(Random.nextBoolean())
        origOpts.setAllow2pc(Random.nextBoolean())
        origOpts.setMaxBackgroundJobs(Random.nextInt(10))
        val copyOpts = DBOptions(origOpts)
        assertEquals(copyOpts.createIfMissing(), origOpts.createIfMissing())
        assertEquals(copyOpts.allow2pc(), origOpts.allow2pc())
        assertEquals(copyOpts.maxBackgroundJobs(), origOpts.maxBackgroundJobs())
    }

    @Test
    fun linkageOfPrepMethods() {
        DBOptions().use { opt -> opt.optimizeForSmallDb() }
    }

    @Test
    fun env() {
        DBOptions().use { opt ->
            getDefaultEnv().use { env ->
                opt.setEnv(env)
                assertSame(env, opt.getEnv())
            }
        }
    }

    @Test
    fun setIncreaseParallelism() {
        DBOptions().use { opt ->
            opt.setIncreaseParallelism(2 * 2)
        }
    }

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
    fun maxTotalWalSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxTotalWalSize(longValue)
            assertEquals(longValue, opt.maxTotalWalSize())
        }
    }

    @Test
    fun maxOpenFiles() {
        DBOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxOpenFiles(intValue)
            assertEquals(intValue, opt.maxOpenFiles())
        }
    }

    @Test
    fun maxFileOpeningThreads() {
        DBOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxFileOpeningThreads(intValue)
            assertEquals(intValue, opt.maxFileOpeningThreads())
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
    fun dbPaths() {
        val dbPaths = listOf(
            DbPath("/a", 10),
            DbPath("/b", 100),
            DbPath("/c", 1000)
        )

        DBOptions().use { opt ->
            assertEquals(emptyList<Any>(), opt.dbPaths())

            opt.setDbPaths(dbPaths)

            assertEquals(dbPaths, opt.dbPaths())
        }
    }

    @Test
    fun dbLogDir() {
        DBOptions().use { opt ->
            val str = "path/to/DbLogDir"
            opt.setDbLogDir(str)
            assertEquals(str, opt.dbLogDir())
        }
    }

    @Test
    fun walDir() {
        DBOptions().use { opt ->
            val str = "path/to/WalDir"
            opt.setWalDir(str)
            assertEquals(str, opt.walDir())
        }
    }

    @Test
    fun deleteObsoleteFilesPeriodMicros() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setDeleteObsoleteFilesPeriodMicros(longValue)
            assertEquals(longValue, opt.deleteObsoleteFilesPeriodMicros())
        }
    }

    @Test
    fun maxSubcompactions() {
        DBOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxSubcompactions(intValue)
            assertEquals(intValue, opt.maxSubcompactions())
        }
    }

    @Test
    fun maxBackgroundJobs() {
        DBOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setMaxBackgroundJobs(intValue)
            assertEquals(intValue, opt.maxBackgroundJobs())
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
    fun recycleLogFileNum() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setRecycleLogFileNum(longValue)
            assertEquals(longValue, opt.recycleLogFileNum())
        }
    }

    @Test
    fun maxManifestFileSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setMaxManifestFileSize(longValue)
            assertEquals(longValue, opt.maxManifestFileSize())
        }
    }

    @Test
    fun tableCacheNumshardbits() {
        DBOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setTableCacheNumshardbits(intValue)
            assertEquals(intValue, opt.tableCacheNumshardbits())
        }
    }

    @Test
    fun walSizeLimitMB() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setWalSizeLimitMB(longValue)
            assertEquals(longValue, opt.walSizeLimitMB())
        }
    }

    @Test
    fun walTtlSeconds() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setWalTtlSeconds(longValue)
            assertEquals(longValue, opt.walTtlSeconds())
        }
    }

    @Test
    fun manifestPreallocationSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setManifestPreallocationSize(longValue)
            assertEquals(longValue, opt.manifestPreallocationSize())
        }
    }

    @Test
    fun useDirectReads() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseDirectReads(boolValue)
            assertEquals(boolValue, opt.useDirectReads())
        }
    }

    @Test
    fun useDirectIoForFlushAndCompaction() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseDirectIoForFlushAndCompaction(boolValue)
            assertEquals(boolValue, opt.useDirectIoForFlushAndCompaction())
        }
    }

    @Test
    fun allowFAllocate() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowFAllocate(boolValue)
            assertEquals(boolValue, opt.allowFAllocate())
        }
    }

    @Test
    fun allowMmapReads() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowMmapReads(boolValue)
            assertEquals(boolValue, opt.allowMmapReads())
        }
    }

    @Test
    fun allowMmapWrites() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowMmapWrites(boolValue)
            assertEquals(boolValue, opt.allowMmapWrites())
        }
    }

    @Test
    fun isFdCloseOnExec() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setIsFdCloseOnExec(boolValue)
            assertEquals(boolValue, opt.isFdCloseOnExec())
        }
    }

    @Test
    fun statsDumpPeriodSec() {
        DBOptions().use { opt ->
            val intValue = Random.nextInt()
            opt.setStatsDumpPeriodSec(intValue)
            assertEquals(intValue, opt.statsDumpPeriodSec())
        }
    }

    @Test
    fun adviseRandomOnOpen() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAdviseRandomOnOpen(boolValue)
            assertEquals(boolValue, opt.adviseRandomOnOpen())
        }
    }

    @Test
    fun dbWriteBufferSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setDbWriteBufferSize(longValue)
            assertEquals(longValue, opt.dbWriteBufferSize())
        }
    }

    @Test
    fun setWriteBufferManager() {
        DBOptions().use { opt ->
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
        DBOptions().use { opt ->
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
        DBOptions().use { opt ->
            val accessHint = AccessHint.SEQUENTIAL
            opt.setAccessHintOnCompactionStart(accessHint)
            assertEquals(accessHint, opt.accessHintOnCompactionStart())
        }
    }

    @Test
    fun newTableReaderForCompactionInputs() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setNewTableReaderForCompactionInputs(boolValue)
            assertEquals(boolValue, opt.newTableReaderForCompactionInputs())
        }
    }

    @Test
    fun compactionReadaheadSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setCompactionReadaheadSize(longValue)
            assertEquals(longValue, opt.compactionReadaheadSize())
        }
    }

    @Test
    fun randomAccessMaxBufferSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setRandomAccessMaxBufferSize(longValue)
            assertEquals(longValue, opt.randomAccessMaxBufferSize())
        }
    }

    @Test
    fun writableFileMaxBufferSize() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setWritableFileMaxBufferSize(longValue)
            assertEquals(longValue, opt.writableFileMaxBufferSize())
        }
    }

    @Test
    fun useAdaptiveMutex() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setUseAdaptiveMutex(boolValue)
            assertEquals(boolValue, opt.useAdaptiveMutex())
        }
    }

    @Test
    fun bytesPerSync() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setBytesPerSync(longValue)
            assertEquals(longValue, opt.bytesPerSync())
        }
    }

    @Test
    fun walBytesPerSync() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setWalBytesPerSync(longValue)
            assertEquals(longValue, opt.walBytesPerSync())
        }
    }

    @Test
    fun enableThreadTracking() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setEnableThreadTracking(boolValue)
            assertEquals(boolValue, opt.enableThreadTracking())
        }
    }

    @Test
    fun delayedWriteRate() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setDelayedWriteRate(longValue)
            assertEquals(longValue, opt.delayedWriteRate())
        }
    }

    @Test
    fun enablePipelinedWrite() {
        DBOptions().use { opt ->
            assertFalse(opt.enablePipelinedWrite())
            opt.setEnablePipelinedWrite(true)
            assertTrue(opt.enablePipelinedWrite())
        }
    }

    @Test
    fun allowConcurrentMemtableWrite() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllowConcurrentMemtableWrite(boolValue)
            assertEquals(boolValue, opt.allowConcurrentMemtableWrite())
        }
    }

    @Test
    fun enableWriteThreadAdaptiveYield() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setEnableWriteThreadAdaptiveYield(boolValue)
            assertEquals(boolValue, opt.enableWriteThreadAdaptiveYield())
        }
    }

    @Test
    fun writeThreadMaxYieldUsec() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setWriteThreadMaxYieldUsec(longValue)
            assertEquals(longValue, opt.writeThreadMaxYieldUsec())
        }
    }

    @Test
    fun writeThreadSlowYieldUsec() {
        DBOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setWriteThreadSlowYieldUsec(longValue)
            assertEquals(longValue, opt.writeThreadSlowYieldUsec())
        }
    }

    @Test
    fun skipStatsUpdateOnDbOpen() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setSkipStatsUpdateOnDbOpen(boolValue)
            assertEquals(boolValue, opt.skipStatsUpdateOnDbOpen())
        }
    }

    @Test
    fun walRecoveryMode() {
        DBOptions().use { opt ->
            for (walRecoveryMode in WALRecoveryMode.values()) {
                opt.setWalRecoveryMode(walRecoveryMode)
                assertEquals(walRecoveryMode, opt.walRecoveryMode())
            }
        }
    }

    @Test
    fun allow2pc() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAllow2pc(boolValue)
            assertEquals(boolValue, opt.allow2pc())
        }
    }

    @Test
    fun rowCache() {
        DBOptions().use { opt ->
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
        DBOptions().use { opt ->
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
                ): LogRecordFoundResult {
                    return LogRecordFoundResult(
                        WalProcessingOption.CONTINUE_PROCESSING, false
                    )
                }

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
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setFailIfOptionsFileError(boolValue)
            assertEquals(boolValue, opt.failIfOptionsFileError())
        }
    }

    @Test
    fun dumpMallocStats() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setDumpMallocStats(boolValue)
            assertEquals(boolValue, opt.dumpMallocStats())
        }
    }

    @Test
    fun avoidFlushDuringRecovery() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAvoidFlushDuringRecovery(boolValue)
            assertEquals(boolValue, opt.avoidFlushDuringRecovery())
        }
    }

    @Test
    fun avoidFlushDuringShutdown() {
        DBOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setAvoidFlushDuringShutdown(boolValue)
            assertEquals(boolValue, opt.avoidFlushDuringShutdown())
        }
    }

    @Test
    fun allowIngestBehind() {
        DBOptions().use { opt ->
            assertFalse(opt.allowIngestBehind())
            opt.setAllowIngestBehind(true)
            assertTrue(opt.allowIngestBehind())
        }
    }

    @Test
    fun preserveDeletes() {
        DBOptions().use { opt ->
            assertFalse(opt.preserveDeletes())
            opt.setPreserveDeletes(true)
            assertTrue(opt.preserveDeletes())
        }
    }

    @Test
    fun twoWriteQueues() {
        DBOptions().use { opt ->
            assertFalse(opt.twoWriteQueues())
            opt.setTwoWriteQueues(true)
            assertTrue(opt.twoWriteQueues())
        }
    }

    @Test
    fun manualWalFlush() {
        DBOptions().use { opt ->
            assertFalse(opt.manualWalFlush())
            opt.setManualWalFlush(true)
            assertTrue(opt.manualWalFlush())
        }
    }

    @Test
    fun atomicFlush() {
        DBOptions().use { opt ->
            assertFalse(opt.atomicFlush())
            opt.setAtomicFlush(true)
            assertTrue(opt.atomicFlush())
        }
    }

    @Test
    fun rateLimiter() {
        DBOptions().use { options ->
            DBOptions().use { anotherOptions ->
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
        DBOptions().use { options ->
            SstFileManager(getDefaultEnv()).use { sstFileManager ->
                options.setSstFileManager(
                    sstFileManager
                )
            }
        }
    }

    @Test
    fun statistics() {
        DBOptions().use { options ->
            val statistics = options.statistics()
            assertNull(statistics)
        }

        Statistics().use { statistics ->
            DBOptions().apply {
                setStatistics(statistics)
            }.use { options ->
                options.statistics().use { stats ->
                    assertNotNull(stats)
                }
            }
        }
    }
}
