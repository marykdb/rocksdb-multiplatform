package maryk.rocksdb

import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TtlDBTest {
    private lateinit var dbDir: File

    @BeforeTest
    fun setup() {
        loadRocksDBLibrary()
        dbDir = createTempDirectory(prefix = "rocksdb-ttl").toFile()
    }

    @AfterTest
    fun tearDown() {
        if (::dbDir.isInitialized) {
            dbDir.deleteRecursively()
        }
    }

    @Test
    fun ttlDBOpen() {
        Options().setCreateIfMissing(true).use { options ->
            openTtlDB(options, dbDir.absolutePath).use { ttlDB ->
                val key = "key".encodeToByteArray()
                val value = "value".encodeToByteArray()
                ttlDB.put(key, value)
                assertContentEquals(value, ttlDB.get(key))
            }
        }
    }

    @Test
    fun ttlDBOpenWithTtl() {
        Options().setCreateIfMissing(true).use { options ->
            openTtlDB(options, dbDir.absolutePath, 1, false).use { ttlDB ->
                val key = "ttl-key".encodeToByteArray()
                val value = "value".encodeToByteArray()
                ttlDB.put(key, value)
                assertContentEquals(value, ttlDB.get(key))

                TimeUnit.SECONDS.sleep(2)
                ttlDB.compactRange()
                assertNull(ttlDB.get(key))
            }
        }
    }

    @Test
    fun ttlDbOpenWithColumnFamilies() {
        val descriptors = listOf(
            ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY),
            ColumnFamilyDescriptor("new_cf".encodeToByteArray())
        )
        val ttlValues = listOf(0, 1)
        val handles = mutableListOf<ColumnFamilyHandle>()

        DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
            .use { dbOptions ->
                openTtlDB(dbOptions, dbDir.absolutePath, descriptors, handles, ttlValues, false)
                    .use { ttlDB ->
                        try {
                            val key = "multi".encodeToByteArray()
                            val value = "value".encodeToByteArray()
                            ttlDB.put(key, value)
                            ttlDB.put(handles[1], key, value)

                            TimeUnit.SECONDS.sleep(2)
                            ttlDB.compactRange()
                            ttlDB.compactRange(handles[1])

                            assertNotNull(ttlDB.get(key))
                            assertNull(ttlDB.get(handles[1], key))
                        } finally {
                            handles.forEach { it.close() }
                        }
                    }
            }
    }

    @Test
    fun createTtlColumnFamily() {
        Options().setCreateIfMissing(true).use { options ->
            openTtlDB(options, dbDir.absolutePath).use { ttlDB ->
                ttlDB.createColumnFamilyWithTtl(
                    ColumnFamilyDescriptor("new_cf".encodeToByteArray()),
                    1
                ).use { cfHandle ->
                    val key = "cf-key".encodeToByteArray()
                    val value = "value".encodeToByteArray()
                    ttlDB.put(cfHandle, key, value)
                    assertContentEquals(value, ttlDB.get(cfHandle, key))

                    TimeUnit.SECONDS.sleep(2)
                    ttlDB.compactRange(cfHandle)
                    assertNull(ttlDB.get(cfHandle, key))
                }
            }
        }
    }
}
