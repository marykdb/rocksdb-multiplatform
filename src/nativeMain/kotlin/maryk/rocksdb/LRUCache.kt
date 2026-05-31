@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT

actual class LRUCache private constructor() : Cache() {
    actual constructor(capacity: Long) : this(capacity, -1, false, 0.0, 0.0)

    actual constructor(capacity: Long, numShardBits: Int) : this(capacity, numShardBits, false, 0.0, 0.0)

    constructor(capacity: Long, numShardBits: Int, strictCapacityLimit: Boolean) : this(capacity, numShardBits, strictCapacityLimit, 0.0, 0.0)

    constructor(
        capacity: Long,
        numShardBits: Int,
        strictCapacityLimit: Boolean,
        highPriPoolRatio: Double
    ) : this(capacity, numShardBits, strictCapacityLimit, highPriPoolRatio, 0.0)

    constructor(
        capacity: Long,
        numShardBits: Int,
        strictCapacityLimit: Boolean,
        highPriPoolRatio: Double,
        lowPriPoolRatio: Double
    ) : this() {
        if (highPriPoolRatio != 0.0 || lowPriPoolRatio != 0.0) {
            throw UnsupportedOperationException(
                "Native RocksDB C API does not expose LRU cache priority pool ratio setters"
            )
        }
        if (strictCapacityLimit) {
            if (numShardBits != -1) {
                throw UnsupportedOperationException(
                    "Native RocksDB C API only exposes strict-capacity LRU cache creation without custom shard bits"
                )
            }
            native = requireNotNull(rocksdb.rocksdb_cache_create_lru_with_strict_capacity_limit(capacity.asSizeT())) {
                "Unable to allocate strict-capacity RocksDB LRU cache"
            }
            return
        }

        val options = requireNotNull(rocksdb.rocksdb_lru_cache_options_create()) {
            "Unable to allocate RocksDB LRU cache options"
        }
        try {
            rocksdb.rocksdb_lru_cache_options_set_capacity(options, capacity.asSizeT())
            rocksdb.rocksdb_lru_cache_options_set_num_shard_bits(options, numShardBits)
            native = requireNotNull(rocksdb.rocksdb_cache_create_lru_opts(options)) {
                "Unable to allocate RocksDB LRU cache"
            }
        } finally {
            rocksdb.rocksdb_lru_cache_options_destroy(options)
        }
    }

    override fun close() {
        if (tryClose()) {
            rocksdb.rocksdb_cache_destroy(native)
        }
    }
}
