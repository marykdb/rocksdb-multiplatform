package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class ClockCache
    actual constructor(capacity: Long, numShardBits: Int, strictCapacityLimit: Boolean)
: Cache(newClockCache(capacity, numShardBits, strictCapacityLimit)) {
    actual constructor(capacity: Long) : this(capacity, -1, false)

    actual constructor(capacity: Long, numShardBits: Int) : this(capacity, numShardBits, false)
}

private fun newClockCache(capacity: Long, numShardBits: Int, strictCapacityLimit: Boolean): CPointer<*> {
    TODO("not implemented")
}
