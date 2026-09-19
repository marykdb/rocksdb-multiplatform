package maryk.rocksdb

/**
 * Common interface for database options implementations.
 */
expect interface DBOptionsInterface<T : DBOptionsInterface<T>> {
    /**
     * Use the specified [Env] instance to interact with the environment.
     *
     * Default: [getDefaultEnv]
     *
     * @param env environment instance to use.
     * @return the current options instance.
     */
    fun setEnv(env: Env): T

    /**
     * Returns the environment configured on this options instance.
     */
    fun getEnv(): Env
}
