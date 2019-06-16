package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlainTableConfigTest {
    @Test
    fun keySize() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setKeySize(5)
        assertEquals(5, plainTableConfig.keySize())
    }

    @Test
    fun bloomBitsPerKey() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setBloomBitsPerKey(11)
        assertEquals(11, plainTableConfig.bloomBitsPerKey())
    }

    @Test
    fun hashTableRatio() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setHashTableRatio(0.95)
        assertEquals(0.95, plainTableConfig.hashTableRatio())
    }

    @Test
    fun indexSparseness() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setIndexSparseness(18)
        assertEquals(18, plainTableConfig.indexSparseness())
    }

    @Test
    fun hugePageTlbSize() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setHugePageTlbSize(1)
        assertEquals(1, plainTableConfig.hugePageTlbSize())
    }

    @Test
    fun encodingType() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setEncodingType(EncodingType.kPrefix)
        assertEquals(EncodingType.kPrefix, plainTableConfig.encodingType())
    }

    @Test
    fun fullScanMode() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setFullScanMode(true)
        assertTrue(plainTableConfig.fullScanMode())
    }

    @Test
    fun storeIndexInFile() {
        val plainTableConfig = PlainTableConfig()
        plainTableConfig.setStoreIndexInFile(true)
        assertTrue(plainTableConfig.storeIndexInFile())
    }

    @Test
    fun plainTableConfig() {
        Options().use { opt ->
            val plainTableConfig = PlainTableConfig()
            opt.setTableFormatConfig(plainTableConfig)
            assertEquals("PlainTable", opt.tableFactoryName())
        }
    }
}
