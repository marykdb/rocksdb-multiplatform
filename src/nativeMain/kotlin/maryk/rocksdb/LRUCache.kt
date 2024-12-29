package maryk.rocksdb

import cnames.structs.rocksdb_lru_cache_options_t
import kotlinx.cinterop.CPointer

actual class LRUCache private constructor() : Cache() {
    lateinit var options: CPointer<rocksdb_lru_cache_options_t>

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
        options = rocksdb.rocksdb_lru_cache_options_create()!!.apply {
            rocksdb.rocksdb_lru_cache_options_set_capacity(this, capacity.toULong())
            rocksdb.rocksdb_lru_cache_options_set_num_shard_bits(this, numShardBits)
        }

        native = rocksdb.rocksdb_cache_create_lru_opts(options)!!
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb.rocksdb_free(options)
        }
        super.close()
    }
}
