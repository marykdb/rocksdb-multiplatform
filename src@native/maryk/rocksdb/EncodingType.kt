package maryk.rocksdb

import maryk.rocksdb.EncodingType.kPlain
import maryk.rocksdb.EncodingType.kPrefix

actual enum class EncodingType(
    internal val value: Byte
) {
    kPlain(0),
    kPrefix(1);
}

internal fun toEncodingType(value: Byte) = when (value) {
    0.toByte() -> kPlain
    1.toByte() -> kPrefix
    else -> throw RocksDBException("Unrecognized $value for EncodingType")
}
