package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ComparatorOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun comparatorOptions() {
        ComparatorOptions().use { options ->
            assertNotNull(options)
            // UseAdaptiveMutex test
            options.setUseAdaptiveMutex(true)
            assertTrue(options.useAdaptiveMutex())

            options.setUseAdaptiveMutex(false)
            assertFalse(options.useAdaptiveMutex())
        }
    }
}
