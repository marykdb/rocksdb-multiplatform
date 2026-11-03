package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CompressionConfigurationTest {
    init { loadRocksDBLibrary() }

    @Test
    fun attachedCompressionSettingsAreCopiesAndReturnedSnapshotsAreOwned() {
        Options().use { options ->
            assertNull(options.copyConfiguredCompressionOptions())
            assertNull(options.copyConfiguredBottommostCompressionOptions())
            CompressionOptions().setWindowBits(-14).setLevel(3).setStrategy(0)
                .setMaxDictBytes(2048).setZStdMaxTrainBytes(1024).setEnabled(true).use { source ->
                    options.configureCompressionOptions(source)
                    options.configureBottommostCompressionOptions(source)
                    source.setLevel(9).setEnabled(false)
                }
            options.copyConfiguredCompressionOptions()!!.use { copy ->
                assertEquals(-14, copy.windowBits())
                assertEquals(3, copy.level())
                assertEquals(0, copy.strategy())
                assertEquals(2048, copy.maxDictBytes())
                assertEquals(1024, copy.zstdMaxTrainBytes())
                assertTrue(copy.enabled())
                copy.setEnabled(false)
            }
            options.copyConfiguredCompressionOptions()!!.use { assertTrue(it.enabled()) }
            options.copyConfiguredBottommostCompressionOptions()!!.use { assertEquals(3, it.level()) }
        }
        val detached = ColumnFamilyOptions().use { options ->
            assertNull(options.copyConfiguredCompressionOptions())
            CompressionOptions().setLevel(4).setEnabled(false).use { source ->
                options.configureCompressionOptions(source)
                options.configureBottommostCompressionOptions(source)
            }
            options.copyConfiguredBottommostCompressionOptions()!!
        }
        detached.use {
            assertEquals(4, it.level())
            assertFalse(it.enabled())
        }
    }

    @Test
    fun copiedSettingsCanBeUsedAfterClosingTheConfigurationHandle() {
        Options().setCreateIfMissing(true).setCompressionType(CompressionType.ZSTD_COMPRESSION)
            .setBottommostCompressionType(CompressionType.ZSTD_COMPRESSION).use { options ->
                CompressionOptions().setLevel(3).setEnabled(true).use { source ->
                    options.configureCompressionOptions(source)
                    options.configureBottommostCompressionOptions(source)
                }
                openRocksDB(options, createTestDBFolder("CompressionConfigurationTest_database")).use { db ->
                    val value = ByteArray(32768) { (it % 17).toByte() }
                    db.put(byteArrayOf(1), value)
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                    assertContentEquals(value, db.get(byteArrayOf(1)))
                }
            }
    }
    @Test
    fun bottommostCompressionIsIndependentFromGeneralCompression() {
        Options().setCompressionType(CompressionType.SNAPPY_COMPRESSION).use { options ->
            options.setBottommostCompressionType(CompressionType.ZSTD_COMPRESSION)
            assertEquals(CompressionType.ZSTD_COMPRESSION, options.bottommostCompressionType())
            assertEquals(CompressionType.SNAPPY_COMPRESSION, options.compressionType())
        }
        ColumnFamilyOptions().setCompressionType(CompressionType.SNAPPY_COMPRESSION).use { options ->
            options.setBottommostCompressionType(CompressionType.NO_COMPRESSION)
            assertEquals(CompressionType.NO_COMPRESSION, options.bottommostCompressionType())
            assertEquals(CompressionType.SNAPPY_COMPRESSION, options.compressionType())
        }
    }
}
