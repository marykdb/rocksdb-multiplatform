package maryk.rocksdb

expect class CompactionOptions() : RocksObject {
    fun compression(): CompressionType
    fun setCompression(compression: CompressionType): CompactionOptions
    fun outputFileSizeLimit(): Long
    fun setOutputFileSizeLimit(limit: Long): CompactionOptions
    fun maxSubcompactions(): Int
    fun setMaxSubcompactions(count: Int): CompactionOptions
}

expect class CompactionOptionsFIFO() : RocksObject {
    fun setMaxTableFilesSize(size: Long): CompactionOptionsFIFO
    fun maxTableFilesSize(): Long
    fun setAllowCompaction(allow: Boolean): CompactionOptionsFIFO
    fun allowCompaction(): Boolean
    /** Combined SST and blob-file limit; zero retains the SST-only limit. */
    fun setMaxDataFilesSize(size: Long): CompactionOptionsFIFO
    fun maxDataFilesSize(): Long
    fun setUseKvRatioCompaction(useKvRatioCompaction: Boolean): CompactionOptionsFIFO
    fun useKvRatioCompaction(): Boolean
}

expect class CompactionOptionsUniversal() : RocksObject {
    fun setSizeRatio(sizeRatio: Int): CompactionOptionsUniversal
    fun sizeRatio(): Int
    fun setMinMergeWidth(width: Int): CompactionOptionsUniversal
    fun minMergeWidth(): Int
    fun setMaxMergeWidth(width: Int): CompactionOptionsUniversal
    fun maxMergeWidth(): Int
    fun setMaxSizeAmplificationPercent(percent: Int): CompactionOptionsUniversal
    fun maxSizeAmplificationPercent(): Int
    fun setCompressionSizePercent(percent: Int): CompactionOptionsUniversal
    fun compressionSizePercent(): Int
    fun setStopStyle(stopStyle: CompactionStopStyle): CompactionOptionsUniversal
    fun stopStyle(): CompactionStopStyle
    fun setAllowTrivialMove(allowTrivialMove: Boolean): CompactionOptionsUniversal
    fun allowTrivialMove(): Boolean
}
