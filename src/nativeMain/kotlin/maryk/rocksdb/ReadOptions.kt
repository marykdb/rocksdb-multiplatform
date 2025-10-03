@file:OptIn(ExperimentalNativeApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_readoptions_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import maryk.ByteBuffer
import maryk.toBoolean
import maryk.toUByte
import rocksdb.rocksdb_readoptions_create
import rocksdb.rocksdb_readoptions_destroy
import rocksdb.rocksdb_readoptions_get_fill_cache
import rocksdb.rocksdb_readoptions_get_prefix_same_as_start
import rocksdb.rocksdb_readoptions_get_read_tier
import rocksdb.rocksdb_readoptions_get_tailing
import rocksdb.rocksdb_readoptions_get_verify_checksums
import rocksdb.rocksdb_readoptions_set_fill_cache
import rocksdb.rocksdb_readoptions_set_prefix_same_as_start
import rocksdb.rocksdb_readoptions_set_read_tier
import rocksdb.rocksdb_readoptions_set_snapshot
import rocksdb.rocksdb_readoptions_set_tailing
import rocksdb.rocksdb_readoptions_set_verify_checksums
import rocksdb.rocksdb_readoptions_set_iterate_upper_bound
import rocksdb.rocksdb_readoptions_set_iterate_lower_bound
import kotlin.experimental.ExperimentalNativeApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

actual class ReadOptions private constructor(val native: CPointer<rocksdb_readoptions_t>?) : RocksObject() {
    private var snapshot: Snapshot? = null
    private var upperBound: BoundBuffer? = null
    private var lowerBound: BoundBuffer? = null

    actual constructor() : this(rocksdb_readoptions_create())

    override fun close() {
        if (isOwningHandle()) {
            upperBound?.free()
            upperBound = null
            lowerBound?.free()
            lowerBound = null
            rocksdb_readoptions_destroy(native)
            super.close()
        }
    }

    actual fun verifyChecksums(): Boolean {
        assert(isOwningHandle())
        return rocksdb_readoptions_get_verify_checksums(native).toBoolean()
    }

    actual fun setVerifyChecksums(verifyChecksums: Boolean): ReadOptions {
        assert(isOwningHandle())
        rocksdb_readoptions_set_verify_checksums(native, verifyChecksums.toUByte())
        return this
    }

    actual fun fillCache(): Boolean {
        assert(isOwningHandle())
        return rocksdb_readoptions_get_fill_cache(native).toBoolean()
    }

    actual fun setFillCache(fillCache: Boolean): ReadOptions {
        assert(isOwningHandle())
        rocksdb_readoptions_set_fill_cache(native, fillCache.toUByte())
        return this
    }

    actual fun prefixSameAsStart(): Boolean {
        assert(isOwningHandle())
        return rocksdb_readoptions_get_prefix_same_as_start(native).toBoolean()
    }

    actual fun setPrefixSameAsStart(prefixSameAsStart: Boolean): ReadOptions {
        assert(isOwningHandle())
        rocksdb_readoptions_set_prefix_same_as_start(native, prefixSameAsStart.toUByte())
        return this;
    }

    actual fun snapshot(): Snapshot? = snapshot

    actual fun setSnapshot(snapshot: Snapshot?): ReadOptions {
        assert(isOwningHandle())
        rocksdb_readoptions_set_snapshot(native, snapshot?.native)
        this.snapshot = snapshot
        return this
    }

    actual fun iterateUpperBound(): Slice? = upperBound?.toSlice()

    actual fun setIterateUpperBound(iterateUpperBound: AbstractSlice<*>): ReadOptions {
        assert(isOwningHandle())
        upperBound = updateBound(upperBound, iterateUpperBound) { pointer, length ->
            rocksdb_readoptions_set_iterate_upper_bound(native, pointer, length)
        }
        return this
    }

    actual fun iterateLowerBound(): Slice? = lowerBound?.toSlice()

    actual fun setIterateLowerBound(iterateLowerBound: AbstractSlice<*>): ReadOptions {
        assert(isOwningHandle())
        lowerBound = updateBound(lowerBound, iterateLowerBound) { pointer, length ->
            rocksdb_readoptions_set_iterate_lower_bound(native, pointer, length)
        }
        return this
    }

    actual fun readTier(): ReadTier {
        assert(isOwningHandle())
        val tier = rocksdb_readoptions_get_read_tier(native).toByte()
        return when (tier) {
            ReadTier.READ_ALL_TIER.getValue() -> ReadTier.READ_ALL_TIER
            ReadTier.BLOCK_CACHE_TIER.getValue() -> ReadTier.BLOCK_CACHE_TIER
            ReadTier.PERSISTED_TIER.getValue() -> ReadTier.PERSISTED_TIER
            ReadTier.MEMTABLE_TIER.getValue() -> ReadTier.MEMTABLE_TIER
            else -> ReadTier.READ_ALL_TIER
        }
    }

    actual fun setReadTier(readTier: ReadTier): ReadOptions {
        assert(isOwningHandle())
        rocksdb_readoptions_set_read_tier(native, readTier.getValue().toInt())
        return this
    }

    actual fun tailing(): Boolean {
        assert(isOwningHandle())
        return rocksdb_readoptions_get_tailing(native).toBoolean()
    }

    actual fun setTailing(tailing: Boolean): ReadOptions {
        assert(isOwningHandle())
        rocksdb_readoptions_set_tailing(native, tailing.toUByte())
        return this
    }
}

private data class BoundBuffer(
    val pointer: CPointer<ByteVar>?,
    val bytes: ByteArray
) {
    fun free() {
        pointer?.let { nativeHeap.free(it.rawValue) }
    }

    fun toSlice(): Slice = Slice(bytes.copyOf())
}

private inline fun updateBound(
    current: BoundBuffer?,
    slice: AbstractSlice<*>,
    setter: (CPointer<ByteVar>?, ULong) -> Unit
): BoundBuffer {
    current?.free()

    val bytes = slice.copyBytes()
    val buffer = nativeHeap.allocArray<ByteVar>(bytes.size)
    bytes.usePinned { pinned ->
        memcpy(buffer, pinned.addressOf(0), bytes.size.toULong())
    }
    setter(buffer, bytes.size.toULong())
    return BoundBuffer(buffer, bytes)
}

private fun AbstractSlice<*>.copyBytes(): ByteArray = when (this) {
    is Slice -> data.copyOf()
    is DirectSlice -> data.array()
    else -> when (val raw = data()) {
        is ByteArray -> raw.copyOf()
        is ByteBuffer -> raw.array()
        else -> error("Unsupported slice type: ${this::class.simpleName}")
    }
}
