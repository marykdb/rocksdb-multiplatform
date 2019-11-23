package maryk.rocksdb

import rocksdb.RocksDBCache

actual class LRUCache private constructor(
    native: RocksDBCache
) : Cache(native) {
    actual constructor(capacity: Long, numShardBits: Int) : this(
        capacity,
        -1,
        false,
        0.0
    )

    actual constructor(
        capacity: Long, numShardBits: Int, strictCapacityLimit: Boolean
    ) : this(capacity, numShardBits, strictCapacityLimit, 0.0)

    actual constructor(
        capacity: Long,
        numShardBits: Int,
        strictCapacityLimit: Boolean,
        highPriPoolRatio: Double
    ) : this(
        RocksDBCache.LRUCacheWithCapacity(
            capacity.toULong(),
            numShardBits,
            strictCapacityLimit,
            highPriPoolRatio
        )
    )
}
