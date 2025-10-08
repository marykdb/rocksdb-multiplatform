package maryk.rocksdb

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TableFilterTest {
    private lateinit var dbDir: File

    @BeforeTest
    fun setup() {
        loadRocksDBLibrary()
        dbDir = createTempDirectory(prefix = "rocksdb-table-filter").toFile()
    }

    @AfterTest
    fun tearDown() {
        if (::dbDir.isInitialized) {
            dbDir.deleteRecursively()
        }
    }

    @Test
    fun readOptionsWithTableFilter() {
        DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
            .use { dbOptions ->
                ColumnFamilyOptions().use { cfOptions ->
                    val descriptors = listOf(
                        ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY),
                        ColumnFamilyDescriptor("new_cf".encodeToByteArray(), cfOptions)
                    )

                    val handles = mutableListOf<ColumnFamilyHandle>()
                    RocksDB.open(dbOptions, dbDir.absolutePath, descriptors, handles).use { db ->
                        try {
                            CfNameCollectionTableFilter().use { filter ->
                                FlushOptions().setWaitForFlush(true).use { flushOptions ->
                                    ReadOptions().use { readOptions ->
                                        readOptions.setTableFilter(filter)

                                        val defaultHandle = handles[0]
                                        val newHandle = handles[1]

                                        val keys = listOf("key1", "key2", "key3")
                                        keys.forEach { key ->
                                            val k = key.encodeToByteArray()
                                            val v = "value-$key".encodeToByteArray()
                                            db.put(defaultHandle, k, v)
                                            db.put(newHandle, k, v)
                                        }

                                        db.flush(flushOptions, handles)

                                        db.newIterator(defaultHandle, readOptions).use { iterator ->
                                            iterator.seekToFirst()
                                            while (iterator.isValid) {
                                                iterator.key()
                                                iterator.value()
                                                iterator.next()
                                            }
                                        }

                                        db.newIterator(newHandle, readOptions).use { iterator ->
                                            iterator.seekToFirst()
                                            while (iterator.isValid) {
                                                iterator.key()
                                                iterator.value()
                                                iterator.next()
                                            }
                                        }

                                        assertEquals(2, filter.cfNames.size)
                                        assertContentEquals(
                                            RocksDB.DEFAULT_COLUMN_FAMILY,
                                            filter.cfNames[0]
                                        )
                                        assertContentEquals(
                                            "new_cf".encodeToByteArray(),
                                            filter.cfNames[1]
                                        )
                                    }
                                }
                            }
                        } finally {
                            handles.forEach { it.close() }
                        }
                    }
                }
            }
    }

    private class CfNameCollectionTableFilter : AbstractTableFilter() {
        val cfNames = mutableListOf<ByteArray>()

        override fun filter(tableProperties: TableProperties): Boolean {
            tableProperties.columnFamilyName()?.let { name ->
                cfNames += name.encodeToByteArray()
            }
            return true
        }
    }
}
