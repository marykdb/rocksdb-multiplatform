package maryk.rocksdb

import maryk.createFolder
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ExtendedTablePropertiesTest {
    init { loadRocksDBLibrary() }

    @Test
    fun copiesAllIndexFieldsAndPropertyMapsBeforeReaderCloses() {
        val path = createTestDBFolder("ExtendedTablePropertiesTest").also { createFolder(it) } + "/external.sst"
        var copied: TableProperties? = null
        Options().use { options ->
            EnvOptions().use { env ->
                SstFileWriter(env, options).use { writer ->
                    writer.open(path)
                    writer.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    writer.finish()
                }
            }
            SstFileReader(options).use { reader ->
                reader.open(path)
                copied = reader.getTableProperties()
            }
        }
        val properties = assertNotNull(copied)
        assertEquals(1, properties.numEntries())
        assertTrue(properties.indexPartitions() >= 0)
        assertTrue(properties.topLevelIndexSize() >= 0)
        assertTrue(properties.indexKeyIsUserKey() in 0L..1L)
        assertTrue(properties.indexValueIsDeltaEncoded() in 0L..1L)
        val first = properties.userCollectedProperties()
        assertTrue(first.containsKey("rocksdb.external_sst_file.version"))
        assertEquals(first, properties.userCollectedProperties())
        assertEquals(properties.readableProperties(), properties.readableProperties())
    }
}
