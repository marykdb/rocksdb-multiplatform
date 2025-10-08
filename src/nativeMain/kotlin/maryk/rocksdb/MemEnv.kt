package maryk.rocksdb

import rocksdb.rocksdb_mem_env_create

actual class MemEnv actual constructor(baseEnv: Env) : Env(
    rocksdb_mem_env_create(baseEnv.native) ?: error("Unable to create in-memory env"),
)
