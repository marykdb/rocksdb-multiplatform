package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KeyMayExistTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("KeyMayExistTest")

    @Test
    fun keyMayExist() {
        val cfDescriptors = listOf(
            ColumnFamilyDescriptor(defaultColumnFamily),
            ColumnFamilyDescriptor("new_cf".encodeToByteArray())
        )

        val columnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder(),
                cfDescriptors, columnFamilyHandleList
            ).use { db ->
                try {
                    assertEquals(2, columnFamilyHandleList.size)
                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    // Test without column family
                    var retValue = StringBuilder()
                    var exists = db.keyMayExist("key".encodeToByteArray(), retValue)
                    assertTrue(exists)
                    assertEquals("value", retValue.toString())

                    // Slice key
                    val builder = StringBuilder("prefix")
                    val offset = builder.toString().length
                    builder.append("slice key 0")
                    val len = builder.toString().length - offset
                    builder.append("suffix")

                    val sliceKey = builder.toString().encodeToByteArray()
                    val sliceValue = "slice value 0".encodeToByteArray()
                    db.put(sliceKey, offset, len, sliceValue, 0, sliceValue.size)

                    retValue = StringBuilder()
                    exists = db.keyMayExist(sliceKey, offset, len, retValue)
                    assertTrue(exists)
                    assertContentEquals(sliceValue, retValue.toString().encodeToByteArray())

                    // Test without column family but with readOptions
                    ReadOptions().use { readOptions ->
                        retValue = StringBuilder()
                        exists = db.keyMayExist(readOptions, "key".encodeToByteArray(), retValue)
                        assertTrue(exists)
                        assertEquals("value", retValue.toString())

                        retValue = StringBuilder()
                        exists = db.keyMayExist(readOptions, sliceKey, offset, len, retValue)
                        assertTrue(exists)
                        assertContentEquals(sliceValue, retValue.toString().encodeToByteArray())
                    }

                    // Test with column family
                    retValue = StringBuilder()
                    exists = db.keyMayExist(
                        columnFamilyHandleList[0], "key".encodeToByteArray(),
                        retValue
                    )
                    assertTrue(exists)
                    assertEquals("value", retValue.toString())

                    // Test slice sky with column family
                    retValue = StringBuilder()
                    exists = db.keyMayExist(
                        columnFamilyHandleList[0], sliceKey, offset, len,
                        retValue
                    )
                    assertTrue(exists)
                    assertContentEquals(sliceValue, retValue.toString().encodeToByteArray())

                    // Test with column family and readOptions
                    ReadOptions().use { readOptions ->
                        retValue = StringBuilder()
                        exists = db.keyMayExist(
                            readOptions,
                            columnFamilyHandleList[0], "key".encodeToByteArray(),
                            retValue
                        )
                        assertTrue(exists)
                        assertEquals("value", retValue.toString())

                        // Test slice key with column family and read options
                        retValue = StringBuilder()
                        exists = db.keyMayExist(
                            readOptions,
                            columnFamilyHandleList[0], sliceKey, offset, len,
                            retValue
                        )
                        assertTrue(exists)
                        assertContentEquals(sliceValue, retValue.toString().encodeToByteArray())
                    }

                    // KeyMayExist in CF1 must return false
                    assertFalse(
                        db.keyMayExist(
                            columnFamilyHandleList[1],
                            "key".encodeToByteArray(), retValue
                        )
                    )

                    // slice key
                    assertFalse(
                        db.keyMayExist(
                            columnFamilyHandleList[1],
                            sliceKey, 1, 3, retValue
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
}
