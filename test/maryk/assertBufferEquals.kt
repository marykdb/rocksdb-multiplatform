import maryk.ByteBuffer
import kotlin.test.assertTrue
import kotlin.test.fail

fun assertBufferEquals(expected: ByteBuffer?, actual: ByteBuffer?) {
    if (expected == null && actual == null) return
    if (expected == null) fail("Expected ByteBuffer is null but actual is not null")
    if (actual == null) fail("Actual ByteBuffer is null but expected is not null")

    val expectedSize = expected.position()
    val actualSize = actual.position()

    // Check if the sizes match.
    assertTrue(expectedSize == actualSize,
        "ByteBuffer sizes differ: expected size $expectedSize, actual size $actualSize")

    // Compare each byte one by one.
    for (i in 0 until expectedSize) {
        val expectedByte = expected[i]
        val actualByte = actual[i]
        assertTrue(expectedByte == actualByte,
            "ByteBuffers differ at index $i: expected $expectedByte, but got $actualByte")
    }
}
