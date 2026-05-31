package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertTrue
import maryk.rocksdb.util.createTestDBFolder

class PerfContextNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun perfContextReportIsCopiedAndFreed() {
        val dbPath = createTestDBFolder("PerfContextNativeTest_report")

        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                db.setPerfLevel(PerfLevel.ENABLE_TIME)
                db.getPerfContext().use { context ->
                    context.reset()
                    db.put("perf-key".encodeToByteArray(), "perf-value".encodeToByteArray())
                    db["perf-key".encodeToByteArray()]

                    assertTrue(context.toString(excludeZeroCounters = false).isNotEmpty())
                    assertTrue(context.toString(excludeZeroCounters = true).isNotEmpty())
                }
            }
        }
    }
}
