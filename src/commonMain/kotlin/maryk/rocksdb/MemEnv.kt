package maryk.rocksdb

/**
 * Memory environment.
 */
expect class MemEnv
    /**
     * Creates a new environment that stores its data
     * in memory and delegates all non-file-storage tasks to
     * `baseEnv`.
     *
     * The caller must delete the result when it is
     * no longer needed.
     *
     * @param baseEnv the base environment,
     * must remain live while the result is in use.
     */
    constructor(baseEnv: Env)
: Env
