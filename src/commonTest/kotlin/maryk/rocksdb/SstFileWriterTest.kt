package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SstFileWriterTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun writeAndIngestExternalFile() {
        val dbPath = createTestDBFolder("SstFileWriterTest_ingest")
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                val sstPath = "$dbPath/external.sst"
                EnvOptions().use { envOptions ->
                    SstFileWriter(envOptions, options).use { writer ->
                        writer.open(sstPath)
                        writer.put("a".encodeToByteArray(), "1".encodeToByteArray())
                        writer.put("b".encodeToByteArray(), "2".encodeToByteArray())
                        writer.finish()
                        assertTrue(writer.fileSize() > 0)
                    }
                }

                IngestExternalFileOptions().use { ingestOptions ->
                    ingestOptions.setMoveFiles(true)
                    db.ingestExternalFile(listOf(sstPath), ingestOptions)
                }

                val loaded = db["a".encodeToByteArray()]
                assertNotNull(loaded)
                assertContentEquals("1".encodeToByteArray(), loaded)
            }
        }
    }
}
