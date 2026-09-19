package maryk.rocksdb

import maryk.assertContainsExactly
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OptimisticTransactionTest : AbstractTransactionTest() {

    @Test
    fun prepare_commit() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                // First, write an initial value.
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    txn.commit()
                }

                // Now, attempt a transaction that calls prepare().
                // Optimistic transactions do not support two-phase commit.
                val exception = assertFailsWith<RocksDBException> {
                    dbContainer.beginTransaction().use { txn ->
                        txn.put(k1, v12)
                        txn.prepare() // Should throw an exception.
                    }
                }
                assertTrue(
                    exception.message?.contains("Two phase commit not supported for optimistic transactions") == true,
                    "Expected error message to mention two phase commit not supported"
                )
            }
        }
    }

    @Test
    fun getForUpdate_cf_conflict() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                val testCf = dbContainer.getTestColumnFamily()

                // Write initial value in the column family.
                dbContainer.beginTransaction().use { txn ->
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, testCf, k1))
                    txn.commit()
                }

                // Start two transactions: txn3 locks the key and txn2 will update it.
                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, testCf, k1, true))

                        // txn2 updates the same key.
                        txn2.put(testCf, k1, v12)
                        assertContentEquals(v12, txn2.get(readOptions, testCf, k1))
                        txn2.commit()

                        // When txn3 commits, it should detect the conflict.
                        val exception = assertFailsWith<RocksDBException> {
                            txn3.commit()
                        }
                        assertEquals(StatusCode.Busy, exception.getStatus()?.getCode())
                    }
                }
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
                // Write initial value in the default column family.
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))
                    txn.commit()
                }

                // Start two transactions: txn3 gets the key for update and txn2 modifies it.
                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, k1, true))

                        // txn2 updates the key.
                        txn2.put(k1, v12)
                        assertContentEquals(v12, txn2.get(readOptions, k1))
                        txn2.commit()

                        // Committing txn3 should now throw a conflict exception.
                        val exception = assertFailsWith<RocksDBException> {
                            txn3.commit()
                        }
                        assertEquals(StatusCode.Busy, exception.getStatus()?.getCode())
                    }
                }
            }
        }
    }

    @Test
    fun multiGetAsListForUpdate_cf_conflict() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf("value1".encodeToByteArray(), "value2".encodeToByteArray())
        val otherValue = "otherValue".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                val testCf = dbContainer.getTestColumnFamily()
                val cfList = listOf(testCf, testCf)

                // Write initial values.
                dbContainer.beginTransaction().use { txn ->
                    txn.put(testCf, keys[0], values[0])
                    txn.put(testCf, keys[1], values[1])
                    assertContainsExactly(values.toList(), txn.multiGetAsList(readOptions, cfList, keys.toList()))
                    txn.commit()
                }

                // Start two transactions to create a conflict.
                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContainsExactly(values.toList(), txn3.multiGetForUpdateAsList(readOptions, cfList, keys.toList()))

                        // txn2 updates one key.
                        txn2.put(testCf, keys[0], otherValue)
                        assertContentEquals(otherValue, txn2.get(readOptions, testCf, keys[0]))
                        txn2.commit()

                        val exception = assertFailsWith<RocksDBException> {
                            txn3.commit()
                        }
                        assertEquals(StatusCode.Busy, exception.getStatus()?.getCode())
                    }
                }
            }
        }
    }

    @Test
    fun multiGetAsListForUpdate_conflict() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf("value1".encodeToByteArray(), "value2".encodeToByteArray())
        val otherValue = "otherValue".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                // Write initial values in the default column family.
                dbContainer.beginTransaction().use { txn ->
                    txn.put(keys[0], values[0])
                    txn.put(keys[1], values[1])
                    assertContainsExactly(values.toList(), txn.multiGetAsList(readOptions, keys.toList()))
                    txn.commit()
                }

                // Create conflict.
                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContainsExactly(values.toList(), txn3.multiGetForUpdateAsList(readOptions, keys.toList()))

                        txn2.put(keys[0], otherValue)
                        assertContentEquals(otherValue, txn2.get(readOptions, keys[0]))
                        txn2.commit()

                        val exception = assertFailsWith<RocksDBException> {
                            txn3.commit()
                        }
                        assertEquals(StatusCode.Busy, exception.getStatus()?.getCode())
                    }
                }
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
                val testCf = dbContainer.getTestColumnFamily()

                // Write an initial value.
                dbContainer.beginTransaction().use { txn ->
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, testCf, k1))
                    txn.commit()
                }

                // Begin two transactions where one will undo its lock.
                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, testCf, k1, true))
                        // Undo the getForUpdate so that this txn no longer holds the lock.
                        txn3.undoGetForUpdate(testCf, k1)

                        txn2.put(testCf, k1, v12)
                        assertContentEquals(v12, txn2.get(readOptions, testCf, k1))
                        txn2.commit()

                        // Since the get was undone, txn3.commit() should succeed.
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
                // Write an initial value.
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))
                    txn.commit()
                }

                // Begin two transactions; one will undo its get-for-update.
                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, k1, true))
                        txn3.undoGetForUpdate(k1)

                        txn2.put(k1, v12)
                        assertContentEquals(v12, txn2.get(readOptions, k1))
                        txn2.commit()

                        // With the lock undone, txn3.commit() should succeed.
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
                val name = "my-transaction-${Random.nextLong()}"
                val exception = assertFailsWith<RocksDBException> {
                    txn.setName(name)
                }
                assertEquals(StatusCode.InvalidArgument, exception.getStatus()?.getCode())
            }
        }
    }

    override fun startDb(): OptimisticTransactionDBContainer {
        val options = DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)

        val defaultCfOptions = ColumnFamilyOptions().apply {
            setMergeOperator(StringAppendOperator("++"))
        }
        val testCfOptions = ColumnFamilyOptions().apply {
            setMergeOperator(StringAppendOperator("**"))
        }
        val columnFamilyDescriptors = listOf(
            ColumnFamilyDescriptor(defaultColumnFamily, defaultCfOptions),
            ColumnFamilyDescriptor(TXN_TEST_COLUMN_FAMILY, testCfOptions)
        )
        val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()

        val optimisticTxnDb = try {
            openOptimisticTransactionDB(
                options,
                dbFolder,
                columnFamilyDescriptors,
                columnFamilyHandles
            )
        } catch (e: RocksDBException) {
            testCfOptions.close()
            options.close()
            throw e
        }

        val writeOptions = WriteOptions()
        val optimisticTxnOptions = OptimisticTransactionOptions()

        return OptimisticTransactionDBContainer(
            optimisticTxnOptions,
            writeOptions,
            columnFamilyHandles,
            optimisticTxnDb,
            testCfOptions,
            options
        )
    }

    class OptimisticTransactionDBContainer(
        private val optimisticTxnOptions: OptimisticTransactionOptions,
        writeOptions: WriteOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        private val optimisticTxnDb: OptimisticTransactionDB,
        columnFamilyOptions: ColumnFamilyOptions,
        options: DBOptions
    ) : DBContainer(writeOptions, columnFamilyHandles, columnFamilyOptions, options) {

        override fun beginTransaction(): Transaction =
            optimisticTxnDb.beginTransaction(writeOptions, optimisticTxnOptions)

        override fun beginTransaction(writeOptions: WriteOptions): Transaction =
            optimisticTxnDb.beginTransaction(writeOptions, optimisticTxnOptions)

        override fun close() {
            optimisticTxnOptions.close()
            writeOptions.close()
            columnFamilyHandles.forEach { it.close() }
            optimisticTxnDb.close()
            options.close()
        }
    }
}
