package maryk.rocksdb

actual class Slice actual constructor(
    override var data: ByteArray
) : AbstractSlice<ByteArray>() {
    actual constructor(str: String) : this(str.encodeToByteArray())

    actual constructor(data: ByteArray, offset: Int) : this(data.copyOfRange(offset, data.size))

    override fun getData(): ByteArray {
        return data
    }

    actual override fun clear() {
        data = ByteArray(0)
    }

    actual override fun removePrefix(n: Int) {
        data = data.copyOfRange(n, data.size)
    }

    override fun size(): Int {
        return data.size.toInt()
    }

    override fun empty(): Boolean {
        return data.isEmpty()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(hex: Boolean): String {
        return if (hex) {
            data.toHexString()
        } else data.decodeToString()
    }

    override operator fun get(offset: Int): Byte {
        return data[offset]
    }
}
