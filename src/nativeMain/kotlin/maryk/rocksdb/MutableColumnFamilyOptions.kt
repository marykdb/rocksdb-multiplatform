@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import rocksdb.rocksdb_mutable_cf_options_get_arena_block_size
import rocksdb.rocksdb_mutable_cf_options_get_blob_compaction_readahead_size
import rocksdb.rocksdb_mutable_cf_options_get_blob_file_size
import rocksdb.rocksdb_mutable_cf_options_get_blob_file_starting_level
import rocksdb.rocksdb_mutable_cf_options_get_blob_garbage_collection_age_cutoff
import rocksdb.rocksdb_mutable_cf_options_get_blob_garbage_collection_force_threshold
import rocksdb.rocksdb_mutable_cf_options_get_enable_blob_files
import rocksdb.rocksdb_mutable_cf_options_get_enable_blob_garbage_collection
import rocksdb.rocksdb_mutable_cf_options_get_experimental_mempurge_threshold
import rocksdb.rocksdb_mutable_cf_options_get_hard_pending_compaction_bytes_limit
import rocksdb.rocksdb_mutable_cf_options_get_inplace_update_num_locks
import rocksdb.rocksdb_mutable_cf_options_get_level0_slowdown_writes_trigger
import rocksdb.rocksdb_mutable_cf_options_get_level0_stop_writes_trigger
import rocksdb.rocksdb_mutable_cf_options_get_max_sequential_skip_in_iterations
import rocksdb.rocksdb_mutable_cf_options_get_max_successive_merges
import rocksdb.rocksdb_mutable_cf_options_get_max_write_buffer_number
import rocksdb.rocksdb_mutable_cf_options_get_memtable_huge_page_size
import rocksdb.rocksdb_mutable_cf_options_get_memtable_prefix_bloom_size_ratio
import rocksdb.rocksdb_mutable_cf_options_get_memtable_whole_key_filtering
import rocksdb.rocksdb_mutable_cf_options_get_min_blob_size
import rocksdb.rocksdb_mutable_cf_options_get_min_tombstones_for_range_conversion
import rocksdb.rocksdb_mutable_cf_options_get_paranoid_file_checks
import rocksdb.rocksdb_mutable_cf_options_get_periodic_compaction_seconds
import rocksdb.rocksdb_mutable_cf_options_get_read_triggered_compaction_threshold
import rocksdb.rocksdb_mutable_cf_options_get_report_bg_io_stats
import rocksdb.rocksdb_mutable_cf_options_get_soft_pending_compaction_bytes_limit
import rocksdb.rocksdb_mutable_cf_options_get_target_file_size_base
import rocksdb.rocksdb_mutable_cf_options_get_target_file_size_multiplier
import rocksdb.rocksdb_mutable_cf_options_get_ttl
import rocksdb.rocksdb_mutable_cf_options_set_arena_block_size
import rocksdb.rocksdb_mutable_cf_options_set_blob_compaction_readahead_size
import rocksdb.rocksdb_mutable_cf_options_set_blob_file_size
import rocksdb.rocksdb_mutable_cf_options_set_blob_file_starting_level
import rocksdb.rocksdb_mutable_cf_options_set_blob_garbage_collection_age_cutoff
import rocksdb.rocksdb_mutable_cf_options_set_blob_garbage_collection_force_threshold
import rocksdb.rocksdb_mutable_cf_options_set_enable_blob_files
import rocksdb.rocksdb_mutable_cf_options_set_enable_blob_garbage_collection
import rocksdb.rocksdb_mutable_cf_options_set_experimental_mempurge_threshold
import rocksdb.rocksdb_mutable_cf_options_set_hard_pending_compaction_bytes_limit
import rocksdb.rocksdb_mutable_cf_options_set_inplace_update_num_locks
import rocksdb.rocksdb_mutable_cf_options_set_level0_slowdown_writes_trigger
import rocksdb.rocksdb_mutable_cf_options_set_level0_stop_writes_trigger
import rocksdb.rocksdb_mutable_cf_options_set_max_sequential_skip_in_iterations
import rocksdb.rocksdb_mutable_cf_options_set_max_successive_merges
import rocksdb.rocksdb_mutable_cf_options_set_max_write_buffer_number
import rocksdb.rocksdb_mutable_cf_options_set_memtable_huge_page_size
import rocksdb.rocksdb_mutable_cf_options_set_memtable_prefix_bloom_size_ratio
import rocksdb.rocksdb_mutable_cf_options_set_memtable_whole_key_filtering
import rocksdb.rocksdb_mutable_cf_options_set_min_blob_size
import rocksdb.rocksdb_mutable_cf_options_set_min_tombstones_for_range_conversion
import rocksdb.rocksdb_mutable_cf_options_set_paranoid_file_checks
import rocksdb.rocksdb_mutable_cf_options_set_periodic_compaction_seconds
import rocksdb.rocksdb_mutable_cf_options_set_read_triggered_compaction_threshold
import rocksdb.rocksdb_mutable_cf_options_set_report_bg_io_stats
import rocksdb.rocksdb_mutable_cf_options_set_soft_pending_compaction_bytes_limit
import rocksdb.rocksdb_mutable_cf_options_set_target_file_size_base
import rocksdb.rocksdb_mutable_cf_options_set_target_file_size_multiplier
import rocksdb.rocksdb_mutable_cf_options_set_ttl

import cnames.structs.rocksdb_mutable_cf_options_t
import kotlinx.cinterop.CPointer
import maryk.asSizeT
import maryk.asUInt32
import maryk.asUInt64
import maryk.sizeTToLong
import maryk.toBoolean
import maryk.toCheckedInt
import maryk.toCheckedLong
import maryk.toUByte
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_mutable_cf_options_clone
import rocksdb.rocksdb_mutable_cf_options_create
import rocksdb.rocksdb_mutable_cf_options_create_from_string
import rocksdb.rocksdb_mutable_cf_options_destroy
import rocksdb.rocksdb_mutable_cf_options_get_compression
import rocksdb.rocksdb_mutable_cf_options_get_disable_auto_compactions
import rocksdb.rocksdb_mutable_cf_options_get_level0_file_num_compaction_trigger
import rocksdb.rocksdb_mutable_cf_options_get_max_bytes_for_level_base
import rocksdb.rocksdb_mutable_cf_options_get_max_compaction_bytes
import rocksdb.rocksdb_mutable_cf_options_get_write_buffer_size
import rocksdb.rocksdb_mutable_cf_options_set_compression
import rocksdb.rocksdb_mutable_cf_options_set_disable_auto_compactions
import rocksdb.rocksdb_mutable_cf_options_set_level0_file_num_compaction_trigger
import rocksdb.rocksdb_mutable_cf_options_set_max_bytes_for_level_base
import rocksdb.rocksdb_mutable_cf_options_set_max_compaction_bytes
import rocksdb.rocksdb_mutable_cf_options_set_write_buffer_size

actual class MutableColumnFamilyOptions internal constructor(
    internal val native: CPointer<rocksdb_mutable_cf_options_t>,
    private val explicitValues: Map<String, String>,
) : AbstractMutableOptions() {
    internal fun valuesForApply(): Map<String, String> {
        checkOpen()
        return explicitValues
    }

    protected override fun dispose() {
        rocksdb_mutable_cf_options_destroy(native)
    }
}

actual fun mutableColumnFamilyOptionsBuilder(): MutableColumnFamilyOptionsBuilder =
    MutableColumnFamilyOptionsBuilder(requireNotNull(rocksdb_mutable_cf_options_create()) {
        "Unable to allocate mutable column family options"
    })

actual fun parseMutableColumnFamilyOptions(
    str: String,
    ignoreUnknown: Boolean,
): MutableColumnFamilyOptionsBuilder {
    require('\u0000' !in str) { "Option strings must not contain NUL" }
    val native = Unit.wrapWithNullErrorThrower { error ->
        rocksdb_mutable_cf_options_create_from_string(str, ignoreUnknown.toUByte(), error)
    } ?: error("Unable to parse mutable column family options")
    return try {
        val values = parsedLiveOptionValues(str, ignoreUnknown) { entry ->
            val parsed = Unit.wrapWithNullErrorThrower { error ->
                rocksdb_mutable_cf_options_create_from_string(entry, 0u, error)
            } ?: error("Unable to parse mutable options entry")
            rocksdb_mutable_cf_options_destroy(parsed)
        }
        MutableColumnFamilyOptionsBuilder(native, values.toMutableMap())
    } catch (error: Throwable) {
        rocksdb_mutable_cf_options_destroy(native)
        throw error
    }
}

actual class MutableColumnFamilyOptionsBuilder internal constructor(
    internal val native: CPointer<rocksdb_mutable_cf_options_t>,
    private val explicitValues: MutableMap<String, String> = linkedMapOf(),
) : AbstractMutableOptions(),
    MutableColumnFamilyOptionsInterface<MutableColumnFamilyOptionsBuilder> {

    protected override fun dispose() {
        rocksdb_mutable_cf_options_destroy(native)
    }

    actual override fun setWriteBufferSize(writeBufferSize: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        writeBufferSize.asSizeT()
        rocksdb_mutable_cf_options_set_write_buffer_size(native, writeBufferSize.asUInt64())
        explicitValues["write_buffer_size"] = writeBufferSize.toString()
        return this
    }

    actual override fun writeBufferSize(): Long {
        checkOpen()
        return rocksdb_mutable_cf_options_get_write_buffer_size(native)
            .toCheckedLong("mutable column-family options write buffer size")
    }

    actual override fun setDisableAutoCompactions(
        disableAutoCompactions: Boolean,
    ): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_disable_auto_compactions(native, disableAutoCompactions.toUByte())
        explicitValues["disable_auto_compactions"] = disableAutoCompactions.toString()
        return this
    }

    actual override fun disableAutoCompactions(): Boolean {
        checkOpen()
        return rocksdb_mutable_cf_options_get_disable_auto_compactions(native).toBoolean()
    }

    actual override fun setLevel0FileNumCompactionTrigger(
        level0FileNumCompactionTrigger: Int,
    ): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_level0_file_num_compaction_trigger(native, level0FileNumCompactionTrigger)
        explicitValues["level0_file_num_compaction_trigger"] = level0FileNumCompactionTrigger.toString()
        return this
    }

    actual override fun level0FileNumCompactionTrigger(): Int {
        checkOpen()
        return rocksdb_mutable_cf_options_get_level0_file_num_compaction_trigger(native)
    }

    actual override fun setMaxCompactionBytes(maxCompactionBytes: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_max_compaction_bytes(native, maxCompactionBytes.asUInt64())
        explicitValues["max_compaction_bytes"] = maxCompactionBytes.toString()
        return this
    }

    actual override fun maxCompactionBytes(): Long {
        checkOpen()
        return rocksdb_mutable_cf_options_get_max_compaction_bytes(native)
            .toCheckedLong("mutable column-family options max compaction bytes")
    }

    actual override fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_max_bytes_for_level_base(native, maxBytesForLevelBase.asUInt64())
        explicitValues["max_bytes_for_level_base"] = maxBytesForLevelBase.toString()
        return this
    }

    actual override fun maxBytesForLevelBase(): Long {
        checkOpen()
        return rocksdb_mutable_cf_options_get_max_bytes_for_level_base(native)
            .toCheckedLong("mutable column-family options max bytes for level base")
    }

    actual override fun setCompressionType(
        compressionType: CompressionType,
    ): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_compression(native, compressionType.value.toInt())
        explicitValues["compression"] = compressionOptionValue(compressionType)
        return this
    }

    actual override fun compressionType(): CompressionType {
        checkOpen()
        return getCompressionType(rocksdb_mutable_cf_options_get_compression(native).toByte())
    }

    actual fun build(): MutableColumnFamilyOptions {
        checkOpen()
        val copy = rocksdb_mutable_cf_options_clone(native)
            ?: error("Unable to clone mutable column family options")
        return MutableColumnFamilyOptions(copy, explicitValues.toMap())
    }

    actual override fun setMaxWriteBufferNumber(value: Int): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_max_write_buffer_number(native, value)
        explicitValues["max_write_buffer_number"] = value.toString()
        return this
    }

    actual override fun maxWriteBufferNumber(): Int {
        checkOpen()
        if ("max_write_buffer_number" !in explicitValues) throw NoSuchElementException("max_write_buffer_number has not been set")
        return rocksdb_mutable_cf_options_get_max_write_buffer_number(native)
    }

    actual override fun setArenaBlockSize(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_arena_block_size(native, value.asSizeT())
        explicitValues["arena_block_size"] = value.toString()
        return this
    }

    actual override fun arenaBlockSize(): Long {
        checkOpen()
        if ("arena_block_size" !in explicitValues) throw NoSuchElementException("arena_block_size has not been set")
        return sizeTToLong(rocksdb_mutable_cf_options_get_arena_block_size(native), "arena_block_size")
    }

    actual override fun setMemtablePrefixBloomSizeRatio(value: Double): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_memtable_prefix_bloom_size_ratio(native, value)
        explicitValues["memtable_prefix_bloom_size_ratio"] = value.toString()
        return this
    }

    actual override fun memtablePrefixBloomSizeRatio(): Double {
        checkOpen()
        if ("memtable_prefix_bloom_size_ratio" !in explicitValues) throw NoSuchElementException("memtable_prefix_bloom_size_ratio has not been set")
        return rocksdb_mutable_cf_options_get_memtable_prefix_bloom_size_ratio(native)
    }

    actual override fun setMemtableWholeKeyFiltering(value: Boolean): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_memtable_whole_key_filtering(native, value.toUByte())
        explicitValues["memtable_whole_key_filtering"] = value.toString()
        return this
    }

    actual override fun memtableWholeKeyFiltering(): Boolean {
        checkOpen()
        if ("memtable_whole_key_filtering" !in explicitValues) throw NoSuchElementException("memtable_whole_key_filtering has not been set")
        return rocksdb_mutable_cf_options_get_memtable_whole_key_filtering(native).toBoolean()
    }

    actual override fun setMemtableHugePageSize(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_memtable_huge_page_size(native, value.asSizeT())
        explicitValues["memtable_huge_page_size"] = value.toString()
        return this
    }

    actual override fun memtableHugePageSize(): Long {
        checkOpen()
        if ("memtable_huge_page_size" !in explicitValues) throw NoSuchElementException("memtable_huge_page_size has not been set")
        return sizeTToLong(rocksdb_mutable_cf_options_get_memtable_huge_page_size(native), "memtable_huge_page_size")
    }

    actual override fun setMaxSuccessiveMerges(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_max_successive_merges(native, value.asSizeT())
        explicitValues["max_successive_merges"] = value.toString()
        return this
    }

    actual override fun maxSuccessiveMerges(): Long {
        checkOpen()
        if ("max_successive_merges" !in explicitValues) throw NoSuchElementException("max_successive_merges has not been set")
        return sizeTToLong(rocksdb_mutable_cf_options_get_max_successive_merges(native), "max_successive_merges")
    }

    actual override fun setMaxSequentialSkipInIterations(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_max_sequential_skip_in_iterations(native, value.asUInt64())
        explicitValues["max_sequential_skip_in_iterations"] = value.toString()
        return this
    }

    actual override fun maxSequentialSkipInIterations(): Long {
        checkOpen()
        if ("max_sequential_skip_in_iterations" !in explicitValues) throw NoSuchElementException("max_sequential_skip_in_iterations has not been set")
        return rocksdb_mutable_cf_options_get_max_sequential_skip_in_iterations(native).toCheckedLong("max_sequential_skip_in_iterations")
    }

    actual override fun setInplaceUpdateNumLocks(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_inplace_update_num_locks(native, value.asSizeT())
        explicitValues["inplace_update_num_locks"] = value.toString()
        return this
    }

    actual override fun inplaceUpdateNumLocks(): Long {
        checkOpen()
        if ("inplace_update_num_locks" !in explicitValues) throw NoSuchElementException("inplace_update_num_locks has not been set")
        return sizeTToLong(rocksdb_mutable_cf_options_get_inplace_update_num_locks(native), "inplace_update_num_locks")
    }

    actual override fun setLevel0SlowdownWritesTrigger(value: Int): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_level0_slowdown_writes_trigger(native, value)
        explicitValues["level0_slowdown_writes_trigger"] = value.toString()
        return this
    }

    actual override fun level0SlowdownWritesTrigger(): Int {
        checkOpen()
        if ("level0_slowdown_writes_trigger" !in explicitValues) throw NoSuchElementException("level0_slowdown_writes_trigger has not been set")
        return rocksdb_mutable_cf_options_get_level0_slowdown_writes_trigger(native)
    }

    actual override fun setLevel0StopWritesTrigger(value: Int): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_level0_stop_writes_trigger(native, value)
        explicitValues["level0_stop_writes_trigger"] = value.toString()
        return this
    }

    actual override fun level0StopWritesTrigger(): Int {
        checkOpen()
        if ("level0_stop_writes_trigger" !in explicitValues) throw NoSuchElementException("level0_stop_writes_trigger has not been set")
        return rocksdb_mutable_cf_options_get_level0_stop_writes_trigger(native)
    }

    actual override fun setTargetFileSizeBase(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_target_file_size_base(native, value.asUInt64())
        explicitValues["target_file_size_base"] = value.toString()
        return this
    }

    actual override fun targetFileSizeBase(): Long {
        checkOpen()
        if ("target_file_size_base" !in explicitValues) throw NoSuchElementException("target_file_size_base has not been set")
        return rocksdb_mutable_cf_options_get_target_file_size_base(native).toCheckedLong("target_file_size_base")
    }

    actual override fun setTargetFileSizeMultiplier(value: Int): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_target_file_size_multiplier(native, value)
        explicitValues["target_file_size_multiplier"] = value.toString()
        return this
    }

    actual override fun targetFileSizeMultiplier(): Int {
        checkOpen()
        if ("target_file_size_multiplier" !in explicitValues) throw NoSuchElementException("target_file_size_multiplier has not been set")
        return rocksdb_mutable_cf_options_get_target_file_size_multiplier(native)
    }


    actual override fun setSoftPendingCompactionBytesLimit(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_soft_pending_compaction_bytes_limit(native, value.asUInt64())
        explicitValues["soft_pending_compaction_bytes_limit"] = value.toString()
        return this
    }

    actual override fun softPendingCompactionBytesLimit(): Long {
        checkOpen()
        if ("soft_pending_compaction_bytes_limit" !in explicitValues) throw NoSuchElementException("soft_pending_compaction_bytes_limit has not been set")
        return rocksdb_mutable_cf_options_get_soft_pending_compaction_bytes_limit(native).toCheckedLong("soft_pending_compaction_bytes_limit")
    }

    actual override fun setHardPendingCompactionBytesLimit(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_hard_pending_compaction_bytes_limit(native, value.asUInt64())
        explicitValues["hard_pending_compaction_bytes_limit"] = value.toString()
        return this
    }

    actual override fun hardPendingCompactionBytesLimit(): Long {
        checkOpen()
        if ("hard_pending_compaction_bytes_limit" !in explicitValues) throw NoSuchElementException("hard_pending_compaction_bytes_limit has not been set")
        return rocksdb_mutable_cf_options_get_hard_pending_compaction_bytes_limit(native).toCheckedLong("hard_pending_compaction_bytes_limit")
    }

    actual override fun setTtl(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_ttl(native, value.asCompactionAgeSeconds())
        explicitValues["ttl"] = value.toString()
        return this
    }

    actual override fun ttl(): Long {
        checkOpen()
        if ("ttl" !in explicitValues) throw NoSuchElementException("ttl has not been set")
        return rocksdb_mutable_cf_options_get_ttl(native).compactionAgeSecondsToLong()
    }

    actual override fun setPeriodicCompactionSeconds(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_periodic_compaction_seconds(native, value.asCompactionAgeSeconds())
        explicitValues["periodic_compaction_seconds"] = value.toString()
        return this
    }

    actual override fun periodicCompactionSeconds(): Long {
        checkOpen()
        if ("periodic_compaction_seconds" !in explicitValues) throw NoSuchElementException("periodic_compaction_seconds has not been set")
        return rocksdb_mutable_cf_options_get_periodic_compaction_seconds(native).compactionAgeSecondsToLong()
    }

    actual override fun setReportBgIoStats(value: Boolean): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_report_bg_io_stats(native, value.toUByte())
        explicitValues["report_bg_io_stats"] = value.toString()
        return this
    }

    actual override fun reportBgIoStats(): Boolean {
        checkOpen()
        if ("report_bg_io_stats" !in explicitValues) throw NoSuchElementException("report_bg_io_stats has not been set")
        return rocksdb_mutable_cf_options_get_report_bg_io_stats(native).toBoolean()
    }

    actual override fun setParanoidFileChecks(value: Boolean): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_paranoid_file_checks(native, value.toUByte())
        explicitValues["paranoid_file_checks"] = value.toString()
        return this
    }

    actual override fun paranoidFileChecks(): Boolean {
        checkOpen()
        if ("paranoid_file_checks" !in explicitValues) throw NoSuchElementException("paranoid_file_checks has not been set")
        return rocksdb_mutable_cf_options_get_paranoid_file_checks(native).toBoolean()
    }

    actual override fun setEnableBlobFiles(value: Boolean): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_enable_blob_files(native, value.toUByte())
        explicitValues["enable_blob_files"] = value.toString()
        return this
    }

    actual override fun enableBlobFiles(): Boolean {
        checkOpen()
        if ("enable_blob_files" !in explicitValues) throw NoSuchElementException("enable_blob_files has not been set")
        return rocksdb_mutable_cf_options_get_enable_blob_files(native).toBoolean()
    }

    actual override fun setMinBlobSize(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_min_blob_size(native, value.asUInt64())
        explicitValues["min_blob_size"] = value.toString()
        return this
    }

    actual override fun minBlobSize(): Long {
        checkOpen()
        if ("min_blob_size" !in explicitValues) throw NoSuchElementException("min_blob_size has not been set")
        return rocksdb_mutable_cf_options_get_min_blob_size(native).toCheckedLong("min_blob_size")
    }

    actual override fun setBlobFileSize(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_blob_file_size(native, value.asUInt64())
        explicitValues["blob_file_size"] = value.toString()
        return this
    }

    actual override fun blobFileSize(): Long {
        checkOpen()
        if ("blob_file_size" !in explicitValues) throw NoSuchElementException("blob_file_size has not been set")
        return rocksdb_mutable_cf_options_get_blob_file_size(native).toCheckedLong("blob_file_size")
    }

    actual override fun setEnableBlobGarbageCollection(value: Boolean): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_enable_blob_garbage_collection(native, value.toUByte())
        explicitValues["enable_blob_garbage_collection"] = value.toString()
        return this
    }

    actual override fun enableBlobGarbageCollection(): Boolean {
        checkOpen()
        if ("enable_blob_garbage_collection" !in explicitValues) throw NoSuchElementException("enable_blob_garbage_collection has not been set")
        return rocksdb_mutable_cf_options_get_enable_blob_garbage_collection(native).toBoolean()
    }

    actual override fun setBlobGarbageCollectionAgeCutoff(value: Double): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_blob_garbage_collection_age_cutoff(native, value)
        explicitValues["blob_garbage_collection_age_cutoff"] = value.toString()
        return this
    }

    actual override fun blobGarbageCollectionAgeCutoff(): Double {
        checkOpen()
        if ("blob_garbage_collection_age_cutoff" !in explicitValues) throw NoSuchElementException("blob_garbage_collection_age_cutoff has not been set")
        return rocksdb_mutable_cf_options_get_blob_garbage_collection_age_cutoff(native)
    }

    actual override fun setBlobGarbageCollectionForceThreshold(value: Double): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_blob_garbage_collection_force_threshold(native, value)
        explicitValues["blob_garbage_collection_force_threshold"] = value.toString()
        return this
    }

    actual override fun blobGarbageCollectionForceThreshold(): Double {
        checkOpen()
        if ("blob_garbage_collection_force_threshold" !in explicitValues) throw NoSuchElementException("blob_garbage_collection_force_threshold has not been set")
        return rocksdb_mutable_cf_options_get_blob_garbage_collection_force_threshold(native)
    }

    actual override fun setBlobCompactionReadaheadSize(value: Long): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_blob_compaction_readahead_size(native, value.asUInt64())
        explicitValues["blob_compaction_readahead_size"] = value.toString()
        return this
    }

    actual override fun blobCompactionReadaheadSize(): Long {
        checkOpen()
        if ("blob_compaction_readahead_size" !in explicitValues) throw NoSuchElementException("blob_compaction_readahead_size has not been set")
        return rocksdb_mutable_cf_options_get_blob_compaction_readahead_size(native).toCheckedLong("blob_compaction_readahead_size")
    }

    actual override fun setBlobFileStartingLevel(value: Int): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_blob_file_starting_level(native, value)
        explicitValues["blob_file_starting_level"] = value.toString()
        return this
    }

    actual override fun blobFileStartingLevel(): Int {
        checkOpen()
        if ("blob_file_starting_level" !in explicitValues) throw NoSuchElementException("blob_file_starting_level has not been set")
        return rocksdb_mutable_cf_options_get_blob_file_starting_level(native)
    }

    actual override fun setExperimentalMempurgeThreshold(value: Double): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_experimental_mempurge_threshold(native, value)
        explicitValues["experimental_mempurge_threshold"] = value.toString()
        return this
    }

    actual override fun experimentalMempurgeThreshold(): Double {
        checkOpen()
        if ("experimental_mempurge_threshold" !in explicitValues) throw NoSuchElementException("experimental_mempurge_threshold has not been set")
        return rocksdb_mutable_cf_options_get_experimental_mempurge_threshold(native)
    }

    actual override fun setMinTombstonesForRangeConversion(value: Int): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_min_tombstones_for_range_conversion(native, value.asUInt32())
        explicitValues["min_tombstones_for_range_conversion"] = value.toString()
        return this
    }

    actual override fun minTombstonesForRangeConversion(): Int {
        checkOpen()
        if ("min_tombstones_for_range_conversion" !in explicitValues) throw NoSuchElementException("min_tombstones_for_range_conversion has not been set")
        return rocksdb_mutable_cf_options_get_min_tombstones_for_range_conversion(native).toCheckedInt("min_tombstones_for_range_conversion")
    }

    actual override fun setReadTriggeredCompactionThreshold(value: Double): MutableColumnFamilyOptionsBuilder {
        checkOpen()
        rocksdb_mutable_cf_options_set_read_triggered_compaction_threshold(native, value)
        explicitValues["read_triggered_compaction_threshold"] = value.toString()
        return this
    }

    actual override fun readTriggeredCompactionThreshold(): Double {
        checkOpen()
        if ("read_triggered_compaction_threshold" !in explicitValues) throw NoSuchElementException("read_triggered_compaction_threshold has not been set")
        return rocksdb_mutable_cf_options_get_read_triggered_compaction_threshold(native)
    }

}
