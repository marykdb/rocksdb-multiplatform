package maryk.rocksdb

import maryk.allocateDirectByteBuffer
import maryk.encodeToByteArray
import maryk.wrapByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DirectSliceTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun directSlice() {
        DirectSlice("abc").use { directSlice ->
            DirectSlice("abc").use { otherSlice ->
                assertEquals("abc", directSlice.toString())
                // clear first slice
                directSlice.clear()
                assertTrue(directSlice.toString().isEmpty())
                // get first char in otherslice
                assertEquals("a".encodeToByteArray()[0], otherSlice[0])
                // remove prefix
                otherSlice.removePrefix(1)
                assertEquals("bc", otherSlice.toString())
            }
        }
    }

    @Test
    fun directSliceWithByteBuffer() {
        val data = "Some text".encodeToByteArray()
        allocateDirectByteBuffer(data.size + 1) { buffer ->
            buffer.put(data)
            buffer.put(data.size, 0.toByte())

            DirectSlice(buffer).use { directSlice ->
                assertEquals("Some text", directSlice.toString())
            }
        }
    }

    @Test
    fun directSliceWithByteBufferAndLength() {
        val data = "Some text".encodeToByteArray()
        allocateDirectByteBuffer(data.size) { buffer ->
            buffer.put(data)
            DirectSlice(buffer, 4).use { directSlice ->
                assertEquals("Some", directSlice.toString())
            }
        }
    }

    @Test
    fun directSliceInitWithoutDirectAllocation() {
        val data = "Some text".encodeToByteArray()
        wrapByteBuffer(data) { buffer ->
            assertFailsWith<IllegalArgumentException> {
                DirectSlice(buffer).use{
                    //no-op
                }
            }
        }
    }

    @Test
    fun directSlicePrefixInitWithoutDirectAllocation() {
        val data = "Some text".encodeToByteArray()
        wrapByteBuffer(data) { buffer ->
            assertFailsWith<IllegalArgumentException> {
                DirectSlice(buffer, 4).use {
                    //no-op
                }
            }
        }
    }

    @Test
    fun directSliceClear() {
        DirectSlice("abc").use { directSlice ->
            assertEquals("abc", directSlice.toString())
            directSlice.clear()
            assertTrue(directSlice.toString().isEmpty())
            directSlice.clear()  // make sure we don't double-free
        }
    }

    @Test
    fun directSliceRemovePrefix() {
        DirectSlice("abc").use { directSlice ->
            assertEquals("abc", directSlice.toString())
            directSlice.removePrefix(1)
            assertEquals("bc", directSlice.toString())
        }
    }
}
