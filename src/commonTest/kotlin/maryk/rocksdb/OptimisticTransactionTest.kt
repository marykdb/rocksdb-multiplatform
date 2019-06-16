package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.StatusCode.Busy
import maryk.rocksdb.StatusCode.InvalidArgument
import maryk.rocksdb.util.createTestDBFolder
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

class OptimisticTransactionTest : AbstractTransactionTest() {
    private fun createTestFolder() = createTestDBFolder("OptimisticTransactionTest")

    @Test
    fun getForUpdate_cf_conflict() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                val testCf = dbContainer.testColumnFamily

                dbContainer.beginTransaction().use { txn ->
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(testCf, readOptions, k1))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, testCf, k1, true))

                        // NOTE: txn2 updates k1, during txn3
                        txn2.put(testCf, k1, v12)
                        assertContentEquals(v12, txn2.get(testCf, readOptions, k1))
                        txn2.commit()

                        try {
                            txn3.commit() // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(Busy, e.getStatus()?.getCode())
                            return
                        }
                    }
                }

                fail("Expected an exception for put after getForUpdate from conflicting transactions")
            }
        }
    }

    @Test
    fun getForUpdate_conflict() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, k1, true))

                        // NOTE: txn2 updates k1, during txn3
                        txn2.put(k1, v12)
                        assertContentEquals(v12, txn2.get(readOptions, k1))
                        txn2.commit()

                        try {
                            txn3.commit() // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(Busy, e.getStatus()?.getCode())
                            return
                        }
                    }
                }

                fail("Expected an exception for put after getForUpdate from conflicting" + "transactions")
            }
        }
    }

    @Test
    fun multiGetForUpdate_cf_conflict() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())
        val otherValue = "otherValue".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                val testCf = dbContainer.testColumnFamily
                val cfList = listOf(testCf, testCf)

                dbContainer.beginTransaction().use { txn ->
                    txn.put(testCf, keys[0], values[0]!!)
                    txn.put(testCf, keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGet(readOptions, cfList, keys))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(values, txn3.multiGetForUpdate(readOptions, cfList, keys))

                        // NOTE: txn2 updates k1, during txn3
                        txn2.put(testCf, keys[0], otherValue)
                        assertContentEquals(otherValue, txn2.get(testCf, readOptions, keys[0]))
                        txn2.commit()

                        try {
                            txn3.commit() // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(Busy, e.getStatus()?.getCode())
                            return
                        }
                    }
                }

                fail("Expected an exception for put after getForUpdate from conflicting" + "transactions")
            }
        }
    }

    @Test
    fun multiGetForUpdate_conflict() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())
        val otherValue = "otherValue".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(keys[0], values[0]!!)
                    txn.put(keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGet(readOptions, keys))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(values, txn3.multiGetForUpdate(readOptions, keys))

                        // NOTE: txn2 updates k1, during txn3
                        txn2.put(keys[0], otherValue)
                        assertContentEquals(otherValue, txn2.get(readOptions, keys[0]))
                        txn2.commit()

                        try {
                            txn3.commit() // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(Busy, e.getStatus()?.getCode())
                            return
                        }
                    }
                }

                fail("Expected an exception for put after getForUpdate from conflicting" + "transactions")
            }
        }
    }

    @Test
    fun undoGetForUpdate_cf_conflict() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                val testCf = dbContainer.testColumnFamily

                dbContainer.beginTransaction().use { txn ->
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(testCf, readOptions, k1))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, testCf, k1, true))

                        // undo the getForUpdate
                        txn3.undoGetForUpdate(testCf, k1)

                        // NOTE: txn2 updates k1, during txn3
                        txn2.put(testCf, k1, v12)
                        assertContentEquals(v12, txn2.get(testCf, readOptions, k1))
                        txn2.commit()

                        // should not cause an exception
                        // because we undid the getForUpdate above!
                        txn3.commit()
                    }
                }
            }
        }
    }

    @Test
    fun undoGetForUpdate_conflict() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->

                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, k1, true))

                        // undo the getForUpdate
                        txn3.undoGetForUpdate(k1)

                        // NOTE: txn2 updates k1, during txn3
                        txn2.put(k1, v12)
                        assertContentEquals(v12, txn2.get(readOptions, k1))
                        txn2.commit()

                        // should not cause an exception
                        // because we undid the getForUpdate above!
                        txn3.commit()
                    }
                }
            }
        }
    }

    @Test
    fun name() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertTrue(txn.getName().isEmpty())
                val name = "my-transaction-" + Random.nextLong()

                try {
                    txn.setName(name)
                } catch (e: RocksDBException) {
                    assertSame(InvalidArgument, e.getStatus()?.getCode())
                    return
                }

                fail("Optimistic transactions cannot be named.")
            }
        }
    }

    override fun startDb(): DBContainer {
        val options = DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)

        val columnFamilyOptions = ColumnFamilyOptions()
        val columnFamilyDescriptors = listOf(
            ColumnFamilyDescriptor(defaultColumnFamily),
            ColumnFamilyDescriptor(
                TXN_TEST_COLUMN_FAMILY,
                columnFamilyOptions
            )
        )
        val columnFamilyHandles = ArrayList<ColumnFamilyHandle>()

        val optimisticTxnDb: OptimisticTransactionDB
        try {
            optimisticTxnDb = openOptimisticTransactionDB(
                options,
                createTestFolder(),
                columnFamilyDescriptors, columnFamilyHandles
            )
        } catch (e: RocksDBException) {
            columnFamilyOptions.close()
            options.close()
            throw e
        }

        val writeOptions = WriteOptions()
        val optimisticTxnOptions = OptimisticTransactionOptions()

        return OptimisticTransactionDBContainer(
            optimisticTxnOptions,
            writeOptions, columnFamilyHandles, optimisticTxnDb, columnFamilyOptions,
            options
        )
    }

    private class OptimisticTransactionDBContainer(
        private val optimisticTxnOptions: OptimisticTransactionOptions,
        writeOptions: WriteOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        private val optimisticTxnDb: OptimisticTransactionDB,
        columnFamilyOptions: ColumnFamilyOptions,
        options: DBOptions
    ) : DBContainer(writeOptions, columnFamilyHandles, columnFamilyOptions, options) {

        override fun beginTransaction(): Transaction {
            return optimisticTxnDb.beginTransaction(
                writeOptions,
                optimisticTxnOptions
            )
        }

        override fun beginTransaction(writeOptions: WriteOptions): Transaction {
            return optimisticTxnDb.beginTransaction(
                writeOptions,
                optimisticTxnOptions
            )
        }

        override fun close() {
            optimisticTxnOptions.close()
            writeOptions.close()
            for (columnFamilyHandle in columnFamilyHandles) {
                columnFamilyHandle.close()
            }
            optimisticTxnDb.close()
            options.close()
        }
    }
}
