package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MemTableTest {
    @Test
    fun hashSkipListMemTable() {
        Options().use { options ->
            // Test HashSkipListMemTableConfig
            val memTableConfig = HashSkipListMemTableConfig()
            assertEquals(1000000, memTableConfig.bucketCount())
            memTableConfig.setBucketCount(2000000)
            assertEquals(2000000, memTableConfig.bucketCount())
            assertEquals(4, memTableConfig.height())
            memTableConfig.setHeight(5)
            assertEquals(5, memTableConfig.height())
            assertEquals(4, memTableConfig.branchingFactor())
            memTableConfig.setBranchingFactor(6)
            assertEquals(6, memTableConfig.branchingFactor())
            options.setMemTableConfig(memTableConfig)
        }
    }

    @Test
    fun skipListMemTable() {
        Options().use { options ->
            val skipMemTableConfig = SkipListMemTableConfig()
            assertEquals(0, skipMemTableConfig.lookahead())
            skipMemTableConfig.setLookahead(20)
            assertEquals(20, skipMemTableConfig.lookahead())
            options.setMemTableConfig(skipMemTableConfig)
        }
    }

    @Test
    fun hashLinkedListMemTable() {
        Options().use { options ->
            val hashLinkedListMemTableConfig = HashLinkedListMemTableConfig()
            assertEquals(50000, hashLinkedListMemTableConfig.bucketCount())
            hashLinkedListMemTableConfig.setBucketCount(100000)
            assertEquals(100000, hashLinkedListMemTableConfig.bucketCount())
            assertEquals(0, hashLinkedListMemTableConfig.hugePageTlbSize())
            hashLinkedListMemTableConfig.setHugePageTlbSize(1)
            assertEquals(1, hashLinkedListMemTableConfig.hugePageTlbSize())
            assertEquals(4096, hashLinkedListMemTableConfig.bucketEntriesLoggingThreshold())
            hashLinkedListMemTableConfig.setBucketEntriesLoggingThreshold(200)
            assertEquals(200, hashLinkedListMemTableConfig.bucketEntriesLoggingThreshold())
            assertTrue(hashLinkedListMemTableConfig.ifLogBucketDistWhenFlush())
            hashLinkedListMemTableConfig.setIfLogBucketDistWhenFlush(false)
            assertFalse(hashLinkedListMemTableConfig.ifLogBucketDistWhenFlush())
            assertEquals(256, hashLinkedListMemTableConfig.thresholdUseSkiplist())
            hashLinkedListMemTableConfig.setThresholdUseSkiplist(29)
            assertEquals(29, hashLinkedListMemTableConfig.thresholdUseSkiplist())
            options.setMemTableConfig(hashLinkedListMemTableConfig)
        }
    }

    @Test
    fun vectorMemTable() {
        Options().use { options ->
            val vectorMemTableConfig = VectorMemTableConfig()
            assertEquals(0, vectorMemTableConfig.reservedSize())
            vectorMemTableConfig.setReservedSize(123)
            assertEquals(123, vectorMemTableConfig.reservedSize())
            options.setMemTableConfig(vectorMemTableConfig)
        }
    }
}
