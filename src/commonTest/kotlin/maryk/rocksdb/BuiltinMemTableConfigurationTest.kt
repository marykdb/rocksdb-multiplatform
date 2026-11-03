package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BuiltinMemTableConfigurationTest {
    init { loadRocksDBLibrary() }

    @Test
    fun defaultsMatchJavaAndConfiguredRepresentationsCanReadScanAndFlush() {
        assertEquals(0L, SkipListMemTableConfig().lookahead())
        assertEquals(1000000L, HashSkipListMemTableConfig().bucketCount())
        assertEquals(50000L, HashLinkedListMemTableConfig().bucketCount())
        assertEquals(256, HashLinkedListMemTableConfig().thresholdUseSkiplist())
        assertEquals(0, VectorMemTableConfig().reservedSize())
        val configs = listOf(
            SkipListMemTableConfig().setLookahead(8),
            HashSkipListMemTableConfig().setBucketCount(1024).setHeight(4).setBranchingFactor(4),
            HashLinkedListMemTableConfig().setBucketCount(1024).setHugePageTlbSize(0)
                .setBucketEntriesLoggingThreshold(100).setIfLogBucketDistWhenFlush(false).setThresholdUseSkiplist(32),
            VectorMemTableConfig().setReservedSize(128),
        )
        configs.forEachIndexed { index, config ->
            Options().setCreateIfMissing(true).setAllowConcurrentMemtableWrite(false)
                .useFixedLengthPrefixExtractor(1).configureMemTable(config).use { options ->
                    openRocksDB(options, createTestDBFolder("BuiltinMemTableConfigurationTest_$index")).use { db ->
                        db.put(byteArrayOf(1, 1), byteArrayOf(11))
                        db.put(byteArrayOf(1, 2), byteArrayOf(12))
                        assertContentEquals(byteArrayOf(11), db.get(byteArrayOf(1, 1)))
                        ReadOptions().setTotalOrderSeek(true).use { read ->
                            db.newIterator(read).use { iterator ->
                                iterator.seekToFirst()
                                var count = 0
                                while (iterator.isValid()) { count++; iterator.next() }
                                iterator.status()
                                assertEquals(2, count)
                            }
                        }
                        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                        assertContentEquals(byteArrayOf(12), db.get(byteArrayOf(1, 2)))
                    }
                }
            ColumnFamilyOptions().use { it.configureMemTable(config) }
        }
    }

    @Test
    fun invalidFactoryParametersAreRejectedBeforeCallingNativeCode() {
        Options().use { options ->
            assertFailsWith<IllegalArgumentException> {
                options.configureMemTable(HashSkipListMemTableConfig().setBucketCount(0))
            }
            assertFailsWith<IllegalArgumentException> {
                options.configureMemTable(VectorMemTableConfig().setReservedSize(-1))
            }
            assertFailsWith<IllegalArgumentException> {
                options.configureMemTable(HashLinkedListMemTableConfig().setThresholdUseSkiplist(0))
            }
        }
    }
}
