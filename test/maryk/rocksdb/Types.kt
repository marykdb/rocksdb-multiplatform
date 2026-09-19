package maryk.rocksdb

/** Convert first 4 bytes of a [data] to an int */
fun byteToInt(data: ByteArray) =
    data[0].toInt() and 0xff or
    (data[1].toInt() and 0xff shl 8) or
    (data[2].toInt() and 0xff shl 16) or
    (data[3].toInt() and 0xff shl 24)

/** Convert an int in [v] to byte array with 4 bytes */
fun intToByte(v: Int) = byteArrayOf(
    (v.ushr(0) and 0xff).toByte(),
    (v.ushr(8) and 0xff).toByte(),
    (v.ushr(16) and 0xff).toByte(),
    (v.ushr(24) and 0xff).toByte()
)
