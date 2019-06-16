package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import maryk.sleep
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TtlDBTest {
    private fun createTestFolder() = createTestDBFolder("TtlDBTest")

    @Test
    fun ttlDBOpen() {
        Options().apply {
            setCreateIfMissing(true)
            setMaxCompactionBytes(0)
        }.use { options ->
            openTtlDB(options, createTestFolder()).use { ttlDB ->
                ttlDB.put("key".encodeToByteArray(), "value".encodeToByteArray())
                assertContentEquals("value".encodeToByteArray(), ttlDB["key".encodeToByteArray()])
                assertNotNull(ttlDB["key".encodeToByteArray()])
            }
        }
    }

    @Test
    fun ttlDBOpenWithTtl() {
        Options().apply {
            setCreateIfMissing(true)
            setMaxCompactionBytes(0)
        }.use { options ->
            openTtlDB(options, createTestFolder(), 1, false).use { ttlDB ->
                ttlDB.put("key".encodeToByteArray(), "value".encodeToByteArray())
                assertContentEquals("value".encodeToByteArray(), ttlDB["key".encodeToByteArray()])
                sleep(2000)
                ttlDB.compactRange()
                assertNull(ttlDB["key".encodeToByteArray()])
            }
        }
    }

    @Test
    fun ttlDbOpenWithColumnFamilies() {
        val cfNames = listOf(
            ColumnFamilyDescriptor(defaultColumnFamily),
            ColumnFamilyDescriptor("new_cf".encodeToByteArray())
        )
        val ttlValues = listOf(0, 1)

        val columnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
        DBOptions().apply {
            setCreateMissingColumnFamilies(true)
            setCreateIfMissing(true)
        }.use { dbOptions ->
            openTtlDB(
                dbOptions,
                createTestFolder(),
                cfNames,
                columnFamilyHandleList, ttlValues, false
            ).use { ttlDB ->
                try {
                    ttlDB.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    assertContentEquals("value".encodeToByteArray(), ttlDB["key".encodeToByteArray()])
                    ttlDB.put(
                        columnFamilyHandleList[1], "key".encodeToByteArray(),
                        "value".encodeToByteArray()
                    )
                    assertContentEquals(
                        "value".encodeToByteArray(),
                        ttlDB.get(columnFamilyHandleList[1], "key".encodeToByteArray())
                    )
                    sleep(2000)

                    ttlDB.compactRange()
                    ttlDB.compactRange(columnFamilyHandleList[1])

                    assertNotNull(ttlDB["key".encodeToByteArray()])
                    assertNull(
                        ttlDB.get(
                            columnFamilyHandleList[1],
                            "key".encodeToByteArray()
                        )
                    )
                } finally {
                    for (columnFamilyHandle in columnFamilyHandleList) {
                        columnFamilyHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun createTtlColumnFamily() {
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openTtlDB(
                options,
                createTestFolder()
            ).use { ttlDB ->
                ttlDB.createColumnFamilyWithTtl(
                    ColumnFamilyDescriptor("new_cf".encodeToByteArray()), 1
                ).use { columnFamilyHandle ->
                    ttlDB.put(
                        columnFamilyHandle, "key".encodeToByteArray(),
                        "value".encodeToByteArray()
                    )
                    assertContentEquals("value".encodeToByteArray(), ttlDB.get(columnFamilyHandle, "key".encodeToByteArray()))
                    sleep(2000)
                    ttlDB.compactRange(columnFamilyHandle)
                    assertNull(ttlDB.get(columnFamilyHandle, "key".encodeToByteArray()))
                }
            }
        }
    }
}
