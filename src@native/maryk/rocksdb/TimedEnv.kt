package maryk.rocksdb

import rocksdb.rocksdb_timed_env_create

actual class TimedEnv actual constructor(
    baseEnv: Env,
) : Env(
    createTimedEnv(baseEnv),
) {
    private var baseEnvRef: Env? = baseEnv

    override fun close() {
        super.close()
        baseEnvRef = null
    }
}

private fun createTimedEnv(baseEnv: Env) =
    baseEnv.let {
        it.checkOwningHandle()
        rocksdb_timed_env_create(it.native) ?: error("Unable to create timed env")
    }
