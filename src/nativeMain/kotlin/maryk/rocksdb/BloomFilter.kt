@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_filterpolicy_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import rocksdb.rocksdb_filterpolicy_create_bloom
import rocksdb.rocksdb_filterpolicy_create_bloom_full
import rocksdb.rocksdb_filterpolicy_destroy

actual class BloomFilter internal constructor(
    internal val native: CPointer<rocksdb_filterpolicy_t>
) : FilterPolicy() {
    actual constructor() : this(rocksdb_filterpolicy_create_bloom(10.toDouble())!!)

    actual constructor(bitsPerKey: Double) : this(
        rocksdb_filterpolicy_create_bloom(bitsPerKey)!!
    )

    actual constructor(bitsPerKey: Double, useBlockBasedBuilder: Boolean) : this(
        // The value useBlockBasedBuilder is ignored in java implementation so ignore it here too.
        rocksdb_filterpolicy_create_bloom_full(bitsPerKey)!!
    )

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_filterpolicy_destroy(native)
            super.close()
        }
    }
}
