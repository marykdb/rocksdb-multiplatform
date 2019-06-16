package maryk.rocksdb

/**
 * Similar to [LRUCache], but based on the CLOCK algorithm with
 * better concurrent performance in some cases
 */
expect class ClockCache : Cache {

    /** Create a new cache with a fixed size [capacity]. */
    constructor(capacity: Long)

    /**
     * Create a new cache with a fixed size capacity. The cache is sharded
     * to 2^numShardBits shards, by hash of the key. The total capacity
     * is divided and evenly assigned to each shard.
     * numShardBits = -1 means it is automatically determined: every shard
     * will be at least 512KB and number of shard bits will not exceed 6.
     *
     * @param capacity The fixed size capacity of the cache
     * @param numShardBits The cache is sharded to 2^numShardBits shards,
     * by hash of the key
     */
    constructor(capacity: Long, numShardBits: Int)

    /**
     * Create a new cache with a fixed size capacity. The cache is sharded
     * to 2^numShardBits shards, by hash of the key. The total capacity
     * is divided and evenly assigned to each shard. If strictCapacityLimit
     * is set, insert to the cache will fail when cache is full.
     * numShardBits = -1 means it is automatically determined: every shard
     * will be at least 512KB and number of shard bits will not exceed 6.
     *
     * @param capacity The fixed size capacity of the cache
     * @param numShardBits The cache is sharded to 2^numShardBits shards,
     * by hash of the key
     * @param strictCapacityLimit insert to the cache will fail when cache is full
     */
    constructor(
        capacity: Long, numShardBits: Int,
        strictCapacityLimit: Boolean
    )
}
