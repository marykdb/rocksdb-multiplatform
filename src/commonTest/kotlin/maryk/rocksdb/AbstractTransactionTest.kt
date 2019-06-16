package maryk.rocksdb

import maryk.assertContentEquals
import maryk.currentTimeMillis
import maryk.encodeToByteArray
import maryk.sleep
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

val TXN_TEST_COLUMN_FAMILY = "txn_test_cf".encodeToByteArray()

/**
 * Base class of [TransactionTest] and [OptimisticTransactionTest]
 */
abstract class AbstractTransactionTest {
    protected abstract fun startDb(): DBContainer

    @Test
    fun setSnapshot() {
        startDb().use { dbContainer -> dbContainer.beginTransaction().use { txn -> txn.setSnapshot() } }
    }

    @Test
    fun setSnapshotOnNextOperation() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.setSnapshotOnNextOperation()
                txn.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
            }
        }
    }

    @Test
    fun setSnapshotOnNextOperation_transactionNotifier() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->

                TestTransactionNotifier().use { notifier ->
                    txn.setSnapshotOnNextOperation(notifier)
                    txn.put("key1".encodeToByteArray(), "value1".encodeToByteArray())

                    txn.setSnapshotOnNextOperation(notifier)
                    txn.put("key2".encodeToByteArray(), "value2".encodeToByteArray())

                    assertEquals(2, notifier.getCreatedSnapshots().size)
                }
            }
        }
    }

    @Test
    fun getSnapshot() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.setSnapshot()
                val snapshot = txn.getSnapshot()
                assertFalse(snapshot!!.isOwningHandle())
            }
        }
    }

    @Test
    fun getSnapshot_null() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                val snapshot = txn.getSnapshot()
                assertNull(snapshot)
            }
        }
    }

    @Test
    fun clearSnapshot() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.setSnapshot()
                txn.clearSnapshot()
            }
        }
    }

    @Test
    fun clearSnapshot_none() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.clearSnapshot()
            }
        }
    }

    @Test
    fun commit() {
        val k1 = "rollback-key1".encodeToByteArray()
        val v1 = "rollback-value1".encodeToByteArray()
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.put(k1, v1)
                txn.commit()
            }

            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn2 ->
                    assertContentEquals(v1, txn2.get(readOptions, k1))
                }
            }
        }
    }

    @Test
    fun rollback() {
        val k1 = "rollback-key1".encodeToByteArray()
        val v1 = "rollback-value1".encodeToByteArray()
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.put(k1, v1)
                txn.rollback()
            }

            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn2 ->
                    assertNull(txn2.get(readOptions, k1))
                }
            }
        }
    }

    @Test
    fun savePoint() {
        val k1 = "savePoint-key1".encodeToByteArray()
        val v1 = "savePoint-value1".encodeToByteArray()
        val k2 = "savePoint-key2".encodeToByteArray()
        val v2 = "savePoint-value2".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)

                    assertContentEquals(v1, txn.get(readOptions, k1))

                    txn.setSavePoint()

                    txn.put(k2, v2)

                    assertContentEquals(v1, txn.get(readOptions, k1))
                    assertContentEquals(v2, txn.get(readOptions, k2))

                    txn.rollbackToSavePoint()

                    assertContentEquals(v1, txn.get(readOptions, k1))
                    assertNull(txn.get(readOptions, k2))

                    txn.commit()
                }

                dbContainer.beginTransaction().use { txn2 ->
                    assertContentEquals(v1, txn2.get(readOptions, k1))
                    assertNull(txn2.get(readOptions, k2))
                }
            }
        }
    }

    @Test
    fun getPut_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    assertNull(txn.get(testCf, readOptions, k1))
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(testCf, readOptions, k1))
                }
            }
        }
    }

    @Test
    fun getPut() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    assertNull(txn.get(readOptions, k1))
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))
                }
            }
        }
    }

    @Test
    fun multiGetPut_cf() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    val cfList = listOf(testCf, testCf)

                    assertContentEquals(arrayOf<ByteArray?>(null, null), txn.multiGet(readOptions, cfList, keys))

                    txn.put(testCf, keys[0], values[0]!!)
                    txn.put(testCf, keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGet(readOptions, cfList, keys))
                }
            }
        }
    }

    @Test
    fun multiGetPut() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    assertContentEquals(arrayOf<ByteArray?>(null, null), txn.multiGet(readOptions, keys))

                    txn.put(keys[0], values[0]!!)
                    txn.put(keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGet(readOptions, keys))
                }
            }
        }
    }

    @Test
    fun getForUpdate_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    assertNull(txn.getForUpdate(readOptions, testCf, k1, true))
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.getForUpdate(readOptions, testCf, k1, true))
                }
            }
        }
    }

    @Test
    fun getForUpdate() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    assertNull(txn.getForUpdate(readOptions, k1, true))
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.getForUpdate(readOptions, k1, true))
                }
            }
        }
    }

    @Test
    fun multiGetForUpdate_cf() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    val cfList = listOf(testCf, testCf)

                    assertContentEquals(arrayOf<ByteArray?>(null, null), txn.multiGetForUpdate(readOptions, cfList, keys))

                    txn.put(testCf, keys[0], values[0]!!)
                    txn.put(testCf, keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGetForUpdate(readOptions, cfList, keys))
                }
            }
        }
    }

    @Test
    fun multiGetForUpdate() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    assertContentEquals(arrayOf<ByteArray?>(null, null), txn.multiGetForUpdate(readOptions, keys))

                    txn.put(keys[0], values[0]!!)
                    txn.put(keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGetForUpdate(readOptions, keys))
                }
            }
        }
    }

    @Test
    fun getIterator() {
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val k1 = "key1".encodeToByteArray()
                    val v1 = "value1".encodeToByteArray()

                    txn.put(k1, v1)

                    txn.getIterator(readOptions).use { iterator ->
                        iterator.seek(k1)
                        assertTrue(iterator.isValid())
                        assertContentEquals(k1, iterator.key())
                        assertContentEquals(v1, iterator.value())
                    }
                }
            }
        }
    }

    @Test
    fun getIterator_cf() {
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily

                    val k1 = "key1".encodeToByteArray()
                    val v1 = "value1".encodeToByteArray()

                    txn.put(testCf, k1, v1)

                    txn.getIterator(readOptions, testCf).use { iterator ->
                        iterator.seek(k1)
                        assertTrue(iterator.isValid())
                        assertContentEquals(k1, iterator.key())
                        assertContentEquals(v1, iterator.value())
                    }
                }
            }
        }
    }

    @Test
    fun merge_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                val testCf = dbContainer.testColumnFamily
                txn.merge(testCf, k1, v1)
            }
        }
    }

    @Test
    fun merge() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer -> dbContainer.beginTransaction().use { txn -> txn.merge(k1, v1) } }
    }


    @Test
    fun delete_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(testCf, readOptions, k1))

                    txn.delete(testCf, k1)
                    assertNull(txn.get(testCf, readOptions, k1))
                }
            }
        }
    }

    @Test
    fun delete() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))

                    txn.delete(k1)
                    assertNull(txn.get(readOptions, k1))
                }
            }
        }
    }

    @Test
    fun delete_parts_cf() {
        val keyParts = arrayOf("ke".encodeToByteArray(), "y1".encodeToByteArray())
        val valueParts = arrayOf("val".encodeToByteArray(), "ue1".encodeToByteArray())
        val key = concat(keyParts)
        val value = concat(valueParts)

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    txn.put(testCf, keyParts, valueParts)
                    assertContentEquals(value, txn.get(testCf, readOptions, key))

                    txn.delete(testCf, keyParts)

                    assertNull(txn.get(testCf, readOptions, key))
                }
            }
        }
    }

    @Test
    fun delete_parts() {
        val keyParts = arrayOf("ke".encodeToByteArray(), "y1".encodeToByteArray())
        val valueParts = arrayOf("val".encodeToByteArray(), "ue1".encodeToByteArray())
        val key = concat(keyParts)
        val value = concat(valueParts)

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(keyParts, valueParts)

                    assertContentEquals(value, txn.get(readOptions, key))

                    txn.delete(keyParts)

                    assertNull(txn.get(readOptions, key))
                }
            }
        }
    }

    @Test
    fun getPutUntracked_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    assertNull(txn.get(testCf, readOptions, k1))
                    txn.putUntracked(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(testCf, readOptions, k1))
                }
            }
        }
    }

    @Test
    fun getPutUntracked() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    assertNull(txn.get(readOptions, k1))
                    txn.putUntracked(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))
                }
            }
        }
    }

    @Test
    fun multiGetPutUntracked_cf() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily

                    val cfList = listOf(testCf, testCf)

                    assertContentEquals(arrayOf<ByteArray?>(null, null), txn.multiGet(readOptions, cfList, keys))
                    txn.putUntracked(testCf, keys[0], values[0]!!)
                    txn.putUntracked(testCf, keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGet(readOptions, cfList, keys))
                }
            }
        }
    }

    @Test
    fun multiGetPutUntracked() {
        val keys = arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray())
        val values = arrayOf<ByteArray?>("value1".encodeToByteArray(), "value2".encodeToByteArray())

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    assertContentEquals(arrayOf<ByteArray?>(null, null), txn.multiGet(readOptions, keys))
                    txn.putUntracked(keys[0], values[0]!!)
                    txn.putUntracked(keys[1], values[1]!!)
                    assertContentEquals(values, txn.multiGet(readOptions, keys))
                }
            }
        }
    }

    @Test
    fun mergeUntracked_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                val testCf = dbContainer.testColumnFamily
                txn.mergeUntracked(testCf, k1, v1)
            }
        }
    }

    @Test
    fun mergeUntracked() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.mergeUntracked(k1, v1)
            }
        }
    }

    @Test
    fun deleteUntracked_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.get(testCf, readOptions, k1))

                    txn.deleteUntracked(testCf, k1)
                    assertNull(txn.get(testCf, readOptions, k1))
                }
            }
        }
    }

    @Test
    fun deleteUntracked() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.get(readOptions, k1))

                    txn.deleteUntracked(k1)
                    assertNull(txn.get(readOptions, k1))
                }
            }
        }
    }

    @Test
    fun deleteUntracked_parts_cf() {
        val keyParts = arrayOf("ke".encodeToByteArray(), "y1".encodeToByteArray())
        val valueParts = arrayOf("val".encodeToByteArray(), "ue1".encodeToByteArray())
        val key = concat(keyParts)
        val value = concat(valueParts)

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    txn.put(testCf, keyParts, valueParts)
                    assertContentEquals(value, txn.get(testCf, readOptions, key))

                    txn.deleteUntracked(testCf, keyParts)
                    assertNull(txn.get(testCf, readOptions, key))
                }
            }
        }
    }

    @Test
    fun deleteUntracked_parts() {
        val keyParts = arrayOf("ke".encodeToByteArray(), "y1".encodeToByteArray())
        val valueParts = arrayOf("val".encodeToByteArray(), "ue1".encodeToByteArray())
        val key = concat(keyParts)
        val value = concat(valueParts)

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    txn.put(keyParts, valueParts)
                    assertContentEquals(value, txn.get(readOptions, key))

                    txn.deleteUntracked(keyParts)
                    assertNull(txn.get(readOptions, key))
                }
            }
        }
    }

    @Test
    fun putLogData() {
        val blob = "blobby".encodeToByteArray()
        startDb().use { dbContainer -> dbContainer.beginTransaction().use { txn -> txn.putLogData(blob) } }
    }

    @Test
    fun enabledDisableIndexing() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.disableIndexing()
                txn.enableIndexing()
                txn.disableIndexing()
                txn.enableIndexing()
            }
        }
    }

    @Test
    fun numKeys() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val k2 = "key2".encodeToByteArray()
        val v2 = "value2".encodeToByteArray()
        val k3 = "key3".encodeToByteArray()
        val v3 = "value3".encodeToByteArray()

        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                val testCf = dbContainer.testColumnFamily
                txn.put(k1, v1)
                txn.put(testCf, k2, v2)
                txn.merge(k3, v3)
                txn.delete(testCf, k2)

                assertEquals(3, txn.getNumKeys())
                assertEquals(2, txn.getNumPuts())
                assertEquals(1, txn.getNumMerges())
                assertEquals(1, txn.getNumDeletes())
            }
        }
    }

    @Test
    fun elapsedTime() {
        val preStartTxnTime = currentTimeMillis()
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                sleep(2000)

                val txnElapsedTime = txn.getElapsedTime()
                assertTrue(currentTimeMillis() - preStartTxnTime > txnElapsedTime)
                assertTrue(0 < txnElapsedTime)
            }
        }
    }

    @Test
    fun getWriteBatch() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.put(k1, v1)

                val writeBatch = txn.getWriteBatch()
                assertNotNull(writeBatch)
                assertFalse(writeBatch.isOwningHandle())
                assertEquals(1, writeBatch.count())
            }
        }
    }

    @Test
    fun setLockTimeout() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                txn.setLockTimeout(1000)
            }
        }
    }

    @Test
    fun writeOptions() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            WriteOptions()
                .setDisableWAL(true)
                .setSync(true).use { writeOptions ->
                    dbContainer.beginTransaction(writeOptions).use { txn ->

                        txn.put(k1, v1)

                        var txnWriteOptions = txn.getWriteOptions()
                        assertNotNull(txnWriteOptions)
                        assertFalse(txnWriteOptions.isOwningHandle())
                        assertNotSame(writeOptions, txnWriteOptions)
                        assertTrue(txnWriteOptions.disableWAL())
                        assertTrue(txnWriteOptions.sync())

                        txn.setWriteOptions(txnWriteOptions.setSync(false))
                        txnWriteOptions = txn.getWriteOptions()
                        assertNotNull(txnWriteOptions)
                        assertFalse(txnWriteOptions.isOwningHandle())
                        assertNotSame(writeOptions, txnWriteOptions)
                        assertTrue(txnWriteOptions.disableWAL())
                        assertFalse(txnWriteOptions.sync())
                    }
                }
        }
    }

    @Test
    fun undoGetForUpdate_cf() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    val testCf = dbContainer.testColumnFamily
                    assertNull(txn.getForUpdate(readOptions, testCf, k1, true))
                    txn.put(testCf, k1, v1)
                    assertContentEquals(v1, txn.getForUpdate(readOptions, testCf, k1, true))
                    txn.undoGetForUpdate(testCf, k1)
                }
            }
        }
    }

    @Test
    fun undoGetForUpdate() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->
                    assertNull(txn.getForUpdate(readOptions, k1, true))
                    txn.put(k1, v1)
                    assertContentEquals(v1, txn.getForUpdate(readOptions, k1, true))
                    txn.undoGetForUpdate(k1)
                }
            }
        }
    }

    @Test
    fun rebuildFromWriteBatch() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()
        val k2 = "key2".encodeToByteArray()
        val v2 = "value2".encodeToByteArray()
        val k3 = "key3".encodeToByteArray()
        val v3 = "value3".encodeToByteArray()

        startDb().use { dbContainer ->
            ReadOptions().use { readOptions ->
                dbContainer.beginTransaction().use { txn ->

                    txn.put(k1, v1)

                    assertContentEquals(v1, txn.get(readOptions, k1))
                    assertEquals(1, txn.getNumKeys())

                    WriteBatch().use { writeBatch ->
                        writeBatch.put(k2, v2)
                        writeBatch.put(k3, v3)
                        txn.rebuildFromWriteBatch(writeBatch)

                        assertContentEquals(v1, txn.get(readOptions, k1))
                        assertContentEquals(v2, txn.get(readOptions, k2))
                        assertContentEquals(v3, txn.get(readOptions, k3))
                        assertEquals(3, txn.getNumKeys())
                    }
                }
            }
        }
    }

    @Test
    fun getCommitTimeWriteBatch() {
        val k1 = "key1".encodeToByteArray()
        val v1 = "value1".encodeToByteArray()

        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->

                txn.put(k1, v1)
                val writeBatch = txn.getCommitTimeWriteBatch()

                assertNotNull(writeBatch)
                assertFalse(writeBatch.isOwningHandle())
                assertEquals(0, writeBatch.count())
            }
        }
    }

    @Test
    fun logNumber() {
        startDb().use { dbContainer ->
            dbContainer.beginTransaction().use { txn ->
                assertEquals(0, txn.getLogNumber())
                val logNumber = Random.nextLong()
                txn.setLogNumber(logNumber)
                assertEquals(logNumber, txn.getLogNumber())
            }
        }
    }

    private class TestTransactionNotifier : AbstractTransactionNotifier() {
        private val createdSnapshots = ArrayList<Snapshot>()

        override fun snapshotCreated(newSnapshot: Snapshot) {
            createdSnapshots.add(newSnapshot)
        }

        fun getCreatedSnapshots(): List<Snapshot> {
            return createdSnapshots
        }
    }

    protected abstract class DBContainer(
        protected val writeOptions: WriteOptions,
        protected val columnFamilyHandles: List<ColumnFamilyHandle>,
        protected val columnFamilyOptions: ColumnFamilyOptions,
        protected val options: DBOptions
    ) : AutoCloseable {

        val testColumnFamily: ColumnFamilyHandle
            get() {
                return columnFamilyHandles.get(1)
            }

        abstract fun beginTransaction(): Transaction

        abstract fun beginTransaction(
            writeOptions: WriteOptions
        ): Transaction

        abstract override fun close()
    }

    companion object {
        private fun concat(bufs: Array<ByteArray>): ByteArray {
            var resultLength = 0
            for (buf in bufs) {
                resultLength += buf.size
            }

            val result = ByteArray(resultLength)
            var resultOffset = 0
            for (buf in bufs) {
                val srcLength = buf.size
                buf.copyInto(result, resultOffset, 0, srcLength)
                resultOffset += srcLength
            }

            return result
        }
    }
}
