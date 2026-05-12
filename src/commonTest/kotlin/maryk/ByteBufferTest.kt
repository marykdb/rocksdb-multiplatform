package maryk

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ByteBufferTest {
    @Test
    fun wrapByteBufferInvokesCallback() {
        var called = false

        wrapByteBuffer(byteArrayOf(1, 2, 3)) { buffer ->
            called = true
            assertContentEquals(byteArrayOf(1, 2, 3), buffer.array())
        }

        assertTrue(called)
    }

    @Test
    fun partialGetAdvancesByLength() {
        wrapByteBuffer(byteArrayOf(1, 2, 3, 4)) { buffer ->
            val first = ByteArray(3)
            buffer[first, 1, 2]

            assertEquals(2, buffer.position())
            assertContentEquals(byteArrayOf(0, 1, 2), first)

            val second = ByteArray(2)
            buffer[second]

            assertContentEquals(byteArrayOf(3, 4), second)
            assertEquals(4, buffer.position())
        }
    }

    @Test
    fun partialGetChecksDestinationBounds() {
        wrapByteBuffer(byteArrayOf(1, 2, 3)) { buffer ->
            assertFailsWith<IndexOutOfBoundsException> {
                buffer[ByteArray(2), 1, 2]
            }
        }
    }

    @Test
    fun putChecksRemainingSpace() {
        allocateByteBuffer(2) { buffer ->
            assertFails {
                buffer.put(byteArrayOf(1, 2, 3))
            }
            assertEquals(0, buffer.position())
        }
    }

    @Test
    fun indexedAccessChecksLimit() {
        wrapByteBuffer(byteArrayOf(1, 2, 3)) { buffer ->
            buffer.limit(2)

            assertFailsWith<IndexOutOfBoundsException> {
                buffer[2]
            }
            assertFailsWith<IndexOutOfBoundsException> {
                buffer.put(2, 4)
            }
        }
    }
}
