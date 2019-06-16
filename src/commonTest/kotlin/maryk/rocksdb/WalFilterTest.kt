package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.WalProcessingOption.CONTINUE_PROCESSING
import maryk.rocksdb.WalProcessingOption.CORRUPTED_RECORD
import maryk.rocksdb.util.createTestDBFolder
import maryk.rocksdb.util.dummyString
import maryk.rocksdb.util.optionsForLogIterTest
import kotlin.test.Test
import kotlin.test.assertTrue

class WalFilterTest {
    private fun createTestFolder() = createTestDBFolder("WalFilterTest")

    @Test
    fun walFilter() {
        // Create 3 batches with two keys each
        val batchKeys = arrayOf(
            arrayOf("key1".encodeToByteArray(), "key2".encodeToByteArray()),
            arrayOf("key3".encodeToByteArray(), "key4".encodeToByteArray()),
            arrayOf("key5".encodeToByteArray(), "key6".encodeToByteArray())
        )

        val cfDescriptors = listOf(
            ColumnFamilyDescriptor(defaultColumnFamily),
            ColumnFamilyDescriptor("pikachu".encodeToByteArray())
        )
        val cfHandles = mutableListOf<ColumnFamilyHandle>()

        val testFolder = createTestFolder()

        // Test with all WAL processing options
        for (option in WalProcessingOption.values()) {
            optionsForLogIterTest().use { options ->
                DBOptions(options).apply {
                    setCreateMissingColumnFamilies(true)
                }.use { dbOptions ->
                    openRocksDB(
                        dbOptions,
                        testFolder,
                        cfDescriptors, cfHandles
                    ).use { db ->
                        try {
                            WriteOptions().use { writeOptions ->
                                // Write given keys in given batches
                                for (i in batchKeys.indices) {
                                    val batch = WriteBatch()
                                    for (j in 0 until batchKeys[i].size) {
                                        batch.put(cfHandles[0], batchKeys[i][j], dummyString(1024))
                                    }
                                    db.write(writeOptions, batch)
                                }
                            }
                        } finally {
                            for (cfHandle in cfHandles) {
                                cfHandle.close()
                            }
                            cfHandles.clear()
                        }
                    }
                }
            }

            // Create a test filter that would apply wal_processing_option at the first
            // record
            val applyOptionForRecordIndex = 1
            TestableWalFilter(option, applyOptionForRecordIndex).use { walFilter ->

                optionsForLogIterTest().use { options ->
                    DBOptions(options).apply {
                        setWalFilter(walFilter)
                    }.use { dbOptions ->
                        try {
                            openRocksDB(
                                dbOptions,
                                testFolder,
                                cfDescriptors, cfHandles
                            ).use {
                                try {
                                    assertTrue(walFilter.logNumbers.isNotEmpty())
                                    assertTrue(walFilter.logFileNames.isNotEmpty())
                                } finally {
                                    for (cfHandle in cfHandles) {
                                        cfHandle.close()
                                    }
                                    cfHandles.clear()
                                }
                            }
                        } catch (e: RocksDBException) {
                            if (option != CORRUPTED_RECORD) {
                                // exception is expected when CORRUPTED_RECORD!
                                throw e
                            }
                        }
                    }
                }
            }
        }
    }

    private class TestableWalFilter(
        private val walProcessingOption: WalProcessingOption,
        private val applyOptionForRecordIndex: Int
    ) : AbstractWalFilter() {
        internal lateinit var cfLognumber: Map<Int, Long>
        internal lateinit var cfNameId: Map<String, Int>
        internal val logNumbers = mutableListOf<Long>()
        internal val logFileNames = mutableListOf<String>()
        private var currentRecordIndex = 0

        override fun columnFamilyLogNumberMap(
            cfLognumber: Map<Int, Long>,
            cfNameId: Map<String, Int>
        ) {
            this.cfLognumber = cfLognumber
            this.cfNameId = cfNameId
        }

        override fun logRecordFound(
            logNumber: Long, logFileName: String, batch: WriteBatch,
            newBatch: WriteBatch
        ): LogRecordFoundResult {

            logNumbers.add(logNumber)
            logFileNames.add(logFileName)

            val optionToReturn = if (currentRecordIndex == applyOptionForRecordIndex) {
                walProcessingOption
            } else {
                CONTINUE_PROCESSING
            }

            currentRecordIndex++

            return LogRecordFoundResult(optionToReturn, false)
        }

        override fun name() = "testable-wal-filter"
    }
}
