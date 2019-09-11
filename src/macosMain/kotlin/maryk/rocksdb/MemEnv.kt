package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class MemEnv actual constructor(baseEnv: Env) : Env(createMemEnv(baseEnv.nativeHandle))

fun createMemEnv(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
