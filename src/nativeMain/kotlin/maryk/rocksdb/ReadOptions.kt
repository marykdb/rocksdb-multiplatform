@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_readoptions_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import maryk.asUInt64
import maryk.sizeTToLong
import maryk.toCheckedLong
import rocksdb.rocksdb_readoptions_get_readahead_size
import rocksdb.rocksdb_readoptions_set_readahead_size
import rocksdb.rocksdb_readoptions_get_auto_prefix_mode
import rocksdb.rocksdb_readoptions_set_auto_prefix_mode
import rocksdb.rocksdb_readoptions_get_total_order_seek
import rocksdb.rocksdb_readoptions_set_total_order_seek
import rocksdb.rocksdb_readoptions_get_async_io
import rocksdb.rocksdb_readoptions_set_async_io
import rocksdb.rocksdb_readoptions_get_deadline
import rocksdb.rocksdb_readoptions_set_deadline
import rocksdb.rocksdb_readoptions_get_io_timeout
import rocksdb.rocksdb_readoptions_set_io_timeout
import rocksdb.rocksdb_readoptions_get_background_purge_on_iterator_cleanup
import rocksdb.rocksdb_readoptions_set_background_purge_on_iterator_cleanup
import rocksdb.rocksdb_readoptions_get_ignore_range_deletions
import rocksdb.rocksdb_readoptions_set_ignore_range_deletions
import rocksdb.rocksdb_readoptions_get_max_skippable_internal_keys
import rocksdb.rocksdb_readoptions_set_max_skippable_internal_keys
import rocksdb.rocksdb_readoptions_get_pin_data
import rocksdb.rocksdb_readoptions_set_pin_data
import rocksdb.rocksdb_readoptions_get_value_size_soft_limit
import rocksdb.rocksdb_readoptions_set_value_size_soft_limit
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
import rocksdb.rocksdb_readoptions_set_table_filter
import kotlin.experimental.ExperimentalNativeApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.usePinned
import maryk.asSizeT
import platform.posix.memcpy
import platform.posix.size_t

actual class ReadOptions private constructor(val native: CPointer<rocksdb_readoptions_t>) : RocksObject() {
    private var snapshot: Snapshot? = null
    private var upperBound: BoundBuffer? = null
    private var lowerBound: BoundBuffer? = null
    internal var tableFilterRef: AbstractTableFilter? = null

    actual constructor() : this(
        requireNotNull(rocksdb_readoptions_create()) {
            "Unable to allocate RocksDB read options"
        }
    )

    override fun close() {
        if (tryClose()) {
            upperBound?.free()
            upperBound = null
            lowerBound?.free()
            lowerBound = null
            tableFilterRef?.closeFromReadOptions()
            tableFilterRef = null
            snapshot = null
            rocksdb_readoptions_destroy(native)
            super.close()
        }
    }

    actual fun verifyChecksums(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_verify_checksums(native).toBoolean()
    }

    actual fun setVerifyChecksums(verifyChecksums: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_verify_checksums(native, verifyChecksums.toUByte())
        return this
    }

    actual fun fillCache(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_fill_cache(native).toBoolean()
    }

    actual fun setFillCache(fillCache: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_fill_cache(native, fillCache.toUByte())
        return this
    }

    actual fun prefixSameAsStart(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_prefix_same_as_start(native).toBoolean()
    }

    actual fun setPrefixSameAsStart(prefixSameAsStart: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_prefix_same_as_start(native, prefixSameAsStart.toUByte())
        return this
    }

    actual fun readaheadSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_readoptions_get_readahead_size(native), "read options readahead size")
    }

    actual fun setReadaheadSize(readaheadSize: Long): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_readahead_size(native, readaheadSize.asSizeT())
        return this
    }

    actual fun autoPrefixMode(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_auto_prefix_mode(native).toBoolean()
    }

    actual fun setAutoPrefixMode(mode: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_auto_prefix_mode(native, mode.toUByte())
        return this
    }

    actual fun totalOrderSeek(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_total_order_seek(native).toBoolean()
    }

    actual fun setTotalOrderSeek(totalOrderSeek: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_total_order_seek(native, totalOrderSeek.toUByte())
        return this
    }

    actual fun asyncIo(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_async_io(native).toBoolean()
    }

    actual fun setAsyncIo(asyncIo: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_async_io(native, asyncIo.toUByte())
        return this
    }

    actual fun deadline(): Long {
        checkOwningHandle()
        return rocksdb_readoptions_get_deadline(native).toCheckedLong("read options deadline")
    }

    actual fun setDeadline(deadlineTime: Long): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_deadline(native, deadlineTime.asUInt64())
        return this
    }

    actual fun ioTimeout(): Long {
        checkOwningHandle()
        return rocksdb_readoptions_get_io_timeout(native).toCheckedLong("read options ioTimeout")
    }

    actual fun setIoTimeout(ioTimeout: Long): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_io_timeout(native, ioTimeout.asUInt64())
        return this
    }

    actual fun backgroundPurgeOnIteratorCleanup(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_background_purge_on_iterator_cleanup(native).toBoolean()
    }

    actual fun setBackgroundPurgeOnIteratorCleanup(backgroundPurgeOnIteratorCleanup: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_background_purge_on_iterator_cleanup(native, backgroundPurgeOnIteratorCleanup.toUByte())
        return this
    }

    actual fun ignoreRangeDeletions(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_ignore_range_deletions(native).toBoolean()
    }

    actual fun setIgnoreRangeDeletions(ignoreRangeDeletions: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_ignore_range_deletions(native, ignoreRangeDeletions.toUByte())
        return this
    }

    actual fun maxSkippableInternalKeys(): Long {
        checkOwningHandle()
        return rocksdb_readoptions_get_max_skippable_internal_keys(native).toCheckedLong("read options maxSkippableInternalKeys")
    }

    actual fun setMaxSkippableInternalKeys(maxSkippableInternalKeys: Long): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_max_skippable_internal_keys(native, maxSkippableInternalKeys.asUInt64())
        return this
    }

    actual fun pinData(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_pin_data(native).toBoolean()
    }

    actual fun setPinData(pinData: Boolean): ReadOptions {
        checkOwningHandle()
        rocksdb_readoptions_set_pin_data(native, pinData.toUByte())
        return this
    }

    actual fun valueSizeSoftLimit(): Long {
        checkOwningHandle()
        val value = rocksdb_readoptions_get_value_size_soft_limit(native)
        return if (value == ULong.MAX_VALUE) -1L else value.toCheckedLong("read options value size soft limit")
    }

    actual fun setValueSizeSoftLimit(valueSizeSoftLimit: Long): ReadOptions {
        checkOwningHandle()
        require(valueSizeSoftLimit >= 0 || valueSizeSoftLimit == -1L) { "Value size soft limit must be non-negative or -1" }
        rocksdb_readoptions_set_value_size_soft_limit(native, if (valueSizeSoftLimit == -1L) ULong.MAX_VALUE else valueSizeSoftLimit.asUInt64())
        return this
    }

    actual fun snapshot(): Snapshot? = snapshot

    internal fun checkOpenForRead() {
        checkOwningHandle()
        snapshot?.checkOwningHandle()
    }

    actual fun setSnapshot(snapshot: Snapshot?): ReadOptions {
        checkOwningHandle()
        snapshot?.checkOwningHandle()
        rocksdb_readoptions_set_snapshot(native, snapshot?.native)
        this.snapshot = snapshot
        return this
    }

    actual fun iterateUpperBound(): Slice? = upperBound?.toSlice()

    actual fun setIterateUpperBound(iterateUpperBound: AbstractSlice<*>): ReadOptions {
        checkOwningHandle()
        upperBound = updateBound(upperBound, iterateUpperBound) { pointer, length ->
            rocksdb_readoptions_set_iterate_upper_bound(native, pointer, length)
        }
        return this
    }

    actual fun iterateLowerBound(): Slice? = lowerBound?.toSlice()

    actual fun setIterateLowerBound(iterateLowerBound: AbstractSlice<*>): ReadOptions {
        checkOwningHandle()
        lowerBound = updateBound(lowerBound, iterateLowerBound) { pointer, length ->
            rocksdb_readoptions_set_iterate_lower_bound(native, pointer, length)
        }
        return this
    }

    actual fun readTier(): ReadTier {
        checkOwningHandle()
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
        checkOwningHandle()
        rocksdb_readoptions_set_read_tier(native, readTier.getValue().toInt())
        return this
    }

    actual fun tailing(): Boolean {
        checkOwningHandle()
        return rocksdb_readoptions_get_tailing(native).toBoolean()
    }

    actual fun setTailing(tailing: Boolean): ReadOptions {
        checkOwningHandle()
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
    setter: (CPointer<ByteVar>?, size_t) -> Unit
): BoundBuffer {
    val bytes = slice.copyBytes()
    if (bytes.isEmpty()) {
        val buffer = nativeHeap.allocArray<ByteVar>(1)
        try {
            setter(buffer, 0u)
            current?.free()
            return BoundBuffer(buffer, bytes)
        } catch (throwable: Throwable) {
            nativeHeap.free(buffer.rawValue)
            throw throwable
        }
    }

    val buffer = nativeHeap.allocArray<ByteVar>(bytes.size)
    try {
        bytes.usePinned { pinned ->
            memcpy(buffer, pinned.addressOf(0), bytes.size.asSizeT())
        }
        setter(buffer, bytes.size.asSizeT())
        current?.free()
        return BoundBuffer(buffer, bytes)
    } catch (throwable: Throwable) {
        nativeHeap.free(buffer.rawValue)
        throw throwable
    }
}

private fun AbstractSlice<*>.copyBytes(): ByteArray = when (this) {
    is Slice -> data.copyOf()
    is DirectSlice -> data().array()
    else -> when (val raw = data()) {
        is ByteArray -> raw.copyOf()
        is ByteBuffer -> raw.array()
        else -> error("Unsupported slice type: ${this::class.simpleName}")
    }
}

actual fun ReadOptions.setTableFilter(tableFilter: AbstractTableFilter): ReadOptions {
    checkOwningHandle()
    if (tableFilterRef === tableFilter) {
        return this
    }
    val previous = tableFilterRef
    tableFilter.transferOwnershipToReadOptions()
    this.tableFilterRef = tableFilter
    rocksdb_readoptions_set_table_filter(native, tableFilter.native)
    previous?.closeFromReadOptions()
    return this
}
