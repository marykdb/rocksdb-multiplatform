package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SnapshotNativeTest {
    @Test
    fun databaseCloseInvalidatesSnapshot() {
        val dbPath = createTestDBFolder("SnapshotNativeTest_owner")

        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            val snapshot = requireNotNull(db.getSnapshot())

            db.close()

            assertFailsWith<IllegalStateException> {
                snapshot.getSequenceNumber()
            }
            snapshot.close()
        }
    }
}
