package maryk.rocksdb

actual class WriteEntry actual constructor(
    type: WriteType,
    key: DirectSlice,
    value: DirectSlice
) : AutoCloseable {
    actual fun getType(): WriteType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getKey(): DirectSlice {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getValue(): DirectSlice? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
