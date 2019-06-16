package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.StatusCode.TimedOut
import maryk.rocksdb.TransactionState.COMMITED
import maryk.rocksdb.TransactionState.STARTED
import maryk.rocksdb.util.createTestDBFolder
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

class TransactionTest : AbstractTransactionTest() {
    private fun createTestFolder() = createTestDBFolder("TransactionTest")

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
                    assertContentEquals(v1, txn.getForUpdate(readOptions, testCf, k1, true))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, testCf, k1, true))

                        // NOTE: txn2 updates k1, during txn3
                        try {
                            txn2.put(testCf, k1, v12) // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(TimedOut, e.getStatus()?.getCode())
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
                    assertContentEquals(v1, txn.getForUpdate(readOptions, k1, true))
                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    dbContainer.beginTransaction().use { txn3 ->
                        assertContentEquals(v1, txn3.getForUpdate(readOptions, k1, true))

                        // NOTE: txn2 updates k1, during txn3
                        try {
                            txn2.put(k1, v12) // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(TimedOut, e.getStatus()?.getCode())
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
                        try {
                            txn2.put(testCf, keys[0], otherValue) // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(TimedOut, e.getStatus()?.getCode())
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
                        try {
                            txn2.put(keys[0], otherValue) // should cause an exception!
                        } catch (e: RocksDBException) {
                            assertSame(TimedOut, e.getStatus()?.getCode())
                            return
                        }
                    }
                }

                fail("Expected an exception for put after getForUpdate from conflicting" + "transactions")
            }
        }
    }

    @Test
    fun name() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertTrue(txn.getName().isEmpty())
                val name = "my-transaction-" + Random.nextLong()
                txn.setName(name)
                assertEquals(name, txn.getName())
            }
        }
    }

    @Test
    fun id() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertTrue(0 < txn.getID())
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
                assertSame(STARTED, txn.getState())
                txn.commit()
                assertSame(COMMITED, txn.getState())
            }

            dbContainer.beginTransaction().use { txn ->
                assertSame(STARTED, txn.getState())
                txn.rollback()
                assertSame(STARTED, txn.getState())
            }
        }
    }

    @Test
    fun id2() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertNotNull(txn.getId())
            }
        }
    }

    override fun startDb(): DBContainer {
        val options = DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }
        val txnDbOptions = TransactionDBOptions()
        val columnFamilyOptions = ColumnFamilyOptions()
        val columnFamilyDescriptors = listOf(
            ColumnFamilyDescriptor(defaultColumnFamily),
            ColumnFamilyDescriptor(
                TXN_TEST_COLUMN_FAMILY,
                columnFamilyOptions
            )
        )
        val columnFamilyHandles = ArrayList<ColumnFamilyHandle>()

        val txnDb: TransactionDB
        try {
            txnDb = openTransactionDB(
                options, txnDbOptions,
                createTestFolder(),
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
            txnOptions, writeOptions,
            columnFamilyHandles, txnDb, txnDbOptions, columnFamilyOptions, options
        )
    }

    private class TransactionDBContainer(
        private val txnOptions: TransactionOptions, writeOptions: WriteOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        private val txnDb: TransactionDB, private val txnDbOptions: TransactionDBOptions,
        columnFamilyOptions: ColumnFamilyOptions,
        options: DBOptions
    ) : DBContainer(writeOptions, columnFamilyHandles, columnFamilyOptions, options) {
        override fun beginTransaction(): Transaction {
            return txnDb.beginTransaction(writeOptions, txnOptions)
        }

        override fun beginTransaction(writeOptions: WriteOptions): Transaction {
            return txnDb.beginTransaction(writeOptions, txnOptions)
        }

        override fun close() {
            txnOptions.close()
            writeOptions.close()
            for (columnFamilyHandle in columnFamilyHandles) {
                columnFamilyHandle.close()
            }
            txnDb.close()
            txnDbOptions.close()
            options.close()
        }
    }

}
