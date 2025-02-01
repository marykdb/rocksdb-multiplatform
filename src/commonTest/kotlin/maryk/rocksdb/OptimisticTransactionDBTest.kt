package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.*

class OptimisticTransactionDBTest {

    private fun createTestFolder() = createTestDBFolder("OptimisticTransactionDBTest")

    @Test
    fun open() {
        val tempFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, tempFolder).use { otdb ->
                assertNotNull(otdb, "OptimisticTransactionDB should be opened successfully")
            }
        }
    }

    @Test
    fun open_columnFamilies() {
        val tempFolder = createTestFolder()
        DBOptions().setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
            .use { dbOptions ->
                ColumnFamilyOptions().use { cfOpts ->
                    val columnFamilyDescriptors = mutableListOf(
                        ColumnFamilyDescriptor(defaultColumnFamily),
                        ColumnFamilyDescriptor("myCf".encodeToByteArray(), cfOpts)
                    )
                    val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
                    openOptimisticTransactionDB(
                        dbOptions,
                        tempFolder,
                        columnFamilyDescriptors,
                        columnFamilyHandles
                    ).use { otdb ->
                        assertNotNull(otdb, "OptimisticTransactionDB with column families should be opened successfully")
                        columnFamilyHandles.forEach { it.close() }
                    }
                }
            }
    }

    @Test
    fun open_columnFamilies_no_default() {
        val tempFolder = createTestFolder()
        DBOptions().setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
            .use { dbOptions ->
                ColumnFamilyOptions().use { cfOpts ->
                    val columnFamilyDescriptors = mutableListOf(
                        ColumnFamilyDescriptor("myCf".encodeToByteArray(), cfOpts)
                    )
                    val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
                    val exception = assertFailsWith<IllegalArgumentException> {
                        openOptimisticTransactionDB(
                            dbOptions,
                            tempFolder,
                            columnFamilyDescriptors,
                            columnFamilyHandles
                        )
                    }
                    assertNotNull(exception, "Opening without default column family should throw IllegalArgumentException")
                }
            }
    }

    @Test
    fun beginTransaction() {
        val tempFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, tempFolder).use { otdb ->
                WriteOptions().use { writeOptions ->
                    otdb.beginTransaction(writeOptions).use { txn ->
                        assertNotNull(txn, "Transaction should be created successfully")
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_transactionOptions() {
        val tempFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, tempFolder).use { otdb ->
                WriteOptions().use { writeOptions ->
                    OptimisticTransactionOptions().use { txnOptions ->
                        otdb.beginTransaction(writeOptions, txnOptions).use { txn ->
                            assertNotNull(txn, "Transaction with OptimisticTransactionOptions should be created successfully")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_withOld() {
        val tempFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, tempFolder).use { otdb ->
                WriteOptions().use { writeOptions ->
                    otdb.beginTransaction(writeOptions).use { txn ->
                        val txnReused = otdb.beginTransaction(writeOptions, txn)
                        assertSame(txn, txnReused, "Reusing transaction should return the same instance")
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_withOld_transactionOptions() {
        val tempFolder = createTestFolder()
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, tempFolder).use { otdb ->
                WriteOptions().use { writeOptions ->
                    OptimisticTransactionOptions().use { txnOptions ->
                        otdb.beginTransaction(writeOptions).use { txn ->
                            val txnReused = otdb.beginTransaction(writeOptions, txnOptions, txn)
                            assertSame(txn, txnReused, "Reusing transaction with OptimisticTransactionOptions should return the same instance")
                        }
                    }
                }
            }
        }
    }
//
//    @Test
//    fun baseDB() {
//        val tempFolder = createTestFolder()
//        Options().setCreateIfMissing(true).use { options ->
//            openOptimisticTransactionDB(options, tempFolder).use { otdb ->
//                val db = otdb.getBaseDB()
//                assertNotNull(db, "Base RocksDB should not be null")
//                assertFalse(db.isOwningHandle, "Base RocksDB should not be owning handle")
//            }
//        }
//    }

    @Test
    fun otdbSimpleIterator() {
        val tempFolder = createTestFolder()
        Options().setCreateIfMissing(true).setMaxCompactionBytes(0).use { options ->
            openOptimisticTransactionDB(options, tempFolder).use { otdb ->
                otdb.put("keyI".encodeToByteArray(), "valueI".encodeToByteArray())
                otdb.newIterator().use { iterator ->
                    iterator.seekToFirst()
                    assertTrue(iterator.isValid(), "Iterator should be valid after seeking to first")
                    assertContentEquals("keyI".encodeToByteArray(), iterator.key(), "Iterator key should match")
                    assertContentEquals("valueI".encodeToByteArray(), iterator.value(), "Iterator value should match")
                    iterator.next()
                    assertFalse(iterator.isValid(), "Iterator should be invalid after reaching the end")
                }
            }
        }
    }
}
