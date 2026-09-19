package maryk.rocksdb

import rocksdb.rocksdb_mem_env_create

actual class MemEnv actual constructor(
    baseEnv: Env,
) : Env(
    createMemEnv(baseEnv),
) {
    private var baseEnvRef: Env? = baseEnv

    override fun close() {
        super.close()
        baseEnvRef = null
    }
}

private fun createMemEnv(baseEnv: Env) =
    baseEnv.let {
        it.checkOwningHandle()
        rocksdb_mem_env_create(it.native) ?: error("Unable to create in-memory env")
    }
