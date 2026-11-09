package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TransactionWriteIsolationTest {
    private fun withDatabase(optimistic: Boolean = false, block: (RocksDB, ColumnFamilyHandle) -> Unit) {
        DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true).use { options ->
            ColumnFamilyOptions().use { cfOptions ->
                val descriptors = listOf(
                    ColumnFamilyDescriptor(defaultColumnFamily, cfOptions),
                    ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions),
                )
                val handles = mutableListOf<ColumnFamilyHandle>()
                val path = createTestDBFolder("TransactionWriteIsolationTest")
                TransactionDBOptions().setDefaultLockTimeout(0).setTransactionLockTimeout(10_000).use { txOptions ->
                    val db = if (optimistic) openOptimisticTransactionDB(options, path, descriptors, handles)
                    else openTransactionDB(options, txOptions, path, descriptors, handles)
                    db.use {
                        try {
                            block(db, handles[1])
                        } finally {
                            handles.forEach { it.close() }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun singleDeleteOverloadsRespectLocksAndDeleteAfterRollback() = withDatabase { db, cf ->
        val transactionDb = db as TransactionDB
        WriteOptions().use { writeOptions ->
            ReadOptions().use { readOptions ->
                val operations: List<Pair<ColumnFamilyHandle?, (ByteArray) -> Unit>> = listOf(
                    null to { key -> db.singleDelete(key) },
                    null to { key -> db.singleDelete(writeOptions, key) },
                    cf to { key -> db.singleDelete(cf, key) },
                    cf to { key -> db.singleDelete(cf, writeOptions, key) },
                )
                operations.forEachIndexed { index, (family, delete) ->
                    val key = byteArrayOf(index.toByte(), 0, 127, -1)
                    val value = "value".encodeToByteArray()
                    if (family == null) db.put(key, value) else db.put(family, key, value)
                    transactionDb.beginTransaction(writeOptions).use { transaction ->
                        if (family == null) transaction.getForUpdate(readOptions, key, true)
                        else transaction.getForUpdate(readOptions, family, key, true)
                        val error = assertFailsWith<RocksDBException> { delete(key) }
                        assertEquals(StatusCode.TimedOut, error.getStatus()?.getCode())
                        assertEquals(StatusSubCode.LockTimeout, error.getStatus()?.getSubCode())
                        assertContentEquals(value, if (family == null) db.get(key) else db.get(family, key))
                        transaction.rollback()
                    }
                    delete(key)
                    assertNull(if (family == null) db.get(key) else db.get(family, key))
                }
            }
        }
    }

    @Test
    fun pessimisticRangeDeleteOverloadsAreRejected() = assertRangeDeletesRejected(false)

    @Test
    fun optimisticRangeDeleteOverloadsAreRejected() = assertRangeDeletesRejected(true)

    private fun assertRangeDeletesRejected(optimistic: Boolean) = withDatabase(optimistic) { db, cf ->
        val key = "key".encodeToByteArray()
        val value = "value".encodeToByteArray()
        val begin = "a".encodeToByteArray()
        val end = "z".encodeToByteArray()
        db.put(key, value)
        db.put(cf, key, value)
        WriteOptions().use { options ->
            val operations = listOf<() -> Unit>(
                { db.deleteRange(begin, end) },
                { db.deleteRange(options, begin, end) },
                { db.deleteRange(cf, begin, end) },
                { db.deleteRange(cf, options, begin, end) },
            )
            operations.forEach { operation ->
                val error = assertFailsWith<RocksDBException> { operation() }
                assertEquals(StatusCode.NotSupported, error.getStatus()?.getCode())
                assertContentEquals(value, db.get(key))
                assertContentEquals(value, db.get(cf, key))
            }
        }
    }

    @Test
    fun optimisticRangeDeleteBatchesAreRejectedAtomically() = withDatabase(true) { db, cf ->
        val key = "key".encodeToByteArray()
        val value = "value".encodeToByteArray()
        val added = "new".encodeToByteArray()
        db.put(key, value)
        db.put(cf, key, value)
        WriteOptions().use { options ->
            listOf<ColumnFamilyHandle?>(null, cf).forEach { family ->
                WriteBatch().use { batch ->
                    batch.put(added, value)
                    if (family == null) batch.deleteRange("a".encodeToByteArray(), "z".encodeToByteArray())
                    else batch.deleteRange(family, "a".encodeToByteArray(), "z".encodeToByteArray())
                    val error = assertFailsWith<RocksDBException> { db.write(options, batch) }
                    assertEquals(StatusCode.NotSupported, error.getStatus()?.getCode())
                    assertNull(db.get(added))
                    assertContentEquals(value, db.get(key))
                    assertContentEquals(value, db.get(cf, key))
                }
            }
        }
    }
}
