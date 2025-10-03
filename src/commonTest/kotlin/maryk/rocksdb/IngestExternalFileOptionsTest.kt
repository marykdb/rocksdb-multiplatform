package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IngestExternalFileOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun togglesRoundTrip() {
        IngestExternalFileOptions().use { options ->
            assertFalse(options.moveFiles())
            assertTrue(options.snapshotConsistency())
            assertTrue(options.allowGlobalSeqNo())
            assertTrue(options.allowBlockingFlush())
            assertFalse(options.ingestBehind())

            options.setMoveFiles(true)
            options.setSnapshotConsistency(false)
            options.setAllowGlobalSeqNo(false)
            options.setAllowBlockingFlush(false)
            options.setIngestBehind(true)

            assertTrue(options.moveFiles())
            assertFalse(options.snapshotConsistency())
            assertFalse(options.allowGlobalSeqNo())
            assertFalse(options.allowBlockingFlush())
            assertTrue(options.ingestBehind())
        }
    }
}
