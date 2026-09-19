package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompactionOptionsFIFOTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun maxTableFilesSize() {
        val size = 500L * 1024 * 1026
        CompactionOptionsFIFO().use { options ->
            options.setMaxTableFilesSize(size)
            assertEquals(size, options.maxTableFilesSize())
        }
    }

    @Test
    fun allowCompactionFlag() {
        CompactionOptionsFIFO().use { options ->
            assertFalse(options.allowCompaction())

            options.setAllowCompaction(true)
            assertTrue(options.allowCompaction())
        }
    }
}
