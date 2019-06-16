package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test

class TimedEnvTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("TimedEnvTest")

    @Test
    fun construct() {
        TimedEnv(getDefaultEnv()).use {
            // no-op
        }
    }

    @Test
    fun construct_integration() {
        TimedEnv(getDefaultEnv()).use { env ->
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
