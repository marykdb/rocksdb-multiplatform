package maryk.rocksdb

import maryk.assertContainsExactly
import kotlin.random.Random
import kotlin.test.*

class TransactionTest : AbstractTransactionTest() {

    @Test
    fun getForUpdate_cf_conflict() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            val readOptions = ReadOptions()
            val testCf = dbContainer.getTestColumnFamily()

            dbContainer.beginTransaction().use { txn ->
                txn.put(testCf, k1, v1)
                assertContentEquals(v1, txn.getForUpdate(readOptions, testCf, k1, true))
                txn.commit()
            }

            dbContainer.beginTransaction().use { txn2 ->
                dbContainer.beginTransaction().use { txn3 ->
                    assertContentEquals(v1, txn3.getForUpdate(readOptions, testCf, k1, true))

                    // NOTE: txn2 updates k1, during txn3
                    try {
                        txn2.put(testCf, k1, v12) // should cause an exception!
                        fail("Expected RocksDBException due to conflict")
                    } catch (e: RocksDBException) {
                        assertEquals(StatusCode.TimedOut, e.getStatus()?.getCode())
                        return
                    }
                }
            }

            fail("Expected an exception for put after getForUpdate from conflicting transactions")
        }
    }

    @Test
    fun prepare_commit() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            val readOptions = ReadOptions()

            dbContainer.beginTransaction().use { txn ->
                txn.put(k1, v1)
                txn.commit()
            }

            dbContainer.beginTransaction().use { txn ->
                txn.setName("txnPrepare1")
                txn.put(k1, v12)
                txn.prepare()
                txn.commit()
            }

            dbContainer.beginTransaction().use { txn ->
                assertContentEquals(v12, txn.get(readOptions, k1))
            }
        }
    }

    @Test
    fun prepare_rollback() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            val readOptions = ReadOptions()

            dbContainer.beginTransaction().use { txn ->
                txn.put(k1, v1)
                txn.commit()
            }

            dbContainer.beginTransaction().use { txn ->
                txn.setName("txnPrepare1")
                txn.put(k1, v12)
                txn.prepare()
                txn.rollback()
            }

            dbContainer.beginTransaction().use { txn ->
                assertContentEquals(v1, txn.get(readOptions, k1))
            }
        }
    }

    @Test
    fun prepare_read_prepared_commit() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    txn.commit()
                }

                dbContainer.beginTransaction().apply {
                    setName("txnPrepare1")
                    put(k1, v12)
                    prepare()
                }.use { txnPrepare ->
                    dbContainer.beginTransaction().use { txn ->
                        assertContentEquals(v1, txn.get(readOptions, k1))
                    }

                    txnPrepare.commit()

                    dbContainer.beginTransaction().use { txn ->
                        assertContentEquals(v12, txn.get(readOptions, k1))
                    }
                }
            }
        }
    }

    @Test
    fun prepare_read_prepared_rollback() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            val readOptions = ReadOptions()

            dbContainer.beginTransaction().use { txn ->
                txn.put(k1, v1)
                txn.commit()
            }

            val txnPrepare = dbContainer.beginTransaction()
            txnPrepare.use {
                it.setName("txnPrepare1")
                it.put(k1, v12)
                it.prepare()
                it.rollback()
            }

            dbContainer.beginTransaction().use { txn ->
                assertContentEquals(v1, txn.get(readOptions, k1))
            }
        }
    }

    @Test
    fun getForUpdate_conflict() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val v12 = "value12".encodeToByteArray()

        startDb().use { dbContainer ->
            val readOptions = ReadOptions()

            dbContainer.beginTransaction().use { txn ->
                txn.put(k1, v1)
                assertContentEquals(v1, txn.getForUpdate(readOptions, k1, true))
                txn.commit()
            }

            dbContainer.beginTransaction().use { txn2 ->
                dbContainer.beginTransaction().use { txn3 ->
                    assertContentEquals(v1, txn3.getForUpdate(readOptions, k1, true))

                    // NOTE: txn2 updates k1, during txn3
                    try {
                        txn2.put(k1, v12) // should cause an exception!
                        fail("Expected RocksDBException due to conflict")
                    } catch (e: RocksDBException) {
                        assertEquals(StatusCode.TimedOut, e.getStatus()?.getCode())
                        return
                    }
                }
            }

            fail("Expected an exception for put after getForUpdate from conflicting transactions")
        }
    }

    @Test
    fun multiGetAsListForUpdate_cf_conflict() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf("value1".encodeToByteArray(), "value2".encodeToByteArray())
        val otherValue = "otherValue".encodeToByteArray()

        startDb().use { dbContainer ->
            val readOptions = ReadOptions()
            val testCf = dbContainer.getTestColumnFamily()
            val cfList = listOf(testCf, testCf)

            dbContainer.beginTransaction().use { txn ->
                txn.put(testCf, keys[0], values[0])
                txn.put(testCf, keys[1], values[1])
                assertContainsExactly(values.toList(), txn.multiGetAsList(readOptions, cfList, keys.toList()))
                txn.commit()
            }

            dbContainer.beginTransaction().use { txn2 ->
                dbContainer.beginTransaction().use { txn3 ->
                    assertContainsExactly(values.toList(), txn3.multiGetForUpdateAsList(readOptions, cfList, keys.toList()))

                    // NOTE: txn2 updates k1, during txn3
                    try {
                        txn2.put(testCf, keys[0], otherValue) // should cause an exception!
                        fail("Expected RocksDBException due to conflict")
                    } catch (e: RocksDBException) {
                        assertEquals(StatusCode.TimedOut, e.getStatus()?.getCode())
                        return
                    }
                }
            }

            fail("Expected an exception for put after getForUpdate from conflicting transactions")
        }
    }

    @Test
    fun multiGetAsListForUpdate_conflict() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf("value1".encodeToByteArray(), "value2".encodeToByteArray())
        val otherValue = "otherValue".encodeToByteArray()

        startDb().use { dbContainer ->
            val readOptions = ReadOptions()

            dbContainer.beginTransaction().use { txn ->
                txn.put(keys[0], values[0])
                txn.put(keys[1], values[1])
                assertContainsExactly(values.toList(), txn.multiGetAsList(readOptions, keys.toList()))
                txn.commit()
            }

            dbContainer.beginTransaction().use { txn2 ->
                dbContainer.beginTransaction().use { txn3 ->
                    assertContainsExactly(values.toList(), txn3.multiGetForUpdateAsList(readOptions, keys.toList()))

                    // NOTE: txn2 updates k1, during txn3
                    try {
                        txn2.put(keys[0], otherValue) // should cause an exception!
                        fail("Expected RocksDBException due to conflict")
                    } catch (e: RocksDBException) {
                        assertEquals(StatusCode.TimedOut, e.getStatus()?.getCode())
                        return
                    }
                }
            }

            fail("Expected an exception for put after getForUpdate from conflicting transactions")
        }
    }

    @Test
    fun name() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertTrue(txn.getName().isEmpty())
                val name = "my-transaction-${Random.nextLong()}"
                txn.setName(name)
                assertEquals(name, txn.getName())
            }
        }
    }

    @Test
    fun ID() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertTrue(txn.getID() > 0)
            }
        }
    }

    @Test
    fun deadlockDetect() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertFalse(txn.isDeadlockDetect())
            }
        }
    }

    @Test
    fun waitingTxns() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertEquals(0, txn.getWaitingTxns().getTransactionIds().size)
            }
        }
    }

    @Test
    fun state() {
        startDb().use { dbContainer ->

            dbContainer.beginTransaction().use { txn ->
                assertEquals(TransactionState.STARTED, txn.getState())
                txn.commit()
                assertEquals(TransactionState.COMMITTED, txn.getState())
            }

            dbContainer.beginTransaction().use { txn ->
                assertEquals(TransactionState.STARTED, txn.getState())
                txn.rollback()
                // Depending on RocksDB's behavior, the state after rollback might still be STARTED or another state.
                // Adjust the expected state accordingly.
                assertEquals(TransactionState.STARTED, txn.getState())
            }
        }
    }

    @Test
    fun Id() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertNotNull(txn.getID())
            }
        }
    }

    override fun startDb(): TransactionDBContainer {
        val options = DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
        val txnDbOptions = TransactionDBOptions()
        val defaultColumnFamilyOptions = ColumnFamilyOptions()
            .setMergeOperator(StringAppendOperator("++"))
        val columnFamilyOptions = ColumnFamilyOptions()
            .setMergeOperator(StringAppendOperator("**"))

        val columnFamilyDescriptors = listOf(
            ColumnFamilyDescriptor(defaultColumnFamily, defaultColumnFamilyOptions),
            ColumnFamilyDescriptor(TXN_TEST_COLUMN_FAMILY, columnFamilyOptions)
        )

        val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()

        val txnDb = try {
            openTransactionDB(
                options,
                txnDbOptions,
                dbFolder,
                columnFamilyDescriptors,
                columnFamilyHandles
            )
        } catch (e: RocksDBException) {
            columnFamilyOptions.close()
            txnDbOptions.close()
            options.close()
            throw e
        }

        val writeOptions = WriteOptions()
        val txnOptions = TransactionOptions()

        return TransactionDBContainer(
            txnOptions,
            writeOptions,
            columnFamilyHandles,
            txnDb,
            txnDbOptions,
            columnFamilyOptions,
            options
        )
    }

    class TransactionDBContainer(
        val txnOptions: TransactionOptions,
        writeOptions: WriteOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        val txnDb: TransactionDB,
        val txnDbOptions: TransactionDBOptions,
        columnFamilyOptions: ColumnFamilyOptions,
        options: DBOptions
    ) : DBContainer(
        writeOptions = writeOptions,
        columnFamilyHandles = columnFamilyHandles,
        columnFamilyOptions = columnFamilyOptions,
        options = options,
    ) {
        override fun beginTransaction(): Transaction {
            return txnDb.beginTransaction(writeOptions, txnOptions)
        }

        override fun beginTransaction(writeOptions: WriteOptions): Transaction {
            return txnDb.beginTransaction(writeOptions, txnOptions)
        }

        override fun close() {
            txnOptions.close()
            writeOptions.close()
            columnFamilyHandles.forEach { it.close() }
            txnDb.close()
            txnDbOptions.close()
            columnFamilyOptions.close()
            options.close()
        }
    }
}
