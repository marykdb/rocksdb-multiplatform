package maryk

import kotlin.test.assertTrue
import kotlin.test.fail

fun assertContentEquals(expected: ByteArray?, actual: ByteArray?) {
    if (actual == null) {
        if (expected != null) {
            fail("Actual byte array cannot be null")
        }

        return
    } else if (expected == null) {
        fail("Actual byte array should be null")
    }
    assertTrue(expected.contentEquals(actual), "Actual byte array [${actual.joinToString(", ")}] does not match expected byte array [${expected.joinToString(", ")}]")
}
