package maryk.rocksdb

/**
 * Base class for all Env implementations in RocksDB.
 */
expect abstract class Env : RocksObject {
    /**
     * Gets the number of background worker threads of the pool
     * for this environment.
     *
     * @return the number of threads.
     */
    fun getBackgroundThreads(priority: Priority): Int

    /**
     * Sets the number of background worker threads of the specified thread
     * pool for this environment.
     *
     * @param number the number of threads
     * @param priority the priority id of a specified thread pool.
     *
     * Default number: 1
     * @return current [RocksEnv] instance.
     */
    fun setBackgroundThreads(number: Int, priority: Priority = Priority.LOW): Env

    /**
     *
     * Returns the length of the queue associated with the specified
     * thread pool.
     *
     * @param priority the priority id of a specified thread pool.
     *
     * @return the thread pool queue length.
     */
    fun getThreadPoolQueueLen(priority: Priority): Int

    /**
     * Enlarge number of background worker threads of a specific thread pool
     * for this environment if it is smaller than specified. 'LOW' is the default
     * pool.
     *
     * @param number the number of threads.
     *
     * @return current [RocksEnv] instance.
     */
    fun incBackgroundThreadsIfNeeded(
        number: Int,
        priority: Priority
    ): Env

    /**
     * Lower IO priority for threads from the specified pool.
     *
     * @param priority the priority id of a specified thread pool.
     */
    fun lowerThreadPoolIOPriority(priority: Priority): Env

    /**
     * Lower CPU priority for threads from the specified pool.
     *
     * @param priority the priority id of a specified thread pool.
     */
    fun lowerThreadPoolCPUPriority(priority: Priority): Env

    /**
     * Returns the status of all threads that belong to the current Env.
     *
     * @return the status of all threads belong to this env.
     */
    fun getThreadList(): List<ThreadStatus>
}

/**
 * Returns the default environment suitable for the current operating
 * system.
 *
 * The result of `getDefault()` is a singleton whose ownership
 * belongs to rocksdb c++.  As a result, the returned RocksEnv will not
 * have the ownership of its c++ resource, and calling its dispose()/close()
 * will be no-op.
 *
 * @return the default [maryk.rocksdb.RocksEnv] instance.
 */
expect fun getDefaultEnv(): Env
