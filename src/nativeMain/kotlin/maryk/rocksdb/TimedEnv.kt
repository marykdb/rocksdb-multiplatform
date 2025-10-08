package maryk.rocksdb

import rocksdb.rocksdb_timed_env_create

actual class TimedEnv actual constructor(baseEnv: Env) : Env(
    rocksdb_timed_env_create(baseEnv.native) ?: error("Unable to create timed env"),
)
