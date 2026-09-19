package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertTrue

class ColumnFamilyNativeTest {
    @Test
    fun listColumnFamiliesPreservesEmbeddedNulNames() {
        val dbPath = createTestDBFolder("ColumnFamilyNativeTest_binary_names")
        val name = byteArrayOf('c'.code.toByte(), 0, 'f'.code.toByte(), 0x80.toByte(), 0xff.toByte())

        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                ColumnFamilyOptions().use { columnFamilyOptions ->
                    val handle = db.createColumnFamilies(columnFamilyOptions, listOf(name)).single()
                    try {
                        db.put(handle, "key".encodeToByteArray(), "value".encodeToByteArray())
                        FlushOptions().setWaitForFlush(true).use { flushOptions ->
                            db.flush(flushOptions, handle)
                        }
                        assertTrue(db.getLiveFilesMetaData().any { it.columnFamilyName().contentEquals(name) })
                        assertTrue(db.getColumnFamilyMetaData(handle).name().contentEquals(name))
                    } finally {
                        handle.close()
                    }
                }
            }

            assertTrue(listColumnFamilies(options, dbPath).any { it.contentEquals(name) })
        }

        ColumnFamilyOptions().use { columnFamilyOptions ->
            val descriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily, columnFamilyOptions),
                ColumnFamilyDescriptor(name, columnFamilyOptions),
            )
            val handles = mutableListOf<ColumnFamilyHandle>()
            DBOptions().use { dbOptions ->
                openRocksDB(dbOptions, dbPath, descriptors, handles).use { db ->
                    try {
                        assertTrue(handles[1].getName().contentEquals(name))
                        assertTrue(db.get(handles[1], "key".encodeToByteArray())!!.contentEquals("value".encodeToByteArray()))
                    } finally {
                        handles.forEach { it.close() }
                    }
                }
            }
        }
    }
}
