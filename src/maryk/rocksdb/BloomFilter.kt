package maryk.rocksdb

/** Bloom filter wrapper used by [BlockBasedTableConfig]. */
expect class BloomFilter : FilterPolicy {
    constructor()
    constructor(bitsPerKey: Double)
    constructor(bitsPerKey: Double, useBlockBasedBuilder: Boolean)
}
