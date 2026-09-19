package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlainTableConfigTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun plainTableConfigRoundTrip() {
        val config = PlainTableConfig()
            .setKeySize(16)
            .setBloomBitsPerKey(8)
            .setHashTableRatio(0.75)
            .setIndexSparseness(32)
            .setHugePageTlbSize(2)
            .setEncodingType(EncodingType.kPlain)
            .setFullScanMode(true)
            .setStoreIndexInFile(true)

        assertEquals(16, config.keySize())
        assertEquals(8, config.bloomBitsPerKey())
        assertEquals(0.75, config.hashTableRatio())
        assertEquals(32L, config.indexSparseness())
        assertEquals(2, config.hugePageTlbSize())
        assertEquals(EncodingType.kPlain, config.encodingType())
        assertTrue(config.fullScanMode())
        assertTrue(config.storeIndexInFile())

        config.setFullScanMode(false)
        config.setStoreIndexInFile(false)
        assertFalse(config.fullScanMode())
        assertFalse(config.storeIndexInFile())

        Options().use { options ->
            options.setTableFormatConfig(config)
        }
    }
}
