package maryk.rocksdb

import maryk.ByteBuffer
import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.BuiltinComparator.BYTEWISE_COMPARATOR
import maryk.rocksdb.BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComparatorTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("ComparatorTest")

    @Test
    fun builtinForwardComparator() {
        Options().apply {
            setCreateIfMissing(true)
            setComparator(BYTEWISE_COMPARATOR)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { rocksDb ->
                rocksDb.put("abc1".encodeToByteArray(), "abc1".encodeToByteArray())
                rocksDb.put("abc2".encodeToByteArray(), "abc2".encodeToByteArray())
                rocksDb.put("abc3".encodeToByteArray(), "abc3".encodeToByteArray())

                rocksDb.newIterator().use { rocksIterator ->
                    // Iterate over keys using a iterator
                    rocksIterator.seekToFirst()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.value())
                    rocksIterator.next()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc2".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc2".encodeToByteArray(), rocksIterator.value())
                    rocksIterator.next()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.value())
                    rocksIterator.next()
                    assertFalse(rocksIterator.isValid())
                    // Get last one
                    rocksIterator.seekToLast()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.value())
                    // Seek for abc
                    rocksIterator.seek("abc".encodeToByteArray())
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.value())
                }
            }
        }
    }

    @Test
    fun builtinReverseComparator() {
        Options().apply {
            setCreateIfMissing(true)
            setComparator(REVERSE_BYTEWISE_COMPARATOR)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { rocksDb ->

                rocksDb.put("abc1".encodeToByteArray(), "abc1".encodeToByteArray())
                rocksDb.put("abc2".encodeToByteArray(), "abc2".encodeToByteArray())
                rocksDb.put("abc3".encodeToByteArray(), "abc3".encodeToByteArray())

                rocksDb.newIterator().use { rocksIterator ->
                    // Iterate over keys using a iterator
                    rocksIterator.seekToFirst()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.value())
                    rocksIterator.next()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc2".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc2".encodeToByteArray(), rocksIterator.value())
                    rocksIterator.next()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.value())
                    rocksIterator.next()
                    assertFalse(rocksIterator.isValid())
                    // Get last one
                    rocksIterator.seekToLast()
                    assertTrue(rocksIterator.isValid())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc1".encodeToByteArray(), rocksIterator.value())
                    // Will be invalid because abc is after abc1
                    rocksIterator.seek("abc".encodeToByteArray())
                    assertFalse(rocksIterator.isValid())
                    // Will be abc3 because the next one after abc999 is abc3
                    rocksIterator.seek("abc999".encodeToByteArray())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.key())
                    assertContentEquals("abc3".encodeToByteArray(), rocksIterator.value())
                }
            }
        }
    }

    @Test
    fun builtinComparatorEnum() {
        assertEquals(0, BYTEWISE_COMPARATOR.ordinal)
        assertEquals(1, REVERSE_BYTEWISE_COMPARATOR.ordinal)
        assertEquals(2, BuiltinComparator.values().size)
        assertEquals(BYTEWISE_COMPARATOR, BuiltinComparator.valueOf("BYTEWISE_COMPARATOR"))
    }
}
