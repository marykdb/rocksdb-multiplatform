package maryk.rocksdb

/** Least Recently Used Cache */
expect class LRUCache : Cache {
    /**
     * Create a new cache with a fixed size capacity
     *
     * @param capacity The fixed size capacity of the cache
     */
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
    constructor(
        capacity: Long,
        numShardBits: Int,
        strictCapacityLimit: Boolean
    )

    constructor(
        capacity: Long,
        numShardBits: Int,
        strictCapacityLimit: Boolean,
        highPriPoolRatio: Double
    )

    constructor(
        capacity: Long,
        numShardBits: Int,
        strictCapacityLimit: Boolean,
        highPriPoolRatio: Double,
        lowPriPoolRatio: Double
    )
}
