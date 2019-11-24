package maryk.rocksdb

import rocksdb.RocksDBCompactionStyle

actual enum class CompactionStyle(
    internal val value: RocksDBCompactionStyle
) {
    LEVEL(0),
    UNIVERSAL(1),
    FIFO(2),
    NONE(3);
}

fun getCompactionStyle(value: RocksDBCompactionStyle): CompactionStyle {
    for (compactionStyle in CompactionStyle.values()) {
        if (compactionStyle.value == value) {
            return compactionStyle
        }
    }
    throw IllegalArgumentException("Illegal value provided for CompactionStyle.")
}
