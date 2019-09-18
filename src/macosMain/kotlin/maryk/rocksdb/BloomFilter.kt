package maryk.rocksdb

import rocksdb.rocksdb_filterpolicy_create_bloom
import rocksdb.rocksdb_filterpolicy_create_bloom_full

private const val DEFAULT_BITS_PER_KEY = 10
private const val DEFAULT_MODE = true

actual class BloomFilter
    actual constructor(bitsPerKey: Int, useBlockBasedMode: Boolean)
: FilterPolicy(createNewBloomFilter(bitsPerKey, useBlockBasedMode)) {
    actual constructor() : this(DEFAULT_BITS_PER_KEY, DEFAULT_MODE)

    actual constructor(bitsPerKey: Int) : this(bitsPerKey, DEFAULT_MODE)
}

private fun createNewBloomFilter(
    bitsKeyKey: Int,
    useBlockBasedMode: Boolean
) = if (useBlockBasedMode) {
    rocksdb_filterpolicy_create_bloom(bitsKeyKey)!!
} else {
    rocksdb_filterpolicy_create_bloom_full(bitsKeyKey)!!
}
