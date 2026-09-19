@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MergeOperatorNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun mergeOperatorCallbackExceptionDoesNotEscapeRead() {
        val dbPath = createTestDBFolder("MergeOperatorNativeTest_callback_exception")
        val callbackCount = ThreadSafeCounter()

        DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
            .use { options ->
                ColumnFamilyOptions()
                    .setMergeOperator(ThrowingMergeOperator(callbackCount))
                    .use { columnFamilyOptions ->
                        val handles = mutableListOf<ColumnFamilyHandle>()
                        val descriptors = listOf(
                            ColumnFamilyDescriptor(defaultColumnFamily, columnFamilyOptions)
                        )
                        openRocksDB(options, dbPath, descriptors, handles).use { db ->
                            try {
                                val defaultHandle = handles.single()
                                db.merge(defaultHandle, "merge-key".encodeToByteArray(), "value".encodeToByteArray())

                                assertFailsWith<RocksDBException> {
                                    db.get(defaultHandle, "merge-key".encodeToByteArray())
                                }
                            } finally {
                                handles.forEach { it.close() }
                            }
                        }
                    }
            }

        assertTrue(callbackCount.value() > 0, "Expected RocksDB to invoke the merge operator callback")
    }
}

private class ThrowingMergeOperator(
    private val callbackCount: ThreadSafeCounter
) : MergeOperator() {
    override fun fullMerge(
        key: CPointer<ByteVarOf<Byte>>?,
        keyLen: size_t,
        existingValue: CPointer<ByteVarOf<Byte>>?,
        existingValueLen: size_t,
        operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
        operandsLengths: CPointer<size_tVar>?,
        numOperands: Int
    ): Pair<Boolean, Pair<CPointer<ByteVarOf<Byte>>?, size_t>> {
        callbackCount.increment()
        throw IllegalStateException("merge operator failure")
    }
}
