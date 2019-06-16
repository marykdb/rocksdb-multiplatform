package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OptionsUtilTest {
    private fun createTestFolder() = createTestDBFolder("OptionsUtilTest")

    internal enum class TestAPI {
        LOAD_LATEST_OPTIONS, LOAD_OPTIONS_FROM_FILE
    }

    @Test
    fun loadLatestOptions() {
        verifyOptions(TestAPI.LOAD_LATEST_OPTIONS)
    }

    @Test
    fun loadOptionsFromFile() {
        verifyOptions(TestAPI.LOAD_OPTIONS_FROM_FILE)
    }

    @Test
    fun getLatestOptionsFileName() {
        val dbPath = createTestFolder()
        Options().setCreateIfMissing(true)
            .use { options -> openRocksDB(options, dbPath).use { db -> assertNotNull(db) } }

        val fName = OptionsUtil.getLatestOptionsFileName(dbPath, getDefaultEnv())
        assertNotNull(fName)
        assertTrue(fName.startsWith("OPTIONS-"))
        // System.out.println("latest options fileName: " + fName);
    }

    private fun verifyOptions(apiType: TestAPI) {
        val dbPath = createTestFolder()
        val options = Options()
            .setCreateIfMissing(true)
            .setParanoidChecks(false)
            .setMaxOpenFiles(478)
            .setDelayedWriteRate(1234567L)
        val baseDefaultCFOpts = ColumnFamilyOptions()
        val secondCFName = "new_cf".encodeToByteArray()
        val baseSecondCFOpts = ColumnFamilyOptions()
            .setWriteBufferSize((70 * 1024).toLong())
            .setMaxWriteBufferNumber(7)
            .setMaxBytesForLevelBase((53 * 1024 * 1024).toLong())
            .setLevel0FileNumCompactionTrigger(3)
            .setLevel0SlowdownWritesTrigger(51)
            .setBottommostCompressionType(CompressionType.ZSTD_COMPRESSION)

        // Create a database with a new column family
        openRocksDB(options, dbPath).use { db ->
            assertNotNull(db)

            // create column family
            db.createColumnFamily(ColumnFamilyDescriptor(secondCFName, baseSecondCFOpts)).use {
                // Check if succeeds
                assertTrue(true)
            }
        }

        // Read the options back and verify
        DBOptions().use { dbOptions ->
            val cfDescs = mutableListOf<ColumnFamilyDescriptor>()
            var path = dbPath
            if (apiType == TestAPI.LOAD_LATEST_OPTIONS) {
                OptionsUtil.loadLatestOptions(path, getDefaultEnv(), dbOptions, cfDescs, false)
            } else if (apiType == TestAPI.LOAD_OPTIONS_FROM_FILE) {
                path = dbPath + "/" + OptionsUtil.getLatestOptionsFileName(dbPath, getDefaultEnv())
                OptionsUtil.loadOptionsFromFile(path, getDefaultEnv(), dbOptions, cfDescs, false)
            }

            assertEquals(options.createIfMissing(), dbOptions.createIfMissing())
            assertEquals(options.paranoidChecks(), dbOptions.paranoidChecks())
            assertEquals(options.maxOpenFiles(), dbOptions.maxOpenFiles())
            assertEquals(options.delayedWriteRate(), dbOptions.delayedWriteRate())

            assertEquals(2, cfDescs.size)
            assertNotNull(cfDescs[0])
            assertNotNull(cfDescs[1])
            assertContentEquals(defaultColumnFamily, cfDescs[0].getName())
            assertContentEquals(secondCFName, cfDescs[1].getName())

            cfDescs[0].getOptions().apply {
                assertEquals(baseDefaultCFOpts.writeBufferSize(), writeBufferSize())
                assertEquals(baseDefaultCFOpts.maxWriteBufferNumber(), maxWriteBufferNumber())
                assertEquals(baseDefaultCFOpts.maxBytesForLevelBase(), maxBytesForLevelBase())
                assertEquals(baseDefaultCFOpts.level0FileNumCompactionTrigger(), level0FileNumCompactionTrigger())
                assertEquals(baseDefaultCFOpts.level0SlowdownWritesTrigger(), level0SlowdownWritesTrigger())
                assertEquals(baseDefaultCFOpts.bottommostCompressionType(), bottommostCompressionType())
            }

            cfDescs[1].getOptions().apply {
                assertEquals(baseSecondCFOpts.writeBufferSize(), writeBufferSize())
                assertEquals(baseSecondCFOpts.maxWriteBufferNumber(), maxWriteBufferNumber())
                assertEquals(baseSecondCFOpts.maxBytesForLevelBase(), maxBytesForLevelBase())
                assertEquals(baseSecondCFOpts.level0FileNumCompactionTrigger(), level0FileNumCompactionTrigger())
                assertEquals(baseSecondCFOpts.level0SlowdownWritesTrigger(), level0SlowdownWritesTrigger())
                assertEquals(baseSecondCFOpts.bottommostCompressionType(), bottommostCompressionType())
            }
        }
    }
}
