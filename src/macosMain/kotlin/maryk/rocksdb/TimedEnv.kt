package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class TimedEnv actual constructor(baseEnv: Env)
    : Env(createTimedEnv(baseEnv.nativeHandle))

private fun createTimedEnv(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
