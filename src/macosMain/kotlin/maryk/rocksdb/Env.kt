package maryk.rocksdb

import rocksdb.RocksDBEnv

actual abstract class Env
    protected constructor(internal val native: RocksDBEnv)
: RocksObject() {
    actual fun getBackgroundThreads(priority: Priority): Int = native.getBackgroundThreads(priority.value)

    actual fun setBackgroundThreads(number: Int, priority: Priority): Env {
        native.setBackgroundThreads(number, priority.value)
        return this
    }

    actual fun getThreadPoolQueueLen(priority: Priority) =
        native.getThreadPoolQueueLen(priority.value)

    actual fun incBackgroundThreadsIfNeeded(number: Int, priority: Priority): Env {
        native.incBackgroundThreadsIfNeeded(number, priority.value)
        return this
    }

    actual fun lowerThreadPoolIOPriority(priority: Priority): Env {
        native.lowerThreadPoolIOPriority(priority.value)
        return this
    }

    actual fun lowerThreadPoolCPUPriority(priority: Priority): Env {
        native.lowerThreadPoolCPUPriority(priority.value)
        return this
    }
}

actual fun getDefaultEnv(): Env = RocksEnv()
