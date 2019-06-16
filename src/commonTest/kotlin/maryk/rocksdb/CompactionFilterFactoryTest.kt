package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.test.RemoveEmptyValueCompactionFilterFactory
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFalse

class CompactionFilterFactoryTest {
    private fun createTestFolder() = createTestDBFolder("CompactionFilterFactoryTest")

    @Test
    fun columnFamilyOptions_setCompactionFilterFactory() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { options ->
            RemoveEmptyValueCompactionFilterFactory().use { compactionFilterFactory ->
                ColumnFamilyOptions().apply {
                    setCompactionFilterFactory(compactionFilterFactory)
                }.use { new_cf_opts ->
                    val cfNames = listOf(
                        ColumnFamilyDescriptor(defaultColumnFamily),
                        ColumnFamilyDescriptor("new_cf".encodeToByteArray(), new_cf_opts)
                    )

                    val cfHandles = mutableListOf<ColumnFamilyHandle>()

                    openRocksDB(
                        options,
                        createTestFolder(), cfNames, cfHandles
                    ).use { rocksDb ->
                        try {
                            val key1 = "key1".encodeToByteArray()
                            val key2 = "key2".encodeToByteArray()

                            val value1 = "value1".encodeToByteArray()
                            val value2 = ByteArray(0)

                            rocksDb.put(cfHandles[1], key1, value1)
                            rocksDb.put(cfHandles[1], key2, value2)

                            rocksDb.compactRange(cfHandles[1])

                            assertContentEquals(value1, rocksDb.get(cfHandles[1], key1))
                            assertFalse(rocksDb.keyMayExist(cfHandles[1], key2, StringBuilder()))
                        } finally {
                            for (cfHandle in cfHandles) {
                                cfHandle.close()
                            }
                        }
                    }
                }
            }
        }
    }
}
