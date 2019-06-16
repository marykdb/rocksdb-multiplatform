package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals

class TableFilterTest {
    private fun createTestFolder() = createTestDBFolder("TableFilterTest")

    @Test
    fun readOptions() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { opt ->
            ColumnFamilyOptions().use { new_cf_opts ->
                val columnFamilyDescriptors = listOf(
                    ColumnFamilyDescriptor(defaultColumnFamily),
                    ColumnFamilyDescriptor("new_cf".encodeToByteArray(), new_cf_opts)
                )

                val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()

                // open database
                openRocksDB(
                    opt,
                    createTestFolder(),
                    columnFamilyDescriptors,
                    columnFamilyHandles
                ).use { db ->
                    try {
                        CfNameCollectionTableFilter().use { cfNameCollectingTableFilter ->
                            FlushOptions().apply {
                                setWaitForFlush(true)
                            }.use { flushOptions ->
                                ReadOptions().apply {
                                    setTableFilter(cfNameCollectingTableFilter)
                                }.use { readOptions ->
                                    db.put(
                                        columnFamilyHandles[0],
                                        "key1".encodeToByteArray(), "value1".encodeToByteArray()
                                    )
                                    db.put(
                                        columnFamilyHandles[0],
                                        "key2".encodeToByteArray(), "value2".encodeToByteArray()
                                    )
                                    db.put(
                                        columnFamilyHandles[0],
                                        "key3".encodeToByteArray(), "value3".encodeToByteArray()
                                    )
                                    db.put(
                                        columnFamilyHandles[1],
                                        "key1".encodeToByteArray(), "value1".encodeToByteArray()
                                    )
                                    db.put(
                                        columnFamilyHandles[1],
                                        "key2".encodeToByteArray(), "value2".encodeToByteArray()
                                    )
                                    db.put(
                                        columnFamilyHandles[1],
                                        "key3".encodeToByteArray(), "value3".encodeToByteArray()
                                    )

                                    db.flush(flushOptions, columnFamilyHandles)

                                    db.newIterator(columnFamilyHandles[0], readOptions).use { iterator ->
                                        iterator.seekToFirst()
                                        while (iterator.isValid()) {
                                            iterator.key()
                                            iterator.value()
                                            iterator.next()
                                        }
                                    }

                                    db.newIterator(columnFamilyHandles[1], readOptions).use { iterator ->
                                        iterator.seekToFirst()
                                        while (iterator.isValid()) {
                                            iterator.key()
                                            iterator.value()
                                            iterator.next()
                                        }
                                    }

                                    assertEquals(2, cfNameCollectingTableFilter.cfNames.size)
                                    assertContentEquals(defaultColumnFamily, cfNameCollectingTableFilter.cfNames[0])
                                    assertContentEquals("new_cf".encodeToByteArray(), cfNameCollectingTableFilter.cfNames[1])
                                }
                            }
                        }
                    } finally {
                        for (columnFamilyHandle in columnFamilyHandles) {
                            columnFamilyHandle.close()
                        }
                    }
                }
            }
        }
    }

    private class CfNameCollectionTableFilter : AbstractTableFilter() {
        val cfNames = mutableListOf<ByteArray>()

        override fun filter(tableProperties: TableProperties): Boolean {
            cfNames.add(tableProperties.getColumnFamilyName()!!)
            return true
        }
    }
}
