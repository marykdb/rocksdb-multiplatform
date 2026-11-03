package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BlobOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun databaseBlobOptionsRoundTrip() {
        Options().use { options ->
            assertFalse(options.enableBlobFiles())
            assertSame(options, options.setEnableBlobFiles(true))
            options.setMinBlobSize(64)
                .setBlobFileSize(1024 * 1024L)
                .setBlobCompressionType(CompressionType.NO_COMPRESSION)
                .setEnableBlobGarbageCollection(true)
                .setBlobGarbageCollectionAgeCutoff(0.4)
                .setBlobGarbageCollectionForceThreshold(0.6)
                .setBlobCompactionReadaheadSize(16384)
                .setBlobFileStartingLevel(1)
                .setPrepopulateBlobCache(PrepopulateBlobCache.PREPOPULATE_BLOB_FLUSH_ONLY)
            assertTrue(options.enableBlobFiles())
            assertEquals(64L, options.minBlobSize())
            assertEquals(1024 * 1024L, options.blobFileSize())
            assertEquals(CompressionType.NO_COMPRESSION, options.blobCompressionType())
            assertTrue(options.enableBlobGarbageCollection())
            assertEquals(0.4, options.blobGarbageCollectionAgeCutoff())
            assertEquals(0.6, options.blobGarbageCollectionForceThreshold())
            assertEquals(16384L, options.blobCompactionReadaheadSize())
            assertEquals(1, options.blobFileStartingLevel())
            assertEquals(PrepopulateBlobCache.PREPOPULATE_BLOB_FLUSH_ONLY, options.prepopulateBlobCache())
        }
    }

    @Test
    fun columnFamilyBlobOptionsRoundTrip() {
        ColumnFamilyOptions().use { options ->
            assertFalse(options.enableBlobFiles())
            assertSame(options, options.setEnableBlobFiles(true))
            options.setMinBlobSize(128)
                .setBlobFileSize(2 * 1024 * 1024L)
                .setBlobCompressionType(CompressionType.NO_COMPRESSION)
                .setEnableBlobGarbageCollection(true)
                .setBlobGarbageCollectionAgeCutoff(0.3)
                .setBlobGarbageCollectionForceThreshold(0.7)
                .setBlobCompactionReadaheadSize(32768)
                .setBlobFileStartingLevel(2)
                .setPrepopulateBlobCache(PrepopulateBlobCache.PREPOPULATE_BLOB_FLUSH_ONLY)
            assertTrue(options.enableBlobFiles())
            assertEquals(128L, options.minBlobSize())
            assertEquals(2 * 1024 * 1024L, options.blobFileSize())
            assertEquals(CompressionType.NO_COMPRESSION, options.blobCompressionType())
            assertTrue(options.enableBlobGarbageCollection())
            assertEquals(0.3, options.blobGarbageCollectionAgeCutoff())
            assertEquals(0.7, options.blobGarbageCollectionForceThreshold())
            assertEquals(32768L, options.blobCompactionReadaheadSize())
            assertEquals(2, options.blobFileStartingLevel())
            assertEquals(PrepopulateBlobCache.PREPOPULATE_BLOB_FLUSH_ONLY, options.prepopulateBlobCache())
        }
    }

    @Test
    fun blobValuesSurviveFlushAndDatabaseReopen() {
        val path = createTestDBFolder("BlobOptionsTest_reopen")
        val smallKey = byteArrayOf(0, 1)
        val smallValue = byteArrayOf(0, 2)
        val largeKey = byteArrayOf(0, 3)
        val largeValue = ByteArray(4096) { ((it * 37) % 256).toByte() }
        Options().setCreateIfMissing(true)
            .setEnableBlobFiles(true)
            .setMinBlobSize(64)
            .setBlobFileSize(1024 * 1024L)
            .setBlobCompressionType(CompressionType.NO_COMPRESSION)
            .setEnableBlobGarbageCollection(true)
            .setBlobGarbageCollectionAgeCutoff(0.5)
            .setBlobGarbageCollectionForceThreshold(0.5)
            .setBlobCompactionReadaheadSize(16384)
            .setBlobFileStartingLevel(0)
            .setPrepopulateBlobCache(PrepopulateBlobCache.PREPOPULATE_BLOB_DISABLE)
            .use { options ->
                openRocksDB(options, path).use { db ->
                    db.put(smallKey, smallValue)
                    db.put(largeKey, largeValue)
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                    assertTrue(db.getLongProperty("rocksdb.num-blob-files") > 0)
                    assertContentEquals(smallValue, db[smallKey])
                    assertContentEquals(largeValue, db[largeKey])
                }
                openRocksDB(options, path).use { db ->
                    assertTrue(db.getLongProperty("rocksdb.num-blob-files") > 0)
                    assertContentEquals(smallValue, db[smallKey])
                    assertContentEquals(largeValue, db[largeKey])
                }
            }
    }

    @Test
    fun columnFamilyBlobSettingsApplyWithoutChangingDefaultFamily() {
        val path = createTestDBFolder("BlobOptionsTest_columnFamily")
        val key = byteArrayOf(0, 4)
        val value = ByteArray(2048) { (it % 251).toByte() }
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, path).use { db ->
                ColumnFamilyOptions().setEnableBlobFiles(true).setMinBlobSize(64)
                    .setBlobCompressionType(CompressionType.NO_COMPRESSION).use { blobOptions ->
                        db.createColumnFamily(ColumnFamilyDescriptor("blobs".encodeToByteArray(), blobOptions)).use { column ->
                            db.put(column, key, value)
                            FlushOptions().setWaitForFlush(true).use { db.flush(it, column) }
                            assertTrue(db.getLongProperty(column, "rocksdb.num-blob-files") > 0)
                            assertEquals(0L, db.getLongProperty("rocksdb.num-blob-files"))
                            assertContentEquals(value, db.get(column, key))
                        }
                    }
            }
        }
    }
}
