package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ReadOnlyTest {
    private fun createTestFolder() = createTestDBFolder("ReadOnlyTest")

    @Test
    fun readOnlyOpen() {
        val testFolder = createTestFolder()
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openRocksDB(
                options,
                testFolder
            ).use { db ->
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                openReadOnlyRocksDB(
                    testFolder
                ).use { db2 ->
                    assertContentEquals("value".encodeToByteArray(), db2["key".encodeToByteArray()])
                }
            }
        }

        ColumnFamilyOptions().use { cfOpts ->
            val cfDescriptors = mutableListOf(
                ColumnFamilyDescriptor(
                    defaultColumnFamily, cfOpts
                )
            )

            val columnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
            openRocksDB(
                testFolder,
                cfDescriptors,
                columnFamilyHandleList
            ).use { db ->
                try {
                    ColumnFamilyOptions().use { newCfOpts ->
                        ColumnFamilyOptions().use { newCf2Opts ->
                            columnFamilyHandleList.add(
                                db.createColumnFamily(
                                    ColumnFamilyDescriptor("new_cf".encodeToByteArray(), newCfOpts)
                                )
                            )
                            columnFamilyHandleList.add(
                                db.createColumnFamily(
                                    ColumnFamilyDescriptor("new_cf2".encodeToByteArray(), newCf2Opts)
                                )
                            )
                            db.put(
                                columnFamilyHandleList[2], "key2".encodeToByteArray(),
                                "value2".encodeToByteArray()
                            )

                            val readOnlyColumnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
                            openReadOnlyRocksDB(
                                testFolder, cfDescriptors,
                                readOnlyColumnFamilyHandleList
                            ).use { db2 ->
                                try {
                                    ColumnFamilyOptions().use { newCfOpts2 ->
                                        ColumnFamilyOptions().use { newCf2Opts2 ->
                                            assertNull(db2["key2".encodeToByteArray()])
                                            assertNull(
                                                db2.get(
                                                    readOnlyColumnFamilyHandleList[0],
                                                    "key2".encodeToByteArray()
                                                )
                                            )
                                            cfDescriptors.clear()
                                            cfDescriptors.add(
                                                ColumnFamilyDescriptor(
                                                    defaultColumnFamily,
                                                    newCfOpts2
                                                )
                                            )
                                            cfDescriptors.add(
                                                ColumnFamilyDescriptor(
                                                    "new_cf2".encodeToByteArray(),
                                                    newCf2Opts2
                                                )
                                            )

                                            val readOnlyColumnFamilyHandleList2 = mutableListOf<ColumnFamilyHandle>()
                                            openReadOnlyRocksDB(
                                                testFolder, cfDescriptors,
                                                readOnlyColumnFamilyHandleList2
                                            ).use { db3 ->
                                                try {
                                                    assertContentEquals(
                                                        "value2".encodeToByteArray(),
                                                        db3.get(
                                                            readOnlyColumnFamilyHandleList2[1],
                                                            "key2".encodeToByteArray()
                                                        )
                                                    )
                                                } finally {
                                                    for (columnFamilyHandle in readOnlyColumnFamilyHandleList2) {
                                                        columnFamilyHandle.close()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } finally {
                                    for (columnFamilyHandle in readOnlyColumnFamilyHandleList) {
                                        columnFamilyHandle.close()
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    for (columnFamilyHandle in columnFamilyHandleList) {
                        columnFamilyHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun failToWriteInReadOnly() {
        val testFolder = createTestFolder()
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openRocksDB(
                options,
                testFolder
            ).use {
                //no-op
            }
        }

        ColumnFamilyOptions().use { cfOpts ->
            val cfDescriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily, cfOpts)
            )

            val readOnlyColumnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
            openReadOnlyRocksDB(
                testFolder, cfDescriptors,
                readOnlyColumnFamilyHandleList
            ).use { rDb ->
                try {
                    assertFailsWith<RocksDBException> {
                        // test that put fails in readonly mode
                        rDb.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    }
                } finally {
                    for (columnFamilyHandle in readOnlyColumnFamilyHandleList) {
                        columnFamilyHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun failToCFWriteInReadOnly() {
        val testFolder = createTestFolder()
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openRocksDB(
                options,
                testFolder
            ).use {
                //no-op
            }
        }

        ColumnFamilyOptions().use { cfOpts ->
            val cfDescriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily, cfOpts)
            )
            val readOnlyColumnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
            openReadOnlyRocksDB(
                testFolder, cfDescriptors,
                readOnlyColumnFamilyHandleList
            ).use { rDb ->
                try {
                    assertFailsWith<RocksDBException> {
                        rDb.put(
                            readOnlyColumnFamilyHandleList[0],
                            "key".encodeToByteArray(), "value".encodeToByteArray()
                        )
                    }
                } finally {
                    for (columnFamilyHandle in readOnlyColumnFamilyHandleList) {
                        columnFamilyHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun failToRemoveInReadOnly() {
        val testFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                testFolder
            ).use {
                //no-op
            }
        }

        ColumnFamilyOptions().use { cfOpts ->
            val cfDescriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily, cfOpts)
            )

            val readOnlyColumnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()

            openReadOnlyRocksDB(
                testFolder, cfDescriptors,
                readOnlyColumnFamilyHandleList
            ).use { rDb ->
                try {
                    assertFailsWith<RocksDBException> {
                        rDb.delete("key".encodeToByteArray())
                    }
                } finally {
                    for (columnFamilyHandle in readOnlyColumnFamilyHandleList) {
                        columnFamilyHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun failToCFRemoveInReadOnly() {
        val testFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                testFolder
            ).use {
                //no-op
            }
        }

        ColumnFamilyOptions().use { cfOpts ->
            val cfDescriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily, cfOpts)
            )

            val readOnlyColumnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
            openReadOnlyRocksDB(
                testFolder, cfDescriptors,
                readOnlyColumnFamilyHandleList
            ).use { rDb ->
                try {
                    assertFailsWith<RocksDBException> {
                        rDb.delete(
                            readOnlyColumnFamilyHandleList[0],
                            "key".encodeToByteArray()
                        )
                    }
                } finally {
                    for (columnFamilyHandle in readOnlyColumnFamilyHandleList) {
                        columnFamilyHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun failToWriteBatchReadOnly() {
        val testFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                testFolder
            ).use {
                //no-op
            }
        }

        ColumnFamilyOptions().use { cfOpts ->
            val cfDescriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily, cfOpts)
            )

            val readOnlyColumnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
            openReadOnlyRocksDB(
                testFolder, cfDescriptors,
                readOnlyColumnFamilyHandleList
            ).use { rDb ->
                WriteBatch().use { wb ->
                    WriteOptions().use { wOpts ->
                        try {
                            wb.put("key".encodeToByteArray(), "value".encodeToByteArray())
                            assertFailsWith<RocksDBException> {
                                rDb.write(wOpts, wb)
                            }
                        } finally {
                            for (columnFamilyHandle in readOnlyColumnFamilyHandleList) {
                                columnFamilyHandle.close()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun failToCFWriteBatchReadOnly() {
        val testFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                testFolder
            ).use {
                //no-op
            }
        }

        ColumnFamilyOptions().use { cfOpts ->
            val cfDescriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily, cfOpts)
            )

            val readOnlyColumnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
            openReadOnlyRocksDB(
                testFolder, cfDescriptors,
                readOnlyColumnFamilyHandleList
            ).use { rDb ->
                WriteBatch().use { wb ->
                    WriteOptions().use { wOpts ->
                        try {
                            wb.put(
                                readOnlyColumnFamilyHandleList[0], "key".encodeToByteArray(),
                                "value".encodeToByteArray()
                            )
                            assertFailsWith<RocksDBException> {
                                rDb.write(wOpts, wb)
                            }
                        } finally {
                            for (columnFamilyHandle in readOnlyColumnFamilyHandleList) {
                                columnFamilyHandle.close()
                            }
                        }
                    }
                }
            }
        }
    }
}
