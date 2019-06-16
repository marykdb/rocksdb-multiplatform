package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith

class HdfsEnvTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("HdfsEnvTest")

    // expect org.rocksdb.RocksDBException: Not compiled with hdfs support
    @Test
    fun construct() {
        assertFailsWith<RocksDBException> {
            HdfsEnv("hdfs://localhost:5000").use {
                // no-op
            }
        }
    }

    // expect org.rocksdb.RocksDBException: Not compiled with hdfs support
    @Test
    fun construct_integration() {
        assertFailsWith<RocksDBException> {
            HdfsEnv("hdfs://localhost:5000").use { env ->
                Options().apply {
                    setCreateIfMissing(true)
                    setEnv(env)
                }.use { options ->
                    openRocksDB(options, createTestFolder()).use { db ->
                        db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                    }
                }
            }
        }
    }
}
