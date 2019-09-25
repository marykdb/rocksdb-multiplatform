package maryk.rocksdb

import cnames.structs.rocksdb_env_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import maryk.rocksdb.Priority.BOTTOM
import maryk.rocksdb.Priority.HIGH
import maryk.rocksdb.Priority.LOW
import maryk.rocksdb.Priority.TOTAL
import rocksdb.rocksdb_create_default_env
import rocksdb.rocksdb_env_set_background_threads
import rocksdb.rocksdb_env_set_high_priority_background_threads

actual abstract class Env
    protected constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle) {
    @Suppress("UNCHECKED_CAST")
    private val typedNativeHandle = nativeHandle as CValuesRef<rocksdb_env_t>

    private val backgroundThreadsMap = mutableMapOf<Priority, Int>()

    actual fun getBackgroundThreads(priority: Priority): Int = backgroundThreadsMap[priority] ?: TODO("UNKNOWN")

    actual fun setBackgroundThreads(number: Int, priority: Priority): Env {
        backgroundThreadsMap[priority] = number
        when (priority) {
            BOTTOM -> rocksdb_env_set_background_threads(typedNativeHandle, number)
            LOW -> rocksdb_env_set_background_threads(typedNativeHandle, number)
            HIGH -> rocksdb_env_set_high_priority_background_threads(typedNativeHandle, number)
            TOTAL -> TODO()
        }
        return this
    }

    actual fun getThreadPoolQueueLen(priority: Priority): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun incBackgroundThreadsIfNeeded(number: Int, priority: Priority): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun lowerThreadPoolIOPriority(priority: Priority): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun lowerThreadPoolCPUPriority(priority: Priority): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getThreadList(): List<ThreadStatus> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private val defaultEnv = RocksEnv(rocksdb_create_default_env()!!)

actual fun getDefaultEnv(): Env = defaultEnv
