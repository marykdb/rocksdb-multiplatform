package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImportColumnFamilyOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun moveFilesFlag() {
        ImportColumnFamilyOptions().use { options ->
            assertFalse(options.moveFiles())

            options.setMoveFiles(true)
            assertTrue(options.moveFiles())
        }
    }
}
