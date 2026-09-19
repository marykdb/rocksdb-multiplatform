package maryk.rocksdb

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdvancedOptionsTest {
    private lateinit var dbDir: File

    @BeforeTest
    fun setUp() {
        loadRocksDBLibrary()
        dbDir = createTempDirectory(prefix = "rocksdb-advanced").toFile()
    }

    @AfterTest
    fun tearDown() {
        if (::dbDir.isInitialized) {
            dbDir.deleteRecursively()
        }
    }

    @Test
    fun walRecoveryModeRoundTrip() {
        DBOptions().use { options ->
            options.setWalRecoveryMode(WALRecoveryMode.SkipAnyCorruptedRecords)
            assertEquals(WALRecoveryMode.SkipAnyCorruptedRecords, options.walRecoveryMode())

            options.setWalRecoveryMode(WALRecoveryMode.AbsoluteConsistency)
            assertEquals(WALRecoveryMode.AbsoluteConsistency, options.walRecoveryMode())
        }
    }

    @Test
    fun perfContextCollectsMetrics() {
        Options().setCreateIfMissing(true).use { options ->
            RocksDB.open(options, dbDir.absolutePath).use { db ->
                db.setPerfLevel(PerfLevel.ENABLE_TIME_AND_CPU_TIME_EXCEPT_FOR_MUTEX)

                db.getPerfContext().use { context ->
                    context.reset()

                    val key = "perf-key".encodeToByteArray()
                    val value = "value".encodeToByteArray()
                    repeat(5) {
                        db.put(key, value)
                        assertNotNull(db.get(key))
                    }

                    assertTrue(context.getUserKeyComparisonCount() >= 0)
                    assertTrue(context.getReadBytes() >= 0)
                    assertTrue(context.toString(false).contains("user_key_comparison_count"))
                }

                assertEquals(
                    PerfLevel.ENABLE_TIME_AND_CPU_TIME_EXCEPT_FOR_MUTEX,
                    db.getPerfLevel()
                )
            }
        }
    }

    @Test
    fun liveFileMetadataIsExposed() {
        Options().setCreateIfMissing(true).use { options ->
            RocksDB.open(options, dbDir.absolutePath).use { db ->
                val key = "meta".encodeToByteArray()
                val value = ByteArray(1024) { 1 }
                db.put(key, value)

                FlushOptions().setWaitForFlush(true).use { flushOptions ->
                    db.flush(flushOptions)
                }

                val metadata = db.getLiveFilesMetaData()
                assertTrue(metadata.isNotEmpty())
                val first = metadata.first()
                assertTrue(first.level() >= 0)
                assertTrue(first.fileName().isNotEmpty())
                assertContentEquals(defaultColumnFamily, first.columnFamilyName())
            }
        }
    }

    @Test
    fun transactionLogIteratorStreamsWal() {
        Options().setCreateIfMissing(true).use { options ->
            RocksDB.open(options, dbDir.absolutePath).use { db ->
                WriteOptions().use { writeOptions ->
                    writeOptions.setDisableWAL(false)
                    repeat(3) { index ->
                        val key = "wal-key-$index".encodeToByteArray()
                        val value = "wal-value-$index".encodeToByteArray()
                        db.put(writeOptions, key, value)
                    }
                }

                db.getUpdatesSince(0).use { iterator: TransactionLogIterator ->
                    iterator.status()

                    val sequences = mutableListOf<Long>()
                    while (iterator.isValid()) {
                        val batch = iterator.getBatch()
                        sequences += batch.sequenceNumber()
                        assertTrue(batch.writeBatch().count() > 0)
                        iterator.next()
                    }

                    assertTrue(sequences.isNotEmpty())
                    assertTrue(sequences.zipWithNext().all { (current, next) -> current <= next })
                }
            }
        }
    }

    @Test
    fun writeBufferManagerTracksAllowStall() {
        LRUCache(4L * 1024 * 1024).use { lruCache ->
            val sharedCache: Cache = lruCache

            WriteBufferManager(2L * 1024 * 1024, sharedCache).use { manager ->
                assertFalse(manager.allowStall())
            }

            WriteBufferManager(2L * 1024 * 1024, sharedCache, allowStall = true).use { manager ->
                assertTrue(manager.allowStall())
            }
        }
    }

    @Test
    fun importColumnFamilyOptionsTracksMoveFiles() {
        ImportColumnFamilyOptions().use { options ->
            assertFalse(options.moveFiles())

            options.setMoveFiles(true)
            assertTrue(options.moveFiles())
        }
    }

    @Test
    fun sstFileManagerIntegratesWithOptions() {
        val env = getDefaultEnv()
        SstFileManager(env).use { manager ->
            manager.setMaxAllowedSpaceUsage(1024L * 1024)
            manager.setCompactionBufferSize(512L)
            manager.setDeleteRateBytesPerSecond(2048)
            manager.setMaxTrashDBRatio(0.5)

            assertFalse(manager.isMaxAllowedSpaceReached())
            assertFalse(manager.isMaxAllowedSpaceReachedIncludingCompactions())
            assertEquals(0.5, manager.getMaxTrashDBRatio(), 1e-6)
        }

        val manager = SstFileManager(env)
        try {
            Options().setCreateIfMissing(true).use { options ->
                options.setSstFileManager(manager)

                RocksDB.open(options, File(dbDir, "sst-manager").apply { mkdirs() }.absolutePath).use { db ->
                    db.put("manager".encodeToByteArray(), "value".encodeToByteArray())
                    assertTrue(manager.getTotalSize() >= 0)
                }
            }
        } finally {
            manager.close()
        }
    }

    @Test
    fun optionsUtilLoadsLatestAndFileOptions() {
        val dbPath = File(dbDir, "options-util").apply { mkdirs() }.absolutePath

        Options().setCreateIfMissing(true).use { options ->
            RocksDB.open(options, dbPath).use { db ->
                db.put("util-key".encodeToByteArray(), "value".encodeToByteArray())
            }
        }

        ConfigOptions().use { config ->
            config
                .setDelimiter(";")
                .setIgnoreUnknownOptions(true)
                .setInputStringsEscaped(false)
                .setEnv(getDefaultEnv())
                .setSanityLevel(SanityLevel.LOOSELY_COMPATIBLE)

            DBOptions().use { dbOptions ->
                val descriptors = mutableListOf<ColumnFamilyDescriptor>()
                OptionsUtil.loadLatestOptions(config, dbPath, dbOptions, descriptors)

                assertTrue(descriptors.isNotEmpty())
                val first = descriptors.first()
                assertContentEquals(defaultColumnFamily, first.getName())
                assertTrue(first.getOptions().numLevels() >= 1)

                val latestOptionsFileName = OptionsUtil.getLatestOptionsFileName(dbPath, getDefaultEnv())
                assertTrue(latestOptionsFileName.contains("OPTIONS"))
                val latestOptionsFile = File(latestOptionsFileName).let { file ->
                    if (file.isAbsolute) file else File(dbPath, latestOptionsFileName)
                }
                assertTrue(latestOptionsFile.exists())

                val fileDescriptors = mutableListOf<ColumnFamilyDescriptor>()
                OptionsUtil.loadOptionsFromFile(config, latestOptionsFile.absolutePath, dbOptions, fileDescriptors)

                assertFalse(fileDescriptors.isEmpty())
                assertContentEquals(defaultColumnFamily, fileDescriptors.first().getName())
            }
        }
    }

    @Test
    fun backgroundWorkControlsExposeNativeOperations() {
        Options().setCreateIfMissing(true).use { options ->
            RocksDB.open(options, File(dbDir, "background-work").apply { mkdirs() }.absolutePath).use { db ->
                db.put("background".encodeToByteArray(), "value".encodeToByteArray())

                // Ensure the new administrative helpers round-trip through the bindings.
                db.cancelAllBackgroundWork(false)

                val catchUp = runCatching { db.tryCatchUpWithPrimary() }
                catchUp.exceptionOrNull()?.let { throwable ->
                    if (throwable is RocksDBException) {
                        throwable.status?.let { status ->
                            assertEquals(StatusCode.NotSupported, status.code)
                        }
                    } else {
                        throw throwable
                    }
                }
            }
        }
    }
}
