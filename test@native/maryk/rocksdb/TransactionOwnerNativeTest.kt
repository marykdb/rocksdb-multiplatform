package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class TransactionOwnerNativeTest {
    @Test
    fun transactionWriteBatchBorrowedWrapperCanBeRepeatedlyClosed() {
        val dbPath = createTestDBFolder("TransactionOwnerNativeTest_repeated_write_batch")

        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                openTransactionDB(options, transactionDbOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { transaction ->
                            repeat(32) {
                                transaction.getWriteBatch().use { writeBatch ->
                                    assertFalse(writeBatch.isOwningHandle())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionWriteBatchBorrowedWrapperRejectsNestedBatchAccess() {
        val dbPath = createTestDBFolder("TransactionOwnerNativeTest_write_batch")

        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                openTransactionDB(options, transactionDbOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { transaction ->
                            transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())

                            val writeBatch = transaction.getWriteBatch()
                            assertNotNull(writeBatch)
                            assertFalse(writeBatch.isOwningHandle())
                            assertEquals(1, writeBatch.count())
                            assertFailsWith<IllegalStateException> {
                                writeBatch.getWriteBatch()
                            }
                            writeBatch.close()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionDbCloseInvalidatesOpenTransaction() {
        val dbPath = createTestDBFolder("TransactionOwnerNativeTest_transaction")

        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                openTransactionDB(options, transactionDbOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        val transaction = db.beginTransaction(writeOptions)
                        db.close()

                        assertFailsWith<IllegalStateException> {
                            transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                        }
                        transaction.close()
                    }
                }
            }
        }
    }

    @Test
    fun optimisticTransactionDbCloseInvalidatesOpenTransaction() {
        val dbPath = createTestDBFolder("TransactionOwnerNativeTest_optimistic")

        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, dbPath).use { db ->
                WriteOptions().use { writeOptions ->
                    val transaction = db.beginTransaction(writeOptions)
                    db.close()

                    assertFailsWith<IllegalStateException> {
                        transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    }
                    transaction.close()
                }
            }
        }
    }

    @Test
    fun transactionDbCloseInvalidatesInheritedDbChildren() {
        val dbPath = createTestDBFolder("TransactionOwnerNativeTest_inherited_children")

        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                openTransactionDB(options, transactionDbOptions, dbPath).use { db ->
                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    val snapshot = requireNotNull(db.getSnapshot())
                    val logIterator = db.getUpdatesSince(0)

                    db.close()

                    assertFailsWith<IllegalStateException> {
                        snapshot.getSequenceNumber()
                    }
                    assertFailsWith<IllegalStateException> {
                        logIterator.isValid()
                    }
                    snapshot.close()
                    logIterator.close()
                }
            }
        }
    }

    @Test
    fun transactionDbCloseInvalidatesReusedTransaction() {
        val dbPath = createTestDBFolder("TransactionOwnerNativeTest_reused_transaction")

        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                openTransactionDB(options, transactionDbOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        val transaction = db.beginTransaction(writeOptions)
                        db.beginTransaction(writeOptions, transaction)

                        db.close()

                        assertFailsWith<IllegalStateException> {
                            transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                        }
                        transaction.close()
                    }
                }
            }
        }
    }

    @Test
    fun optimisticTransactionDbCloseInvalidatesReusedTransaction() {
        val dbPath = createTestDBFolder("TransactionOwnerNativeTest_reused_optimistic")

        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, dbPath).use { db ->
                WriteOptions().use { writeOptions ->
                    val transaction = db.beginTransaction(writeOptions)
                    db.beginTransaction(writeOptions, transaction)

                    db.close()

                    assertFailsWith<IllegalStateException> {
                        transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    }
                    transaction.close()
                }
            }
        }
    }
}
