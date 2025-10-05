package maryk.rocksdb

expect class CompressionOptions() : RocksObject {
    fun setWindowBits(windowBits: Int): CompressionOptions
    fun windowBits(): Int
    fun setLevel(level: Int): CompressionOptions
    fun level(): Int
    fun setStrategy(strategy: Int): CompressionOptions
    fun strategy(): Int
    fun setMaxDictBytes(bytes: Int): CompressionOptions
    fun maxDictBytes(): Int
}
