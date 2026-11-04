@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_filterpolicy_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import rocksdb.rocksdb_filterpolicy_create_bloom_full
import rocksdb.rocksdb_filterpolicy_destroy

actual class BloomFilter actual constructor(
    private val bitsPerKey: Double
) : FilterPolicy() {
    private val native = newBloomPolicy(bitsPerKey)

    actual constructor() : this(10.0)

    // The legacy builder flag is ignored by RocksJava as well.
    actual constructor(bitsPerKey: Double, useBlockBasedBuilder: Boolean) : this(bitsPerKey)

    internal override fun createNativeCopy(): CPointer<rocksdb_filterpolicy_t> {
        checkOwningHandle()
        return newBloomPolicy(bitsPerKey)
    }

    override fun close() {
        if (tryClose()) {
            rocksdb_filterpolicy_destroy(native)
            super.close()
        }
    }
}

private fun newBloomPolicy(bitsPerKey: Double): CPointer<rocksdb_filterpolicy_t> =
    requireNotNull(rocksdb_filterpolicy_create_bloom_full(bitsPerKey)) {
        "Unable to allocate RocksDB bloom filter"
    }
