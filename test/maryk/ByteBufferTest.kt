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
    fun wrapEmptyByteBufferInvokesCallback() {
        var called = false

        wrapByteBuffer(ByteArray(0)) { buffer ->
            called = true
            assertEquals(0, buffer.remaining())
            assertContentEquals(ByteArray(0), buffer.array())
        }

        assertTrue(called)
    }

    @Test
    fun duplicateEmptyByteBufferInvokesCallback() {
        var called = false

        wrapByteBuffer(ByteArray(0)) { buffer ->
            duplicateByteBuffer(buffer) { duplicate ->
                called = true
                assertEquals(0, duplicate.remaining())
                assertContentEquals(ByteArray(0), duplicate.array())
            }
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
    fun partialGetChecksOverflowingDestinationBounds() {
        wrapByteBuffer(byteArrayOf(1, 2, 3)) { buffer ->
            assertFailsWith<IndexOutOfBoundsException> {
                buffer[ByteArray(2), Int.MAX_VALUE, 1]
            }
        }
    }

    @Test
    fun partialPutChecksOverflowingSourceBounds() {
        allocateByteBuffer(3) { buffer ->
            assertFailsWith<IndexOutOfBoundsException> {
                buffer.put(byteArrayOf(1, 2), Int.MAX_VALUE, 1)
            }
        }
    }

    @Test
    fun allocationRejectsNegativeCapacity() {
        assertFailsWith<IllegalArgumentException> {
            allocateByteBuffer(-1) {
                // no-op
            }
        }

        assertFailsWith<IllegalArgumentException> {
            allocateDirectByteBuffer(-1) {
                // no-op
            }
        }
    }

    @Test
    fun emptyAllocationInvokesCallback() {
        var heapCalled = false
        var directCalled = false

        allocateByteBuffer(0) { buffer ->
            heapCalled = true
            assertEquals(0, buffer.remaining())
            assertContentEquals(ByteArray(0), buffer.array())
        }

        allocateDirectByteBuffer(0) { buffer ->
            directCalled = true
            assertEquals(0, buffer.remaining())
        }

        assertTrue(heapCalled)
        assertTrue(directCalled)
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
