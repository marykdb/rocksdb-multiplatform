package maryk.rocksdb

/** Timed environment. */
expect class TimedEnv : Env {

    /**
     * Creates a new environment that measures function call times for
     * filesystem operations, reporting results to variables in PerfContext.
     *
     * The caller must delete the result when it is
     * no longer needed.
     *
     * @param baseEnv the base environment,
     * must remain live while the result is in use.
     */
    constructor(baseEnv: Env)
}
