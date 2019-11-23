package maryk.rocksdb

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WriteOptionsTest {
    @BeforeTest
    fun beforeTest() {
        loadRocksDBLibrary()
    }

    @Test
    fun writeOptions() {
        WriteOptions().use { writeOptions ->
            writeOptions.setSync(true)
            assertTrue(writeOptions.sync())
            writeOptions.setSync(false)
            assertFalse(writeOptions.sync())

            writeOptions.setDisableWAL(true)
            assertTrue(writeOptions.disableWAL())
            writeOptions.setDisableWAL(false)
            assertFalse(writeOptions.disableWAL())

            writeOptions.setIgnoreMissingColumnFamilies(true)
            assertTrue(writeOptions.ignoreMissingColumnFamilies())
            writeOptions.setIgnoreMissingColumnFamilies(false)
            assertFalse(writeOptions.ignoreMissingColumnFamilies())

            writeOptions.setNoSlowdown(true)
            assertTrue(writeOptions.noSlowdown())
            writeOptions.setNoSlowdown(false)
            assertFalse(writeOptions.noSlowdown())

            writeOptions.setLowPri(true)
            assertTrue(writeOptions.lowPri())
            writeOptions.setLowPri(false)
            assertFalse(writeOptions.lowPri())
        }
    }
}
