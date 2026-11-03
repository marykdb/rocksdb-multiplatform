@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_concurrent_task_limiter_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString

actual abstract class ConcurrentTaskLimiter internal constructor(
    internal val native: CPointer<rocksdb_concurrent_task_limiter_t>,
) : RocksObject() {
    actual abstract fun name(): String
    actual abstract fun setMaxOutstandingTask(maxOutstandingTask: Int): ConcurrentTaskLimiter
    actual abstract fun resetMaxOutstandingTask(): ConcurrentTaskLimiter
    actual abstract fun outstandingTask(): Int

    override fun close() {
        if (tryClose()) {
            rocksdb.rocksdb_concurrent_task_limiter_destroy(native)
            super.close()
        }
    }
}

actual class ConcurrentTaskLimiterImpl private constructor(
    native: CPointer<rocksdb_concurrent_task_limiter_t>,
) : ConcurrentTaskLimiter(native) {
    actual constructor(name: String, maxOutstandingTask: Int) : this(createTaskLimiter(name, maxOutstandingTask))

    actual override fun name(): String {
        checkOwningHandle()
        return requireNotNull(rocksdb.rocksdb_concurrent_task_limiter_name(native)).toKString()
    }
    actual override fun setMaxOutstandingTask(maxOutstandingTask: Int): ConcurrentTaskLimiter {
        checkOwningHandle()
        rocksdb.rocksdb_concurrent_task_limiter_set_max_outstanding_task(native, maxOutstandingTask)
        return this
    }
    actual override fun resetMaxOutstandingTask(): ConcurrentTaskLimiter {
        checkOwningHandle()
        rocksdb.rocksdb_concurrent_task_limiter_reset_max_outstanding_task(native)
        return this
    }
    actual override fun outstandingTask(): Int {
        checkOwningHandle()
        return rocksdb.rocksdb_concurrent_task_limiter_outstanding_task(native)
    }
}

private fun createTaskLimiter(name: String, maxOutstandingTask: Int): CPointer<rocksdb_concurrent_task_limiter_t> {
    require('\u0000' !in name) { "Limiter name must not contain NUL" }
    return requireNotNull(rocksdb.rocksdb_concurrent_task_limiter_create(name, maxOutstandingTask)) {
        "Unable to allocate concurrent task limiter"
    }
}
