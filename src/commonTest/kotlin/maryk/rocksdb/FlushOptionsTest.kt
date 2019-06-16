package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlushOptionsTest {
    @Test
    fun waitForFlush() {
        FlushOptions().use { flushOptions ->
            assertTrue(flushOptions.waitForFlush())
            flushOptions.setWaitForFlush(false)
            assertFalse(flushOptions.waitForFlush())
        }
    }

    @Test
    fun allowWriteStall() {
        FlushOptions().use { flushOptions ->
            assertFalse(flushOptions.allowWriteStall())
            flushOptions.setAllowWriteStall(true)
            assertTrue(flushOptions.allowWriteStall())
        }
    }
}
