package maryk.rocksdb

import maryk.rocksdb.Priority.BOTTOM
import maryk.rocksdb.Priority.HIGH
import maryk.rocksdb.Priority.LOW
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultEnvTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun backgroundThreads() {
        getDefaultEnv().use { defaultEnv ->
            defaultEnv.setBackgroundThreads(5, BOTTOM)
            assertEquals(5, defaultEnv.getBackgroundThreads(BOTTOM))

            defaultEnv.setBackgroundThreads(5)
            assertEquals(5, defaultEnv.getBackgroundThreads(LOW))

            defaultEnv.setBackgroundThreads(5, LOW)
            assertEquals(5, defaultEnv.getBackgroundThreads(LOW))

            defaultEnv.setBackgroundThreads(5, HIGH)
            assertEquals(5, defaultEnv.getBackgroundThreads(HIGH))
        }
    }

    @Test
    fun threadPoolQueueLen() {
        getDefaultEnv().use { defaultEnv ->
            assertEquals(0, defaultEnv.getThreadPoolQueueLen(BOTTOM))
            assertEquals(0, defaultEnv.getThreadPoolQueueLen(LOW))
            assertEquals(0, defaultEnv.getThreadPoolQueueLen(HIGH))
        }
    }

    @Test
    fun incBackgroundThreadsIfNeeded() {
        getDefaultEnv().use { defaultEnv ->
            defaultEnv.incBackgroundThreadsIfNeeded(20, BOTTOM)
            assertTrue(20 <= defaultEnv.getBackgroundThreads(BOTTOM))

            defaultEnv.incBackgroundThreadsIfNeeded(20, LOW)
            assertTrue(20 <= defaultEnv.getBackgroundThreads(LOW))

            defaultEnv.incBackgroundThreadsIfNeeded(20, HIGH)
            assertTrue(20 <= defaultEnv.getBackgroundThreads(HIGH))
        }
    }

    @Test
    fun lowerThreadPoolIOPriority() {
        getDefaultEnv().use { defaultEnv ->
            defaultEnv.lowerThreadPoolIOPriority(BOTTOM)

            defaultEnv.lowerThreadPoolIOPriority(LOW)

            defaultEnv.lowerThreadPoolIOPriority(HIGH)
        }
    }

    @Test
    fun lowerThreadPoolCPUPriority() {
        getDefaultEnv().use { defaultEnv ->
            defaultEnv.lowerThreadPoolCPUPriority(BOTTOM)

            defaultEnv.lowerThreadPoolCPUPriority(LOW)

            defaultEnv.lowerThreadPoolCPUPriority(HIGH)
        }
    }
}
