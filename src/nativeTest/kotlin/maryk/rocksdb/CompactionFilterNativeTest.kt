@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.UByteVarOf
import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class CompactionFilterNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun compactionFilterCallbackExceptionDoesNotEscapeManualCompaction() {
        val dbPath = createTestDBFolder("CompactionFilterNativeTest_filter_exception")
        val callbackCount = ThreadSafeCounter()

        ColumnFamilyOptions()
            .setCompactionFilter(ThrowingCompactionFilter(callbackCount))
            .use { columnFamilyOptions ->
                withDefaultColumnFamily(dbPath, columnFamilyOptions) { db, defaultHandle ->
                    writeCompactionInput(db, defaultHandle, "filter")

                    db.compactRange(defaultHandle)

                    assertContentEquals(
                        "filter-value-0".encodeToByteArray(),
                        db.get(defaultHandle, "filter-key-0".encodeToByteArray())
                    )
                }
            }

        assertTrue(callbackCount.value() > 0, "Expected RocksDB to invoke the compaction filter callback")
    }

    @Test
    fun compactionFilterFactoryCallbackExceptionDoesNotEscapeManualCompaction() {
        val dbPath = createTestDBFolder("CompactionFilterNativeTest_factory_exception")
        val factoryCount = ThreadSafeCounter()
        val filterCount = ThreadSafeCounter()

        ColumnFamilyOptions()
            .setCompactionFilterFactory(ThrowingCompactionFilterFactory(factoryCount, filterCount))
            .use { columnFamilyOptions ->
                withDefaultColumnFamily(dbPath, columnFamilyOptions) { db, defaultHandle ->
                    writeCompactionInput(db, defaultHandle, "factory")

                    db.compactRange(defaultHandle)

                    assertContentEquals(
                        "factory-value-0".encodeToByteArray(),
                        db.get(defaultHandle, "factory-key-0".encodeToByteArray())
                    )
                }
            }

        assertTrue(factoryCount.value() > 0, "Expected RocksDB to invoke the compaction filter factory callback")
        assertTrue(filterCount.value() > 0, "Expected RocksDB to invoke the factory-created filter callback")
    }

    private fun withDefaultColumnFamily(
        dbPath: String,
        columnFamilyOptions: ColumnFamilyOptions,
        block: (RocksDB, ColumnFamilyHandle) -> Unit
    ) {
        DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true)
            .use { dbOptions ->
                val handles = mutableListOf<ColumnFamilyHandle>()
                val descriptors = listOf(ColumnFamilyDescriptor(defaultColumnFamily, columnFamilyOptions))
                openRocksDB(dbOptions, dbPath, descriptors, handles).use { db ->
                    try {
                        block(db, handles.single())
                    } finally {
                        handles.forEach { it.close() }
                    }
                }
            }
    }

    private fun writeCompactionInput(db: RocksDB, defaultHandle: ColumnFamilyHandle, prefix: String) {
        repeat(32) { index ->
            db.put(
                defaultHandle,
                "$prefix-key-$index".encodeToByteArray(),
                "$prefix-value-$index".encodeToByteArray()
            )
        }
        FlushOptions().setWaitForFlush(true).use { flushOptions ->
            db.flush(flushOptions, defaultHandle)
        }
    }
}

private class ThrowingCompactionFilter(
    private val callbackCount: ThreadSafeCounter
) : AbstractCompactionFilter<Slice>() {
    override fun filter(
        level: Int,
        key: CPointer<ByteVar>?,
        keyLength: size_t,
        existingValue: CPointer<ByteVar>?,
        valueLength: size_t,
        newValue: CPointer<CPointerVarOf<CPointer<ByteVar>>>?,
        newValueLength: CPointer<size_tVar>?,
        valueChanged: CPointer<UByteVarOf<UByte>>?
    ): Boolean {
        callbackCount.increment()
        throw IllegalStateException("compaction filter failure")
    }
}

private class ThrowingCompactionFilterFactory(
    private val factoryCount: ThreadSafeCounter,
    private val filterCount: ThreadSafeCounter
) : AbstractCompactionFilterFactory<ThrowingCompactionFilter>() {
    override fun name(): String = "ThrowingCompactionFilterFactory"

    override fun createCompactionFilter(context: AbstractCompactionFilterContext): ThrowingCompactionFilter {
        assertTrue(context.isManualCompaction(), "Manual compactRange should create a manual compaction context")
        factoryCount.increment()
        return ThrowingCompactionFilter(filterCount)
    }
}
