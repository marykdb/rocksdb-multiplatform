package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class CompressionOptions actual constructor() : RocksObject(newCompressionOptions()) {
    actual fun setWindowBits(windowBits: Int): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun windowBits(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel(level: Int): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStrategy(strategy: Int): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun strategy(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxDictBytes(maxDictBytes: Int): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxDictBytes(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setZStdMaxTrainBytes(zstdMaxTrainBytes: Int): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun zstdMaxTrainBytes(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnabled(enabled: Boolean): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun enabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newCompressionOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
