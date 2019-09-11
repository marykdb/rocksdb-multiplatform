package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class SstFileWriter
    actual constructor(envOptions: EnvOptions, options: Options)
: RocksObject(
    newSstFileWriter(
        envOptions.nativeHandle, options.nativeHandle
    )
) {
    actual fun open(filePath: String) {
    }

    actual fun put(key: Slice, value: Slice) {
    }

    actual fun put(key: DirectSlice, value: DirectSlice) {
    }

    actual fun put(key: ByteArray, value: ByteArray) {
    }

    actual fun merge(key: Slice, value: Slice) {
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
    }

    actual fun merge(key: DirectSlice, value: DirectSlice) {
    }

    actual fun delete(key: Slice) {
    }

    actual fun delete(key: DirectSlice) {
    }

    actual fun delete(key: ByteArray) {
    }

    actual fun finish() {
    }
}

fun newSstFileWriter(nativeHandle: CPointer<*>, nativeHandle1: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
