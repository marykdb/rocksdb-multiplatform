package maryk.rocksdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.UByteVarOf
import maryk.rocksdb.util.createTestDBFolder
import maryk.rocksdb.util.ThreadSafeCounter
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TtlDBNativeTest {
    @Test
    fun ttlColumnFamilyOpenRetainsCompactionFilterAfterOptionsClose() {
        val dbPath = createTestDBFolder("TtlDBNativeTest_column_family_filter")
        val callbackCount = ThreadSafeCounter()
        val columnFamilyOptions = ColumnFamilyOptions()
            .setCompactionFilter(CountingCompactionFilter(callbackCount))
        val handles = mutableListOf<ColumnFamilyHandle>()

        DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
            .use { dbOptions ->
                val descriptors = listOf(ColumnFamilyDescriptor(defaultColumnFamily, columnFamilyOptions))
                openTtlDB(dbOptions, dbPath, descriptors, handles, listOf(0), false).use { db ->
                    try {
                        columnFamilyOptions.close()
                        val defaultHandle = handles.single()
                        repeat(32) { index ->
                            db.put(
                                defaultHandle,
                                "key-$index".encodeToByteArray(),
                                "value-$index".encodeToByteArray(),
                            )
                        }
                        FlushOptions().setWaitForFlush(true).use { flushOptions ->
                            db.flush(flushOptions, defaultHandle)
                        }
                        db.compactRange(defaultHandle)

                        assertTrue(callbackCount.value() > 0, "Expected RocksDB to invoke the retained compaction filter")
                    } finally {
                        handles.forEach { it.close() }
                    }
                }
            }
    }

    @Test
    fun ttlDbCloseInvalidatesInheritedDbChildren() {
        val dbPath = createTestDBFolder("TtlDBNativeTest_inherited_children")

        Options().setCreateIfMissing(true).use { options ->
            openTtlDB(options, dbPath).use { db ->
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

private class CountingCompactionFilter(
    private val callbackCount: ThreadSafeCounter,
) : AbstractCompactionFilter<Slice>() {
    override fun filter(
        level: Int,
        key: CPointer<ByteVar>?,
        keyLength: size_t,
        existingValue: CPointer<ByteVar>?,
        valueLength: size_t,
        newValue: CPointer<CPointerVarOf<CPointer<ByteVar>>>?,
        newValueLength: CPointer<size_tVar>?,
        valueChanged: CPointer<UByteVarOf<UByte>>?,
    ): Boolean {
        callbackCount.increment()
        return false
    }
}
