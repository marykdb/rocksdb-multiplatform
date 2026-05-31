package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TtlDBNativeTest {
    @Test
    fun ttlDbCloseInvalidatesInheritedDbChildren() {
        val dbPath = createTestDBFolder("TtlDBNativeTest_inherited_children")

        Options().setCreateIfMissing(true).use { options ->
            openTtlDB(options, dbPath).use { db ->
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                val snapshot = requireNotNull(db.getSnapshot())
                val logIterator = db.getUpdatesSince(0)

                db.close()

                assertFailsWith<IllegalStateException> {
                    snapshot.getSequenceNumber()
                }
                assertFailsWith<IllegalStateException> {
                    logIterator.isValid()
                }
                snapshot.close()
                logIterator.close()
            }
        }
    }
}
