package maryk.rocksdb

import maryk.assertContentEquals
import maryk.createFile
import maryk.encodeToByteArray
import maryk.rocksdb.SstFileWriterTest.OpType.DELETE
import maryk.rocksdb.SstFileWriterTest.OpType.DELETE_BYTES
import maryk.rocksdb.SstFileWriterTest.OpType.MERGE
import maryk.rocksdb.SstFileWriterTest.OpType.MERGE_BYTES
import maryk.rocksdb.SstFileWriterTest.OpType.PUT
import maryk.rocksdb.SstFileWriterTest.OpType.PUT_BYTES
import maryk.rocksdb.util.BytewiseComparator
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val SST_FILE_NAME = "test.sst"

class SstFileWriterTest {
    internal enum class OpType {
        PUT, PUT_BYTES, MERGE, MERGE_BYTES, DELETE, DELETE_BYTES

    }

    internal inner class KeyValueWithOp(val key: String, val value: String, val opType: OpType)

    private fun createFolder() = createTestDBFolder("SstFileWriterTest")

    private fun createFile() = createFile(createFolder(), SST_FILE_NAME)

    private fun newSstFile(
        keyValues: List<KeyValueWithOp>,
        useJavaBytewiseComparator: Boolean
    ): String {
        val envOptions = EnvOptions()
        val stringAppendOperator = StringAppendOperator()
        val options = Options().setMergeOperator(stringAppendOperator)
        var comparatorOptions: ComparatorOptions? = null
        var comparator: BytewiseComparator? = null

        val sstFileWriter = if (useJavaBytewiseComparator) {
            comparatorOptions = ComparatorOptions()
            comparator = BytewiseComparator(comparatorOptions)
            options.setComparator(comparator)
            SstFileWriter(envOptions, options)
        } else {
            SstFileWriter(envOptions, options)
        }

        val sstFile = createFile()
        try {
            sstFileWriter.open(sstFile)
            for (keyValue in keyValues) {
                val keySlice = Slice(keyValue.key)
                val valueSlice = Slice(keyValue.value)
                val keyBytes = keyValue.key.encodeToByteArray()
                val valueBytes = keyValue.value.encodeToByteArray()
                when (keyValue.opType) {
                    PUT -> sstFileWriter.put(keySlice, valueSlice)
                    PUT_BYTES -> sstFileWriter.put(keyBytes, valueBytes)
                    MERGE -> sstFileWriter.merge(keySlice, valueSlice)
                    MERGE_BYTES -> sstFileWriter.merge(keyBytes, valueBytes)
                    DELETE -> sstFileWriter.delete(keySlice)
                    DELETE_BYTES -> sstFileWriter.delete(keyBytes)
                }
                keySlice.close()
                valueSlice.close()
            }
            sstFileWriter.finish()
        } finally {
            assertNotNull(sstFileWriter)
            sstFileWriter.close()
            options.close()
            envOptions.close()
            comparatorOptions?.close()
            comparator?.close()
        }
        return sstFile
    }

    @Test
    fun generateSstFileWithJavaComparator() {
        val keyValues = listOf(
            KeyValueWithOp("key1", "value1", PUT),
            KeyValueWithOp("key2", "value2", PUT),
            KeyValueWithOp("key3", "value3", MERGE),
            KeyValueWithOp("key4", "value4", MERGE),
            KeyValueWithOp("key5", "", DELETE)
        )

        newSstFile(keyValues, true)
    }

    @Test
    fun generateSstFileWithNativeComparator() {
        val keyValues = listOf(
            KeyValueWithOp("key1", "value1", PUT),
            KeyValueWithOp("key2", "value2", PUT),
            KeyValueWithOp("key3", "value3", MERGE),
            KeyValueWithOp("key4", "value4", MERGE),
            KeyValueWithOp("key5", "", DELETE)
        )

        newSstFile(keyValues, false)
    }

    @Test
    fun ingestSstFile() {
        val keyValues = listOf(
            KeyValueWithOp("key1", "value1", PUT),
            KeyValueWithOp("key2", "value2", PUT),
            KeyValueWithOp("key3", "value3", PUT_BYTES),
            KeyValueWithOp("key4", "value4", MERGE),
            KeyValueWithOp("key5", "value5", MERGE_BYTES),
            KeyValueWithOp("key6", "", DELETE),
            KeyValueWithOp("key7", "", DELETE)
        )

        val sstFile = newSstFile(keyValues, false)
        val dbFolder = createFolder()
        StringAppendOperator().use { stringAppendOperator ->
            Options().apply {
                setCreateIfMissing(true)
                setMergeOperator(stringAppendOperator)
            }.use { options ->
                openRocksDB(options, dbFolder).use { db ->
                    IngestExternalFileOptions().use { ingestExternalFileOptions ->
                        db.ingestExternalFile(
                            listOf(sstFile),
                            ingestExternalFileOptions
                        )

                        assertContentEquals("value1".encodeToByteArray(), db["key1".encodeToByteArray()])
                        assertContentEquals("value2".encodeToByteArray(), db["key2".encodeToByteArray()])
                        assertContentEquals("value3".encodeToByteArray(), db["key3".encodeToByteArray()])
                        assertContentEquals("value4".encodeToByteArray(), db["key4".encodeToByteArray()])
                        assertContentEquals("value5".encodeToByteArray(), db["key5".encodeToByteArray()])
                        assertNull(db["key6".encodeToByteArray()])
                        assertNull(db["key7".encodeToByteArray()])
                    }
                }
            }
        }
    }

    @Test
    fun ingestSstFile_cf() {
        val keyValues = listOf(
            KeyValueWithOp("key1", "value1", PUT),
            KeyValueWithOp("key2", "value2", PUT),
            KeyValueWithOp("key3", "value3", MERGE),
            KeyValueWithOp("key4", "", DELETE)
        )

        val sstFile = newSstFile(keyValues, false)
        val dbFolder = createFolder()
        StringAppendOperator().use { stringAppendOperator ->
            Options().apply {
                setCreateIfMissing(true)
                setCreateMissingColumnFamilies(true)
                setMergeOperator(stringAppendOperator)
            }.use { options ->
                openRocksDB(options, dbFolder).use { db ->
                    IngestExternalFileOptions().use { ingestExternalFileOptions ->

                        ColumnFamilyOptions().apply {
                            setMergeOperator(stringAppendOperator)
                        }.use { cf_opts ->
                            db.createColumnFamily(
                                ColumnFamilyDescriptor("new_cf".encodeToByteArray(), cf_opts)
                            ).use { cf_handle ->

                                db.ingestExternalFile(
                                    cf_handle,
                                    listOf(sstFile),
                                    ingestExternalFileOptions
                                )

                                assertContentEquals(
                                    "value1".encodeToByteArray(),
                                    db.get(cf_handle, "key1".encodeToByteArray())
                                )
                                assertContentEquals(
                                    "value2".encodeToByteArray(),
                                    db.get(cf_handle, "key2".encodeToByteArray())
                                )
                                assertContentEquals(
                                    "value3".encodeToByteArray(),
                                    db.get(cf_handle, "key3".encodeToByteArray())
                                )
                                assertNull(db.get(cf_handle, "key4".encodeToByteArray()))
                            }
                        }
                    }
                }
            }
        }
    }
}
