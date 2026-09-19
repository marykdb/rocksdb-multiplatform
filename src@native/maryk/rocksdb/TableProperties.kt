@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_tableproperties_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.asSizeT
import maryk.toByteArray
import maryk.toCheckedLong
import platform.posix.size_t
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
    internal constructor(native: CPointer<rocksdb_tableproperties_t>) : this(
        dataSizeValue = rocksdb_tableproperties_data_size(native).toCheckedLong("table properties data size"),
        indexSizeValue = rocksdb_tableproperties_index_size(native).toCheckedLong("table properties index size"),
        filterSizeValue = rocksdb_tableproperties_filter_size(native).toCheckedLong("table properties filter size"),
        rawKeySizeValue = rocksdb_tableproperties_raw_key_size(native).toCheckedLong("table properties raw key size"),
        rawValueSizeValue = rocksdb_tableproperties_raw_value_size(native).toCheckedLong("table properties raw value size"),
        numDataBlocksValue = rocksdb_tableproperties_num_data_blocks(native).toCheckedLong("table properties data block count"),
        numEntriesValue = rocksdb_tableproperties_num_entries(native).toCheckedLong("table properties entry count"),
        numDeletionsValue = rocksdb_tableproperties_num_deletions(native).toCheckedLong("table properties deletion count"),
        numMergeOperandsValue = rocksdb_tableproperties_num_merge_operands(native).toCheckedLong("table properties merge operand count"),
        numRangeDeletionsValue = rocksdb_tableproperties_num_range_deletions(native).toCheckedLong("table properties range deletion count"),
        formatVersionValue = rocksdb_tableproperties_format_version(native).toCheckedLong("table properties format version"),
        columnFamilyIdValue = rocksdb_tableproperties_column_family_id(native).toCheckedLong("table properties column family id"),
        columnFamilyNameValue = memScoped {
            val length = alloc<size_tVar>()
            decodeOptionalTablePropertyString(
                rocksdb_tableproperties_column_family_name(native, length.ptr),
                length.value,
                "column family name",
            )
        },
        creationTimeValue = rocksdb_tableproperties_creation_time(native).toCheckedLong("table properties creation time"),
        oldestKeyTimeValue = rocksdb_tableproperties_oldest_key_time(native).toCheckedLong("table properties oldest key time"),
        slowCompressionEstimatedDataSizeValue =
            rocksdb_tableproperties_slow_compression_estimated_data_size(native).toCheckedLong("table properties slow compression estimated data size"),
        fastCompressionEstimatedDataSizeValue =
            rocksdb_tableproperties_fast_compression_estimated_data_size(native).toCheckedLong("table properties fast compression estimated data size"),
        filterPolicyNameValue = memScoped {
            val length = alloc<size_tVar>()
            decodeOptionalTablePropertyString(
                rocksdb_tableproperties_filter_policy_name(native, length.ptr),
                length.value,
                "filter policy name",
            )
        },
        comparatorNameValue = memScoped {
            val length = alloc<size_tVar>()
            decodeOptionalTablePropertyString(
                rocksdb_tableproperties_comparator_name(native, length.ptr),
                length.value,
                "comparator name",
            )
        },
        mergeOperatorNameValue = memScoped {
            val length = alloc<size_tVar>()
            decodeOptionalTablePropertyString(
                rocksdb_tableproperties_merge_operator_name(native, length.ptr),
                length.value,
                "merge operator name",
            )
        },
        prefixExtractorNameValue = memScoped {
            val length = alloc<size_tVar>()
            decodeOptionalTablePropertyString(
                rocksdb_tableproperties_prefix_extractor_name(native, length.ptr),
                length.value,
                "prefix extractor name",
            )
        },
        propertyCollectorsNamesValue = memScoped {
            val length = alloc<size_tVar>()
            decodeOptionalTablePropertyString(
                rocksdb_tableproperties_property_collectors_names(native, length.ptr),
                length.value,
                "property collectors names",
            )
        },
        compressionNameValue = memScoped {
            val length = alloc<size_tVar>()
            decodeOptionalTablePropertyString(
                rocksdb_tableproperties_compression_name(native, length.ptr),
                length.value,
                "compression name",
            )
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

private fun decodeOptionalTablePropertyString(
    value: CPointer<ByteVar>?,
    length: size_t,
    label: String,
): String? {
    if (value == null) {
        check(length == 0.asSizeT()) {
            "RocksDB returned null table properties $label for $length bytes"
        }
        return null
    }
    return value.toByteArray(length).decodeToString()
}
