package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.*

class ScopedOperationsTest {
    init { loadRocksDBLibrary() }

    @Test
    fun snapshotIsConsistentAndReleasesItsOptionsAndNativeSnapshot() {
        openRocksDB(createTestDBFolder("scoped-snapshot")).use { db ->
            val key = byteArrayOf(0, 1)
            db.put(key, byteArrayOf(1))
            lateinit var borrowed: ReadOptions
            val result = db.withSnapshot { read ->
                borrowed = read
                assertEquals(1L, db.getLongProperty("rocksdb.num-snapshots"))
                db.put(key, byteArrayOf(2))
                assertContentEquals(byteArrayOf(1), db.get(read, key))
                "complete"
            }
            assertEquals("complete", result)
            assertFalse(borrowed.isOwningHandle())
            assertEquals(0L, db.getLongProperty("rocksdb.num-snapshots"))
            assertContentEquals(byteArrayOf(2), db.get(key))
        }
    }

    @Test
    fun nestedSnapshotsCleanUpWhenCallbackFails() {
        openRocksDB(createTestDBFolder("scoped-snapshot-failure")).use { db ->
            val failure = IllegalStateException("snapshot callback failed")
            assertSame(failure, assertFailsWith<IllegalStateException> {
                db.withSnapshot {
                    db.withSnapshot {
                        assertEquals(2L, db.getLongProperty("rocksdb.num-snapshots"))
                        throw failure
                    }
                }
            })
            assertEquals(0L, db.getLongProperty("rocksdb.num-snapshots"))
        }
    }

    @Test
    fun pessimisticCommitAndRollbackCloseOnlyOwnedResources() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { dbOptions ->
                openTransactionDB(options, dbOptions, createTestDBFolder("scoped-pessimistic")).use { db ->
                    WriteOptions().use { writes ->
                        TransactionOptions().use { transactions ->
                            lateinit var observed: Transaction
                            assertEquals(42, db.withTransaction(writes, transactions) { txn ->
                                observed = txn
                                txn.put(byteArrayOf(1), byteArrayOf(2))
                                42
                            })
                            assertFalse(observed.isOwningHandle())
                            assertTrue(writes.isOwningHandle())
                            assertTrue(transactions.isOwningHandle())
                            assertContentEquals(byteArrayOf(2), db.get(byteArrayOf(1)))
                        }
                    }
                    val failure = IllegalStateException("rollback")
                    lateinit var rolledBack: Transaction
                    assertSame(failure, assertFailsWith<IllegalStateException> {
                        db.withTransaction { txn ->
                            rolledBack = txn
                            txn.put(byteArrayOf(3), byteArrayOf(4))
                            throw failure
                        }
                    })
                    assertFalse(rolledBack.isOwningHandle())
                    assertNull(db.get(byteArrayOf(3)))
                }
            }
        }
    }

    @Test
    fun optimisticCommitAndRollbackCloseOnlyOwnedResources() {
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, createTestDBFolder("scoped-optimistic")).use { db ->
                WriteOptions().use { writes ->
                    OptimisticTransactionOptions().use { transactions ->
                        lateinit var observed: Transaction
                        assertEquals(7, db.withTransaction(writes, transactions) { txn ->
                            observed = txn
                            txn.put(byteArrayOf(1), byteArrayOf(2))
                            7
                        })
                        assertFalse(observed.isOwningHandle())
                        assertTrue(writes.isOwningHandle())
                        assertTrue(transactions.isOwningHandle())
                    }
                }
                assertContentEquals(byteArrayOf(2), db.get(byteArrayOf(1)))
                val failure = IllegalStateException("rollback")
                assertSame(failure, assertFailsWith<IllegalStateException> {
                    db.withTransaction { txn ->
                        txn.put(byteArrayOf(3), byteArrayOf(4))
                        throw failure
                    }
                })
                assertNull(db.get(byteArrayOf(3)))
            }
        }
    }

    @Test
    fun optimisticConflictPropagatesWithoutRetryingCallback() {
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, createTestDBFolder("scoped-conflict")).use { db ->
                val key = byteArrayOf(1)
                db.put(key, byteArrayOf(0))
                var attempts = 0
                lateinit var observed: Transaction
                ReadOptions().use { read ->
                    assertFailsWith<RocksDBException> {
                        db.withTransaction { txn ->
                            attempts++
                            observed = txn
                            txn.setSnapshot()
                            assertContentEquals(byteArrayOf(0), txn.getForUpdate(read, key, true))
                            db.withTransaction { other -> other.put(key, byteArrayOf(9)) }
                            txn.put(key, byteArrayOf(2))
                        }
                    }
                }
                assertEquals(1, attempts)
                assertFalse(observed.isOwningHandle())
                assertContentEquals(byteArrayOf(9), db.get(key))
            }
        }
    }
}
