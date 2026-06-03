package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
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
                        writer.put("c".encodeToByteArray(), ByteArray(0))
                        writer.finish()
                        assertTrue(writer.fileSize() > 0)
                    }
                }

                SstFileReader(options).use { reader ->
                    reader.open(sstPath)
                    reader.verifyChecksum()
                    val properties = reader.getTableProperties()
                    assertEquals(3, properties.numEntries())

                    ReadOptions().use { readOptions ->
                        reader.newIterator(readOptions).use { iterator ->
                            iterator.seekToFirst()
                            assertTrue(iterator.isValid())
                            assertContentEquals("a".encodeToByteArray(), iterator.key())
                            assertContentEquals("1".encodeToByteArray(), iterator.value())
                            iterator.next()
                            assertTrue(iterator.isValid())
                            assertContentEquals("b".encodeToByteArray(), iterator.key())
                            assertContentEquals("2".encodeToByteArray(), iterator.value())
                        }
                    }
                }

                IngestExternalFileOptions().use { ingestOptions ->
                    ingestOptions.setMoveFiles(true)
                    db.ingestExternalFile(listOf(sstPath), ingestOptions)
                }

                val loaded = db["a".encodeToByteArray()]
                assertNotNull(loaded)
                assertContentEquals("1".encodeToByteArray(), loaded)

                val emptyValue = db["c".encodeToByteArray()]
                assertNotNull(emptyValue)
                assertContentEquals(ByteArray(0), emptyValue)
            }
        }
    }
}
