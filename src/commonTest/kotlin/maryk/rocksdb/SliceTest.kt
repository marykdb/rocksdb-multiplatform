package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SliceTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun slice() {
        Slice("testSlice").use { slice ->
            assertFalse(slice.empty())
            assertEquals(9, slice.size())
            assertContentEquals("testSlice".encodeToByteArray(), slice.data())
        }

        Slice("otherSlice".encodeToByteArray()).use { otherSlice ->
            assertContentEquals("otherSlice".encodeToByteArray(), otherSlice.data())
        }

        Slice(
            "otherSlice".encodeToByteArray(),
            5
        ).use { thirdSlice -> assertContentEquals("Slice".encodeToByteArray(), thirdSlice.data()) }
    }

    @Test
    fun sliceClear() {
        Slice("abc").use { slice ->
            assertEquals("abc", slice.toString())
            slice.clear()
            assertTrue(slice.toString().isEmpty())
            slice.clear()  // make sure we don't double-free
        }
    }

    @Test
    fun sliceRemovePrefix() {
        Slice("abc").use { slice ->
            assertEquals("abc", slice.toString())
            slice.removePrefix(1)
            assertEquals("bc", slice.toString())
        }
    }

    @Test
    fun sliceEquals() {
        Slice("abc").use { slice ->
            Slice("abc").use { slice2 ->
                assertEquals(slice, slice2)
                assertEquals(slice.hashCode(), slice2.hashCode())
            }
        }
    }

    @Test
    fun sliceStartWith() {
        Slice("matchpoint").use { slice ->
            Slice("mat").use { match ->
                Slice("nomatch").use { noMatch ->
                    assertTrue(slice.startsWith(match))
                    assertFalse(slice.startsWith(noMatch))
                }
            }
        }
    }

    @Test
    fun sliceToString() {
        Slice("stringTest").use { slice ->
            assertEquals("stringTest", slice.toString())
            assertNotEquals("", slice.toString(true))
        }
    }
}
