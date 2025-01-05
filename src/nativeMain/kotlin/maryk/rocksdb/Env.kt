package maryk.rocksdb

import cnames.structs.rocksdb_env_t
import kotlinx.cinterop.CPointer
import rocksdb.rocksdb_create_default_env

actual abstract class Env
protected constructor(internal val native: CPointer<rocksdb_env_t>?)
    : RocksObject() {
    actual fun getBackgroundThreads(priority: Priority): Int =
        rocksdb.rocksdb_env_get_background_threads_with_priority(native, priority.value.toInt())

    actual fun setBackgroundThreads(number: Int, priority: Priority): Env {
        rocksdb.rocksdb_env_set_background_threads_with_priority(native, number, priority.value.toInt())
        return this
    }

    actual fun getThreadPoolQueueLen(priority: Priority): Int =
        rocksdb.rocksdb_env_get_thread_pool_queue_length(native)

    actual fun incBackgroundThreadsIfNeeded(number: Int, priority: Priority): Env {
        rocksdb.rocksdb_env_inc_background_threads_if_needed(native, number, priority.value.toInt())
        return this
    }

    actual fun lowerThreadPoolIOPriority(priority: Priority): Env {
        rocksdb.rocksdb_env_lower_with_priority_thread_pool_io_priority(native, priority.value.toInt())
        return this
    }

    actual fun lowerThreadPoolCPUPriority(priority: Priority): Env {
        rocksdb.rocksdb_env_lower_with_priority_thread_pool_cpu_priority(native, priority.value.toInt())
        return this
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb.rocksdb_env_destroy(native)
        }
        super.close()
    }
}

actual fun getDefaultEnv(): Env = RocksEnv(rocksdb_create_default_env())
