package maryk.rocksdb

import maryk.createFolder
import maryk.decodeToString
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CheckPointTest {
    private val checkpointFolder = "build/test-database/CheckPointTest".apply {
        createFolder(this)
    }

    private fun createTestFolder() = createTestDBFolder("CheckPointTest")

    @Test
    fun checkPoint() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                createCheckpoint(db).use { checkpoint ->
                    checkpoint.createCheckpoint("$checkpointFolder/snapshot1")
                    db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())
                    checkpoint.createCheckpoint("$checkpointFolder/snapshot2")
                }
            }

            openRocksDB(
                options,
                "$checkpointFolder/snapshot1"
            ).use { db ->
                assertEquals("value", db["key".encodeToByteArray()]!!.decodeToString())
                assertNull(db["key2".encodeToByteArray()])
            }

            openRocksDB(
                options,
                "$checkpointFolder/snapshot2"
            ).use { db ->
                assertEquals("value", db["key".encodeToByteArray()]!!.decodeToString())
                assertEquals("value2", db["key2".encodeToByteArray()]!!.decodeToString())
            }
        }
    }

    @Test
    fun failIfDbIsNull() {
        assertFailsWith<IllegalArgumentException> {
            createCheckpoint(null).use {}
        }
    }

    @Test
    fun failIfDbNotInitialized() {
        openRocksDB(
            createTestFolder()
        ).use { db ->
            db.close()
            assertFailsWith<IllegalStateException> {
                createCheckpoint(db)
            }
        }
    }

    @Test
    fun failWithIllegalPath() {
        openRocksDB(createTestFolder()).use { db ->
            createCheckpoint(db).use { checkpoint ->
                assertFailsWith<RocksDBException> {
                    checkpoint.createCheckpoint("/Z:///:\\C:\\TZ/-")
                }
            }
        }
    }
}
