package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class WriteOptions : RocksObject {
    actual constructor() : super(newWriteOptions())

    actual constructor(other: WriteOptions) : super(copyWriteOptions(other.nativeHandle))

    constructor(nativeHandle: CPointer<*>) : super(nativeHandle) {
        disOwnNativeHandle()
    }

    actual fun setSync(flag: Boolean): WriteOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun sync(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDisableWAL(flag: Boolean): WriteOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun disableWAL(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIgnoreMissingColumnFamilies(ignoreMissingColumnFamilies: Boolean): WriteOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ignoreMissingColumnFamilies(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setNoSlowdown(noSlowdown: Boolean): WriteOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun noSlowdown(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLowPri(lowPri: Boolean): WriteOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun lowPri(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun newWriteOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun copyWriteOptions(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
