package maryk.rocksdb

import maryk.assertContentEquals
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RocksIteratorTest {
    private fun createTestFolder() = createTestDBFolder("RocksIteratorTest")

    @Test
    fun rocksIterator() {
        Options().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())

                db.newIterator().use { iterator ->
                    iterator.seekToFirst()
                    assertTrue(iterator.isValid())
                    assertContentEquals("key1".encodeToByteArray(), iterator.key())
                    assertContentEquals("value1".encodeToByteArray(), iterator.value())
                    iterator.next()
                    assertTrue(iterator.isValid())
                    assertContentEquals("key2".encodeToByteArray(), iterator.key())
                    assertContentEquals("value2".encodeToByteArray(), iterator.value())
                    iterator.next()
                    assertFalse(iterator.isValid())
                    iterator.seekToLast()
                    iterator.prev()
                    assertTrue(iterator.isValid())
                    assertContentEquals("key1".encodeToByteArray(), iterator.key())
                    assertContentEquals("value1".encodeToByteArray(), iterator.value())
                    iterator.seekToFirst()
                    iterator.seekToLast()
                    assertTrue(iterator.isValid())
                    assertContentEquals("key2".encodeToByteArray(), iterator.key())
                    assertContentEquals("value2".encodeToByteArray(),iterator.value())
                    iterator.status()
                }

                db.newIterator().use { iterator ->
                    iterator.seek("key0".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key1".encodeToByteArray(), iterator.key())

                    iterator.seek("key1".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key1".encodeToByteArray(), iterator.key())

                    iterator.seek("key1.5".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key2".encodeToByteArray(), iterator.key())

                    iterator.seek("key2".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key2".encodeToByteArray(), iterator.key())

                    iterator.seek("key3".encodeToByteArray())
                    assertFalse(iterator.isValid())
                }

                db.newIterator().use { iterator ->
                    iterator.seekForPrev("key0".encodeToByteArray())
                    assertFalse(iterator.isValid())

                    iterator.seekForPrev("key1".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key1".encodeToByteArray(), iterator.key())

                    iterator.seekForPrev("key1.5".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key1".encodeToByteArray(), iterator.key())

                    iterator.seekForPrev("key2".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key2".encodeToByteArray(), iterator.key())

                    iterator.seekForPrev("key3".encodeToByteArray())
                    assertTrue(iterator.isValid())
                    assertContentEquals("key2".encodeToByteArray(), iterator.key())
                }
            }
        }
    }

    @Test
    fun rocksIteratorWithPrefix() {
        Options().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
            useFixedLengthPrefixExtractor(3)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()+"-prefix"
            ).use { db ->
                db.put("kex1".encodeToByteArray(), "valueX".encodeToByteArray())
                db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())
                db.put("kez1".encodeToByteArray(), "valueZ".encodeToByteArray())

                ReadOptions().also {
                    it.setPrefixSameAsStart(true)
                }.use { readOptions ->
                    db.newIterator(readOptions).use { iterator ->
                        assertFalse(iterator.isValid())

                        iterator.seek("key".encodeToByteArray())
                        assertTrue(iterator.isValid())
                        assertContentEquals("key1".encodeToByteArray(), iterator.key())

                        iterator.next()
                        assertTrue(iterator.isValid())
                        assertContentEquals("key2".encodeToByteArray(), iterator.key())

                        iterator.next()
                        assertFalse(iterator.isValid())
                    }

                    db.newIterator(readOptions).use { iterator ->
                        assertFalse(iterator.isValid())

                        iterator.seekForPrev("key4".encodeToByteArray())
                        assertTrue(iterator.isValid())
                        assertContentEquals("key2".encodeToByteArray(), iterator.key())

                        iterator.prev()
                        assertTrue(iterator.isValid())
                        assertContentEquals("key1".encodeToByteArray(), iterator.key())

                        iterator.prev()
                        assertFalse(iterator.isValid())
                    }
                }
            }
        }
    }
}
