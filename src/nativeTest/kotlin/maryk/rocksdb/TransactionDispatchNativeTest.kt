package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TransactionDispatchNativeTest {
    private fun withDatabase(policy: TxnDBWritePolicy, block: (TransactionDB, List<ColumnFamilyHandle>) -> Unit) {
        DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true).setAllow2pc(true).use { options ->
            ColumnFamilyOptions().use { cfOptions ->
                val descriptors = listOf(
                    ColumnFamilyDescriptor(defaultColumnFamily, cfOptions),
                    ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions),
                )
                val handles = mutableListOf<ColumnFamilyHandle>()
                TransactionDBOptions().setWritePolicy(policy).setDefaultLockTimeout(0).use { txOptions ->
                    openTransactionDB(options, txOptions, createTestDBFolder("TransactionDispatchNativeTest"), descriptors, handles).use { db ->
                        try {
                            block(db, handles)
                        } finally {
                            handles.forEach { it.close() }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun deleteBatchesLockEveryFamilyAndFailAtomically() {
        TxnDBWritePolicy.entries.forEach { policy ->
            withDatabase(policy) { db, families ->
                WriteOptions().use { writeOptions ->
                    ReadOptions().use { readOptions ->
                        families.forEach { cf ->
                            val key = byteArrayOf(0, 127, -1)
                            val added = "added".encodeToByteArray()
                            val value = "value".encodeToByteArray()
                            db.put(cf, key, value)
                            WriteBatch().use { batch ->
                                batch.put(cf, added, value)
                                batch.delete(cf, key)
                                db.beginTransaction(writeOptions).use { tx ->
                                    tx.getForUpdate(readOptions, cf, key, true)
                                    val error = assertFailsWith<RocksDBException> { db.write(writeOptions, batch) }
                                    assertEquals(StatusCode.TimedOut, error.getStatus()?.getCode())
                                    assertEquals(StatusSubCode.LockTimeout, error.getStatus()?.getSubCode())
                                    assertContentEquals(value, db.get(cf, key))
                                    assertNull(db.get(cf, added))
                                    tx.rollback()
                                }
                                db.write(writeOptions, batch)
                                assertNull(db.get(cf, key))
                                assertContentEquals(value, db.get(cf, added))
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun preparedValuesStayHiddenFromReadsIteratorsAndSnapshots() {
        listOf(TxnDBWritePolicy.WRITE_PREPARED, TxnDBWritePolicy.WRITE_UNPREPARED).forEach { policy ->
            withDatabase(policy) { db, families ->
                val key = "key".encodeToByteArray()
                val before = "before".encodeToByteArray()
                val pending = "pending".encodeToByteArray()
                families.forEach { db.put(it, key, before) }
                WriteOptions().use { writeOptions ->
                    db.beginTransaction(writeOptions).use { tx ->
                        tx.setName("prepared")
                        families.forEach { tx.put(it, key, pending) }
                        tx.prepare()
                        families.forEach { cf ->
                            assertContentEquals(before, db.get(cf, key))
                            val buffer = ByteArray(before.size)
                            assertEquals(before.size, db.get(cf, key, buffer))
                            assertContentEquals(before, buffer)
                            db.newIterator(cf).use { iterator ->
                                iterator.seek(key)
                                assertContentEquals(before, iterator.value())
                            }
                        }
                        db.multiGetAsList(families, families.map { key }).forEach { assertContentEquals(before, it) }
                        val iterators = db.newIterators(families)
                        try {
                            iterators.forEach { iterator ->
                                iterator.seek(key)
                                assertContentEquals(before, iterator.value())
                            }
                        } finally {
                            iterators.forEach { it.close() }
                        }
                        db.getSnapshot()!!.use { snapshot ->
                            ReadOptions().setSnapshot(snapshot).use { readOptions ->
                                tx.commit()
                                families.forEach { cf ->
                                    assertContentEquals(pending, db.get(cf, key))
                                    assertContentEquals(before, db.get(cf, readOptions, key))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
