package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class OptimisticTransactionDBTest {
    private fun createTestFolder() = createTestDBFolder("OptimisticTransactionDBTest")

    @Test
    fun open() {
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(
                options,
                createTestFolder()
            ).use { otdb ->
                assertNotNull(otdb)
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

                    openOptimisticTransactionDB(
                        dbOptions,
                        createTestFolder(),
                        columnFamilyDescriptors, columnFamilyHandles
                    ).use { otdb ->
                        try {
                            assertNotNull(otdb)
                        } finally {
                            for (handle in columnFamilyHandles) {
                                handle.close()
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
            openOptimisticTransactionDB(
                options,
                createTestFolder()
            ).use { otdb ->
                WriteOptions().use { writeOptions ->
                    otdb.beginTransaction(writeOptions).use { txn ->
                        assertNotNull(txn)
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_transactionOptions() {
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openOptimisticTransactionDB(
                options,
                createTestFolder()
            ).use { otdb ->
                WriteOptions().use { writeOptions ->
                    OptimisticTransactionOptions().use { optimisticTxnOptions ->
                        otdb.beginTransaction(
                            writeOptions,
                            optimisticTxnOptions
                        ).use { txn ->
                            assertNotNull(txn)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_withOld() {
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(
                options,
                createTestFolder()
            ).use { otdb ->
                WriteOptions().use { writeOptions ->
                    otdb.beginTransaction(writeOptions).use { txn ->
                        val txnReused = otdb.beginTransaction(writeOptions, txn)
                        assertSame(txn, txnReused)
                    }
                }
            }
        }
    }

    @Test
    fun beginTransaction_withOld_transactionOptions() {
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openOptimisticTransactionDB(
                options,
                createTestFolder()
            ).use { otdb ->
                WriteOptions().use { writeOptions ->
                    OptimisticTransactionOptions().use { optimisticTxnOptions ->
                        otdb.beginTransaction(writeOptions).use { txn ->
                            val txnReused = otdb.beginTransaction(
                                writeOptions,
                                optimisticTxnOptions, txn
                            )
                            assertSame(txn, txnReused)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun baseDB() {
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openOptimisticTransactionDB(
                options,
                createTestFolder()
            ).use { otdb ->
                assertNotNull(otdb)
                val db = otdb.getBaseDB()
                assertNotNull(db)
                assertFalse(db.isOwningHandle())
            }
        }
    }
}
