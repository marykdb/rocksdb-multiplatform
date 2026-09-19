package maryk.rocksdb

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
            defaultEnv.setBackgroundThreads(5, Priority.BOTTOM)
            assertEquals(5, defaultEnv.getBackgroundThreads(Priority.BOTTOM))

            defaultEnv.setBackgroundThreads(5, Priority.LOW)
            assertEquals(5, defaultEnv.getBackgroundThreads(Priority.LOW))

            defaultEnv.setBackgroundThreads(5, Priority.HIGH)
            assertEquals(5, defaultEnv.getBackgroundThreads(Priority.HIGH))
        }
    }

    @Test
    fun threadPoolQueueLen() {
        getDefaultEnv().use { defaultEnv ->
            assertEquals(0, defaultEnv.getThreadPoolQueueLen(Priority.BOTTOM))
            assertEquals(0, defaultEnv.getThreadPoolQueueLen(Priority.LOW))
            assertEquals(0, defaultEnv.getThreadPoolQueueLen(Priority.HIGH))
        }
    }

    @Test
    fun incBackgroundThreadsIfNeeded() {
        getDefaultEnv().use { defaultEnv ->
            defaultEnv.incBackgroundThreadsIfNeeded(20, Priority.BOTTOM)
            assertTrue(20 <= defaultEnv.getBackgroundThreads(Priority.BOTTOM))

            defaultEnv.incBackgroundThreadsIfNeeded(20, Priority.LOW)
            assertTrue(20 <= defaultEnv.getBackgroundThreads(Priority.LOW))

            defaultEnv.incBackgroundThreadsIfNeeded(20, Priority.HIGH)
            assertTrue(20 <= defaultEnv.getBackgroundThreads(Priority.HIGH))
        }
    }

    @Test
    fun lowerThreadPoolIOPriority() {
        getDefaultEnv().use { defaultEnv ->
            defaultEnv.lowerThreadPoolIOPriority(Priority.BOTTOM)

            defaultEnv.lowerThreadPoolIOPriority(Priority.LOW)

            defaultEnv.lowerThreadPoolIOPriority(Priority.HIGH)
        }
    }

    @Test
    fun lowerThreadPoolCPUPriority() {
        getDefaultEnv().use { defaultEnv ->
            defaultEnv.lowerThreadPoolCPUPriority(Priority.BOTTOM)

            defaultEnv.lowerThreadPoolCPUPriority(Priority.LOW)

            defaultEnv.lowerThreadPoolCPUPriority(Priority.HIGH)
        }
    }
}
