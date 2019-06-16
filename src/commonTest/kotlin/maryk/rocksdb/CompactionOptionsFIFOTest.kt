package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class CompactionOptionsFIFOTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun maxTableFilesSize() {
        val size = (500 * 1024 * 1026).toLong()
        CompactionOptionsFIFO().use { opt ->
            opt.setMaxTableFilesSize(size)
            assertEquals(size, opt.maxTableFilesSize())
        }
    }

    @Test
    fun allowCompaction() {
        val allowCompaction = true
        CompactionOptionsFIFO().use { opt ->
            opt.setAllowCompaction(allowCompaction)
            assertEquals(allowCompaction, opt.allowCompaction())
        }
    }
}
