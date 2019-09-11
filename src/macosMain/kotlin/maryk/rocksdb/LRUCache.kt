package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class LRUCache private constructor(nativeHandle: CPointer<*>) : Cache(nativeHandle) {
    actual constructor(capacity: Long, numShardBits: Int) : this(capacity, -1, false, 0.0)

    actual constructor(
        capacity: Long, numShardBits: Int, strictCapacityLimit: Boolean
    ) : this(newLRUCache(capacity, numShardBits, strictCapacityLimit, 0.0))

    actual constructor(
        capacity: Long,
        numShardBits: Int,
        strictCapacityLimit: Boolean,
        highPriPoolRatio: Double
    ) : this(newLRUCache(capacity, numShardBits, strictCapacityLimit, highPriPoolRatio))
}

private fun newLRUCache(
    capacity: Long,
    numShardBits: Int, strictCapacityLimit: Boolean,
    highPriPoolRatio: Double
): CPointer<*> {
    TODO("implement")
}
