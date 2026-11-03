package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExpandedLiveOptionsTest {
    init { loadRocksDBLibrary() }

    @Test
    fun liveColumnFamilyScalarsApplyWithoutReplacingUnchangedSettings() {
        val path = createTestDBFolder("ExpandedLiveOptionsTest_scalars")
        Options().setCreateIfMissing(true).setWriteBufferSize(4 * 1024 * 1024L).use { options ->
            openRocksDB(options, path).use { db ->
                mutableColumnFamilyOptionsBuilder().use { builder ->
                    builder.setMaxWriteBufferNumber(3)
                    assertEquals(3, builder.maxWriteBufferNumber(), "maxWriteBufferNumber")
                    builder.setArenaBlockSize(4096L)
                    assertEquals(4096L, builder.arenaBlockSize(), "arenaBlockSize")
                    builder.setMemtablePrefixBloomSizeRatio(0.1)
                    assertEquals(0.1, builder.memtablePrefixBloomSizeRatio(), "memtablePrefixBloomSizeRatio")
                    builder.setMemtableWholeKeyFiltering(true)
                    assertEquals(true, builder.memtableWholeKeyFiltering(), "memtableWholeKeyFiltering")
                    builder.setMemtableHugePageSize(0L)
                    assertEquals(0L, builder.memtableHugePageSize(), "memtableHugePageSize")
                    builder.setMaxSuccessiveMerges(4L)
                    assertEquals(4L, builder.maxSuccessiveMerges(), "maxSuccessiveMerges")
                    builder.setMaxSequentialSkipInIterations(16L)
                    assertEquals(16L, builder.maxSequentialSkipInIterations(), "maxSequentialSkipInIterations")
                    builder.setInplaceUpdateNumLocks(100L)
                    assertEquals(100L, builder.inplaceUpdateNumLocks(), "inplaceUpdateNumLocks")
                    builder.setLevel0SlowdownWritesTrigger(12)
                    assertEquals(12, builder.level0SlowdownWritesTrigger(), "level0SlowdownWritesTrigger")
                    builder.setLevel0StopWritesTrigger(20)
                    assertEquals(20, builder.level0StopWritesTrigger(), "level0StopWritesTrigger")
                    builder.setTargetFileSizeBase(1048576L)
                    assertEquals(1048576L, builder.targetFileSizeBase(), "targetFileSizeBase")
                    builder.setTargetFileSizeMultiplier(2)
                    assertEquals(2, builder.targetFileSizeMultiplier(), "targetFileSizeMultiplier")
                    builder.setSoftPendingCompactionBytesLimit(67108864L)
                    assertEquals(67108864L, builder.softPendingCompactionBytesLimit(), "softPendingCompactionBytesLimit")
                    builder.setHardPendingCompactionBytesLimit(134217728L)
                    assertEquals(134217728L, builder.hardPendingCompactionBytesLimit(), "hardPendingCompactionBytesLimit")
                    builder.setTtl(7200L)
                    assertEquals(7200L, builder.ttl(), "ttl")
                    builder.setPeriodicCompactionSeconds(3600L)
                    assertEquals(3600L, builder.periodicCompactionSeconds(), "periodicCompactionSeconds")
                    builder.setReportBgIoStats(true)
                    assertEquals(true, builder.reportBgIoStats(), "reportBgIoStats")
                    builder.setParanoidFileChecks(true)
                    assertEquals(true, builder.paranoidFileChecks(), "paranoidFileChecks")
                    builder.setEnableBlobFiles(true)
                    assertEquals(true, builder.enableBlobFiles(), "enableBlobFiles")
                    builder.setMinBlobSize(1024L)
                    assertEquals(1024L, builder.minBlobSize(), "minBlobSize")
                    builder.setBlobFileSize(1048576L)
                    assertEquals(1048576L, builder.blobFileSize(), "blobFileSize")
                    builder.setEnableBlobGarbageCollection(true)
                    assertEquals(true, builder.enableBlobGarbageCollection(), "enableBlobGarbageCollection")
                    builder.setBlobGarbageCollectionAgeCutoff(0.3)
                    assertEquals(0.3, builder.blobGarbageCollectionAgeCutoff(), "blobGarbageCollectionAgeCutoff")
                    builder.setBlobGarbageCollectionForceThreshold(0.9)
                    assertEquals(0.9, builder.blobGarbageCollectionForceThreshold(), "blobGarbageCollectionForceThreshold")
                    builder.setBlobCompactionReadaheadSize(16384L)
                    assertEquals(16384L, builder.blobCompactionReadaheadSize(), "blobCompactionReadaheadSize")
                    builder.setBlobFileStartingLevel(0)
                    assertEquals(0, builder.blobFileStartingLevel(), "blobFileStartingLevel")
                    builder.setExperimentalMempurgeThreshold(0.0)
                    assertEquals(0.0, builder.experimentalMempurgeThreshold(), "experimentalMempurgeThreshold")
                    builder.setMinTombstonesForRangeConversion(0)
                    assertEquals(0, builder.minTombstonesForRangeConversion(), "minTombstonesForRangeConversion")
                    builder.setReadTriggeredCompactionThreshold(16.0)
                    assertEquals(16.0, builder.readTriggeredCompactionThreshold(), "readTriggeredCompactionThreshold")
                    builder.build().use { update ->
                        builder.setMaxWriteBufferNumber(5)
                        db.setOptions(update)
                    }
                }
                ConfigOptions().use { config ->
                    DBOptions().use { loaded ->
                        val descriptors = mutableListOf<ColumnFamilyDescriptor>()
                        try {
                            OptionsUtil.loadLatestOptions(config, path, loaded, descriptors)
                            val family = descriptors.single().getOptions()
                            assertEquals(4 * 1024 * 1024L, family.writeBufferSize())
                            assertEquals(3, family.maxWriteBufferNumber())
                            assertEquals(7200L, family.ttl())
                            assertEquals(3600L, family.periodicCompactionSeconds())
                            assertEquals(1048576L, family.targetFileSizeBase())
                            assertTrue(family.enableBlobFiles())
                            assertEquals(1024L, family.minBlobSize())
                        } finally {
                            descriptors.forEach { it.getOptions().close() }
                        }
                    }
                }
                val key = byteArrayOf(0, 1)
                val value = ByteArray(2048) { it.toByte() }
                db.put(key, value)
                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                assertContentEquals(value, db.get(key))
            }
        }
    }

    @Test
    fun liveDatabaseWakeupIntervalRemainsPartial() {
        Options().setCreateIfMissing(true).setMaxOpenFiles(83).use { options ->
            openRocksDB(options, createTestDBFolder("ExpandedLiveOptionsTest_wakeup")).use { db ->
                mutableDBOptionsBuilder().use { builder ->
                    builder.setMaxBackgroundCompactions(2)
                    builder.setMaxCompactionTriggerWakeupSeconds(30)
                    assertEquals(2, builder.maxBackgroundCompactions())
                    assertEquals(30L, builder.maxCompactionTriggerWakeupSeconds())
                    builder.build().use { db.setDBOptions(it) }
                }
                db.put(byteArrayOf(1), byteArrayOf(2))
                assertContentEquals(byteArrayOf(2), db.get(byteArrayOf(1)))
            }
        }
    }
    @Test
    fun newBuilderGettersRequireAnExplicitValue() {
        mutableColumnFamilyOptionsBuilder().use { builder ->
            assertFailsWith<NoSuchElementException> { builder.maxWriteBufferNumber() }
            assertFailsWith<NoSuchElementException> { builder.ttl() }
        }
        mutableDBOptionsBuilder().use { builder ->
            assertFailsWith<NoSuchElementException> { builder.maxCompactionTriggerWakeupSeconds() }
        }
        parseMutableColumnFamilyOptions("ttl=900").use { builder ->
            assertEquals(900L, builder.ttl())
            assertFailsWith<NoSuchElementException> { builder.maxWriteBufferNumber() }
        }
    }
}
