package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun copyConstructor() {
        val origOpts = WriteOptions().apply {
            setDisableWAL(Random.nextBoolean())
            setIgnoreMissingColumnFamilies(Random.nextBoolean())
            setSync(Random.nextBoolean())
        }
        val copyOpts = WriteOptions(origOpts)
        assertEquals(origOpts.disableWAL(), copyOpts.disableWAL())
        assertEquals(origOpts.ignoreMissingColumnFamilies(), copyOpts.ignoreMissingColumnFamilies())
        assertEquals(origOpts.sync(), copyOpts.sync())

        origOpts.close()
        copyOpts.close()
    }
}
