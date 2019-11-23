package maryk.rocksdb

actual class TimedEnv actual constructor(baseEnv: Env)
    : Env(baseEnv.native)
