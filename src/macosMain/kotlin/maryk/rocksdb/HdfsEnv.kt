package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class HdfsEnv actual constructor(fsName: String) : Env(createHdfsEnv(fsName))

private fun createHdfsEnv(fsName: String): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
