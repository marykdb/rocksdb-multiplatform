package maryk.rocksdb

import maryk.assertContentEquals
import maryk.decodeToString
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TransactionDBTest {
    private fun createTestFolder() = createTestDBFolder("TransactionDBTest")

    @Test
    fun open() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options, txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    assertNotNull(tdb)
                }
            }
        }
    }

    @Test
    fun open_columnFamilies() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { dbOptions ->
                ColumnFamilyOptions().use { myCfOpts ->
                    val columnFamilyDescriptors = listOf(
                        ColumnFamilyDescriptor(defaultColumnFamily),
                        ColumnFamilyDescriptor("myCf".encodeToByteArray(), myCfOpts)
                    )

                    val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()

                    TransactionDBOptions().use { txnDbOptions ->
                        openTransactionDB(
                            dbOptions,
                            txnDbOptions,
                            createTestFolder(),
                            columnFamilyDescriptors,
                            columnFamilyHandles
                        ).use { tdb ->
                            try {
                                assertNotNull(tdb)
                            } finally {
                                for (handle in columnFamilyHandles) {
                                    handle.close()
                                }
                            }
                        }
                    }
                }
            }
    }

    @Test
    fun beginTransaction() {
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options,
                    txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    WriteOptions().use { writeOptions ->
                        tdb.beginTransaction(writeOptions).use { txn ->
                            assertNotNull(txn)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_transactionOptions() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options, txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    WriteOptions().use { writeOptions ->
                        TransactionOptions().use { txnOptions ->
                            tdb.beginTransaction(
                                writeOptions,
                                txnOptions
                            ).use { txn ->
                                assertNotNull(txn)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_withOld() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options, txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    WriteOptions().use { writeOptions ->
                        tdb.beginTransaction(writeOptions).use { txn ->
                            val txnReused = tdb.beginTransaction(writeOptions, txn)
                            assertSame(txn, txnReused)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_withOld_transactionOptions() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options, txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    WriteOptions().use { writeOptions ->
                        TransactionOptions().use { txnOptions ->
                            tdb.beginTransaction(writeOptions).use { txn ->
                                val txnReused = tdb.beginTransaction(
                                    writeOptions,
                                    txnOptions, txn
                                )
                                assertSame(txn, txnReused)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun lockStatusData() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options,
                    txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    WriteOptions().use { writeOptions ->
                        ReadOptions().use { readOptions ->
                            tdb.beginTransaction(writeOptions).use { txn ->
                                val key = "key".encodeToByteArray()
                                val value = "value".encodeToByteArray()

                                txn.put(key, value)
                                assertContentEquals(value, txn.getForUpdate(readOptions, key, true))

                                val lockStatus = tdb.getLockStatusData()

                                assertEquals(1, lockStatus.size)
                                val entrySet = lockStatus.entries
                                val entry = entrySet.iterator().next()
                                val columnFamilyId = entry.key
                                assertEquals(0, columnFamilyId)
                                val keyLockInfo = entry.value
                                assertEquals(key.decodeToString(), keyLockInfo.getKey())
                                assertEquals(1, keyLockInfo.getTransactionIDs().size)
                                assertEquals(txn.getId(), keyLockInfo.getTransactionIDs()[0])
                                assertTrue(keyLockInfo.isExclusive())
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun deadlockInfoBuffer() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options, txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    // TODO(AR) can we cause a deadlock so that we can test the output here?
                    assertTrue(tdb.getDeadlockInfoBuffer().isEmpty())
                }
            }
        }
    }

    @Test
    fun setDeadlockInfoBufferSize() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txnDbOptions ->
                openTransactionDB(
                    options, txnDbOptions,
                    createTestFolder()
                ).use { tdb ->
                    tdb.setDeadlockInfoBufferSize(123)
                }
            }
        }
    }
}

