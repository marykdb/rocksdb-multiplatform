package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SstFileManagerTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun configureLimitsAndRates() {
        val maxSpace = 64L * 1024 * 1024
        val bufferSize = 10L * 1024 * 1024
        val deleteRate = 52L * 1024 * 1024
        val trashRatio = 0.2

        getDefaultEnv().use { env ->
            SstFileManager(env).use { manager ->
                assertEquals(0, manager.getDeleteRateBytesPerSecond())
                manager.setMaxAllowedSpaceUsage(maxSpace)
                manager.setCompactionBufferSize(bufferSize)
                manager.setDeleteRateBytesPerSecond(deleteRate)
                manager.setMaxTrashDBRatio(trashRatio)

                assertFalse(manager.isMaxAllowedSpaceReached())
                assertFalse(manager.isMaxAllowedSpaceReachedIncludingCompactions())
                assertEquals(deleteRate, manager.getDeleteRateBytesPerSecond())
                assertEquals(trashRatio, manager.getMaxTrashDBRatio(), 1e-6)
            }
        }
    }

    @Test
    fun totalSizeStartsAtZero() {
        getDefaultEnv().use { env ->
            SstFileManager(env).use { manager ->
                assertEquals(0, manager.getTotalSize())
                assertEquals(0.25, manager.getMaxTrashDBRatio(), 1e-6)
            }
        }
    }
}
