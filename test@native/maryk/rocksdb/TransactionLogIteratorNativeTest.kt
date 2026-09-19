package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TransactionLogIteratorNativeTest {
    @Test
    fun databaseCloseInvalidatesTransactionLogIterator() {
        val dbPath = createTestDBFolder("TransactionLogIteratorNativeTest_owner")

        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            db.put("key".encodeToByteArray(), "value".encodeToByteArray())

            val iterator = db.getUpdatesSince(0)
            db.close()

            assertFailsWith<IllegalStateException> {
                iterator.isValid()
            }
            iterator.close()
        }
    }
}
