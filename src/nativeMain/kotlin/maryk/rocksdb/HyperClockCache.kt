@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import maryk.asSizeT
import maryk.toUByte

actual class HyperClockCache actual constructor(
    capacity: Long,
    estimatedEntryCharge: Long,
    numShardBits: Int,
    strictCapacityLimit: Boolean,
) : Cache() {
    init {
        val options = requireNotNull(rocksdb.rocksdb_hyper_clock_cache_options_create(capacity.asSizeT(), estimatedEntryCharge.asSizeT())) {
            "Unable to allocate HyperClockCache options"
        }
        try {
            rocksdb.rocksdb_hyper_clock_cache_options_set_num_shard_bits(options, numShardBits)
            rocksdb.rocksdb_hyper_clock_cache_options_set_strict_capacity_limit(options, strictCapacityLimit.toUByte())
            native = requireNotNull(rocksdb.rocksdb_cache_create_hyper_clock_opts(options)) {
                "Unable to allocate HyperClockCache"
            }
        } finally {
            rocksdb.rocksdb_hyper_clock_cache_options_destroy(options)
        }
    }
}
