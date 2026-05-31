package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CheckpointNativeTest {
    @Test
    fun checkpointCannotUseClosedOwnerDb() {
        val dbPath = createTestDBFolder("CheckpointNativeTest_owner")

        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            val checkpoint = createCheckpoint(db)

            db.close()

            checkpoint.use {
                assertFailsWith<IllegalStateException> {
                    it.createCheckpoint("$dbPath-checkpoint")
                }
            }
        }
    }
}
