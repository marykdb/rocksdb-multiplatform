package maryk.rocksdb

import cnames.structs.rocksdb_tableproperties_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toByteArray
import platform.posix.size_tVar
import rocksdb.rocksdb_tableproperties_column_family_id
import rocksdb.rocksdb_tableproperties_column_family_name
import rocksdb.rocksdb_tableproperties_comparator_name
import rocksdb.rocksdb_tableproperties_compression_name
import rocksdb.rocksdb_tableproperties_creation_time
import rocksdb.rocksdb_tableproperties_data_size
import rocksdb.rocksdb_tableproperties_fast_compression_estimated_data_size
import rocksdb.rocksdb_tableproperties_filter_policy_name
import rocksdb.rocksdb_tableproperties_filter_size
import rocksdb.rocksdb_tableproperties_format_version
import rocksdb.rocksdb_tableproperties_index_size
import rocksdb.rocksdb_tableproperties_merge_operator_name
import rocksdb.rocksdb_tableproperties_num_data_blocks
import rocksdb.rocksdb_tableproperties_num_deletions
import rocksdb.rocksdb_tableproperties_num_entries
import rocksdb.rocksdb_tableproperties_num_merge_operands
import rocksdb.rocksdb_tableproperties_num_range_deletions
import rocksdb.rocksdb_tableproperties_oldest_key_time
import rocksdb.rocksdb_tableproperties_prefix_extractor_name
import rocksdb.rocksdb_tableproperties_property_collectors_names
import rocksdb.rocksdb_tableproperties_raw_key_size
import rocksdb.rocksdb_tableproperties_raw_value_size
import rocksdb.rocksdb_tableproperties_slow_compression_estimated_data_size

actual class TableProperties internal constructor(
    private val dataSizeValue: Long,
    private val indexSizeValue: Long,
    private val filterSizeValue: Long,
    private val rawKeySizeValue: Long,
    private val rawValueSizeValue: Long,
    private val numDataBlocksValue: Long,
    private val numEntriesValue: Long,
    private val numDeletionsValue: Long,
    private val numMergeOperandsValue: Long,
    private val numRangeDeletionsValue: Long,
    private val formatVersionValue: Long,
    private val columnFamilyIdValue: Long,
    private val columnFamilyNameValue: String?,
    private val creationTimeValue: Long,
    private val oldestKeyTimeValue: Long,
    private val slowCompressionEstimatedDataSizeValue: Long,
    private val fastCompressionEstimatedDataSizeValue: Long,
    private val filterPolicyNameValue: String?,
    private val comparatorNameValue: String?,
    private val mergeOperatorNameValue: String?,
    private val prefixExtractorNameValue: String?,
    private val propertyCollectorsNamesValue: String?,
    private val compressionNameValue: String?,
) {
    internal constructor(native: CPointer<rocksdb_tableproperties_t>?) : this(
        dataSizeValue = rocksdb_tableproperties_data_size(native).toLong(),
        indexSizeValue = rocksdb_tableproperties_index_size(native).toLong(),
        filterSizeValue = rocksdb_tableproperties_filter_size(native).toLong(),
        rawKeySizeValue = rocksdb_tableproperties_raw_key_size(native).toLong(),
        rawValueSizeValue = rocksdb_tableproperties_raw_value_size(native).toLong(),
        numDataBlocksValue = rocksdb_tableproperties_num_data_blocks(native).toLong(),
        numEntriesValue = rocksdb_tableproperties_num_entries(native).toLong(),
        numDeletionsValue = rocksdb_tableproperties_num_deletions(native).toLong(),
        numMergeOperandsValue = rocksdb_tableproperties_num_merge_operands(native).toLong(),
        numRangeDeletionsValue = rocksdb_tableproperties_num_range_deletions(native).toLong(),
        formatVersionValue = rocksdb_tableproperties_format_version(native).toLong(),
        columnFamilyIdValue = rocksdb_tableproperties_column_family_id(native).toLong(),
        columnFamilyNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_tableproperties_column_family_name(native, length.ptr)?.toByteArray(length.value)
                ?.decodeToString()
        },
        creationTimeValue = rocksdb_tableproperties_creation_time(native).toLong(),
        oldestKeyTimeValue = rocksdb_tableproperties_oldest_key_time(native).toLong(),
        slowCompressionEstimatedDataSizeValue =
            rocksdb_tableproperties_slow_compression_estimated_data_size(native).toLong(),
        fastCompressionEstimatedDataSizeValue =
            rocksdb_tableproperties_fast_compression_estimated_data_size(native).toLong(),
        filterPolicyNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_tableproperties_filter_policy_name(native, length.ptr)?.toByteArray(length.value)
                ?.decodeToString()
        },
        comparatorNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_tableproperties_comparator_name(native, length.ptr)?.toByteArray(length.value)
                ?.decodeToString()
        },
        mergeOperatorNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_tableproperties_merge_operator_name(native, length.ptr)?.toByteArray(length.value)
                ?.decodeToString()
        },
        prefixExtractorNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_tableproperties_prefix_extractor_name(native, length.ptr)?.toByteArray(length.value)
                ?.decodeToString()
        },
        propertyCollectorsNamesValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_tableproperties_property_collectors_names(native, length.ptr)
                ?.toByteArray(length.value)
                ?.decodeToString()
        },
        compressionNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_tableproperties_compression_name(native, length.ptr)?.toByteArray(length.value)
                ?.decodeToString()
        },
    )

    actual fun dataSize(): Long = dataSizeValue
    actual fun indexSize(): Long = indexSizeValue
    actual fun filterSize(): Long = filterSizeValue
    actual fun rawKeySize(): Long = rawKeySizeValue
    actual fun rawValueSize(): Long = rawValueSizeValue
    actual fun numDataBlocks(): Long = numDataBlocksValue
    actual fun numEntries(): Long = numEntriesValue
    actual fun numDeletions(): Long = numDeletionsValue
    actual fun numMergeOperands(): Long = numMergeOperandsValue
    actual fun numRangeDeletions(): Long = numRangeDeletionsValue
    actual fun formatVersion(): Long = formatVersionValue
    actual fun columnFamilyId(): Long = columnFamilyIdValue
    actual fun columnFamilyName(): String? = columnFamilyNameValue
    actual fun creationTime(): Long = creationTimeValue
    actual fun oldestKeyTime(): Long = oldestKeyTimeValue
    actual fun slowCompressionEstimatedDataSize(): Long =
        slowCompressionEstimatedDataSizeValue
    actual fun fastCompressionEstimatedDataSize(): Long =
        fastCompressionEstimatedDataSizeValue
    actual fun filterPolicyName(): String? = filterPolicyNameValue
    actual fun comparatorName(): String? = comparatorNameValue
    actual fun mergeOperatorName(): String? = mergeOperatorNameValue
    actual fun prefixExtractorName(): String? = prefixExtractorNameValue
    actual fun propertyCollectorsNames(): String? = propertyCollectorsNamesValue
    actual fun compressionName(): String? = compressionNameValue
}
