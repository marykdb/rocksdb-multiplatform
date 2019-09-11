package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class FlushOptions actual constructor() : RocksObject(newFlushOptions()) {
    actual fun setWaitForFlush(waitForFlush: Boolean): FlushOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun waitForFlush(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowWriteStall(allowWriteStall: Boolean): FlushOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowWriteStall(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newFlushOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
