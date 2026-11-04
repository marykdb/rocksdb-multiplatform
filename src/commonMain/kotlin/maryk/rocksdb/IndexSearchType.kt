package maryk.rocksdb

/** Search strategy within SST index blocks. Interpolation and auto require the bytewise comparator. */
expect enum class IndexSearchType {
    kBinary,
    kInterpolation,
    kAuto
}
