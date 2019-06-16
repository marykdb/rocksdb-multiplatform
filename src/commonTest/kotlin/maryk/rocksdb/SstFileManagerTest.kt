package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SstFileManagerTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun maxAllowedSpaceUsage() {
        SstFileManager(getDefaultEnv()).use { sstFileManager ->
            sstFileManager.setMaxAllowedSpaceUsage((1024 * 1024 * 64).toLong())
            assertFalse(sstFileManager.isMaxAllowedSpaceReached())
            assertFalse(sstFileManager.isMaxAllowedSpaceReachedIncludingCompactions())
        }
    }

    @Test
    fun compactionBufferSize() {
        SstFileManager(getDefaultEnv()).use { sstFileManager ->
            sstFileManager.setCompactionBufferSize((1024 * 1024 * 10).toLong())
            assertFalse(sstFileManager.isMaxAllowedSpaceReachedIncludingCompactions())
        }
    }

    @Test
    fun totalSize() {
        SstFileManager(getDefaultEnv()).use { sstFileManager ->
            assertEquals(0, sstFileManager.getTotalSize())
        }
    }

    @Test
    fun trackedFiles() {
        SstFileManager(getDefaultEnv()).use { sstFileManager ->
            assertEquals(emptyMap(), sstFileManager.getTrackedFiles())
        }
    }

    @Test
    fun deleteRateBytesPerSecond() {
        SstFileManager(getDefaultEnv()).use { sstFileManager ->
            assertEquals(SST_FILE_MANAGER_RATE_BYTES_PER_SEC_DEFAULT, sstFileManager.getDeleteRateBytesPerSecond())
            val ratePerSecond = (1024 * 1024 * 52).toLong()
            sstFileManager.setDeleteRateBytesPerSecond(ratePerSecond)
            assertEquals(ratePerSecond, sstFileManager.getDeleteRateBytesPerSecond())
        }
    }

    @Test
    fun maxTrashDBRatio() {
        SstFileManager(getDefaultEnv()).use { sstFileManager ->
            assertEquals(SST_FILE_MANAGER_MAX_TRASH_DB_RATION_DEFAULT, sstFileManager.getMaxTrashDBRatio())
            val trashRatio = 0.2
            sstFileManager.setMaxTrashDBRatio(trashRatio)
            assertEquals(trashRatio, sstFileManager.getMaxTrashDBRatio())
        }
    }
}
