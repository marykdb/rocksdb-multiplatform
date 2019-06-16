package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IngestExternalFileOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun createExternalSstFileInfoWithoutParameters() {
        IngestExternalFileOptions().use { options ->
            assertNotNull(options)
        }
    }

    @Test
    fun createExternalSstFileInfoWithParameters() {
        val moveFiles = Random.nextBoolean()
        val snapshotConsistency = Random.nextBoolean()
        val allowGlobalSeqNo = Random.nextBoolean()
        val allowBlockingFlush = Random.nextBoolean()
        IngestExternalFileOptions(
            moveFiles, snapshotConsistency,
            allowGlobalSeqNo, allowBlockingFlush
        ).use { options ->
            assertNotNull(options)
            assertEquals(moveFiles, options.moveFiles())
            assertEquals(snapshotConsistency, options.snapshotConsistency())
            assertEquals(allowGlobalSeqNo, options.allowGlobalSeqNo())
            assertEquals(allowBlockingFlush, options.allowBlockingFlush())
        }
    }

    @Test
    fun moveFiles() {
        IngestExternalFileOptions().use { options ->
            val moveFiles = Random.nextBoolean()
            options.setMoveFiles(moveFiles)
            assertEquals(moveFiles, options.moveFiles())
        }
    }

    @Test
    fun snapshotConsistency() {
        IngestExternalFileOptions().use { options ->
            val snapshotConsistency = Random.nextBoolean()
            options.setSnapshotConsistency(snapshotConsistency)
            assertEquals(snapshotConsistency, options.snapshotConsistency())
        }
    }

    @Test
    fun allowGlobalSeqNo() {
        IngestExternalFileOptions().use { options ->
            val allowGlobalSeqNo = Random.nextBoolean()
            options.setAllowGlobalSeqNo(allowGlobalSeqNo)
            assertEquals(allowGlobalSeqNo, options.allowGlobalSeqNo())
        }
    }

    @Test
    fun allowBlockingFlush() {
        IngestExternalFileOptions().use { options ->
            val allowBlockingFlush = Random.nextBoolean()
            options.setAllowBlockingFlush(allowBlockingFlush)
            assertEquals(allowBlockingFlush, options.allowBlockingFlush())
        }
    }

    @Test
    fun ingestBehind() {
        IngestExternalFileOptions().use { options ->
            assertFalse(options.ingestBehind())
            options.setIngestBehind(true)
            assertTrue(options.ingestBehind())
        }
    }

    @Test
    fun writeGlobalSeqno() {
        IngestExternalFileOptions().use { options ->
            assertTrue(options.writeGlobalSeqno())
            options.setWriteGlobalSeqno(false)
            assertFalse(options.writeGlobalSeqno())
        }
    }
}
