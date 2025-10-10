package maryk.rocksdb

import cnames.structs.rocksdb_mutable_cf_options_t
import kotlinx.cinterop.CPointer
import maryk.toBoolean
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
) : AbstractMutableOptions() {
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
    val native = Unit.wrapWithNullErrorThrower { error ->
        rocksdb_mutable_cf_options_create_from_string(str, ignoreUnknown.toUByte(), error)
    } ?: error("Unable to parse mutable column family options")
    return MutableColumnFamilyOptionsBuilder(native)
}

actual class MutableColumnFamilyOptionsBuilder internal constructor(
    internal val native: CPointer<rocksdb_mutable_cf_options_t>,
) : AbstractMutableOptions(),
    MutableColumnFamilyOptionsInterface<MutableColumnFamilyOptionsBuilder> {

    protected override fun dispose() {
        rocksdb_mutable_cf_options_destroy(native)
    }

    actual override fun setWriteBufferSize(writeBufferSize: Long): MutableColumnFamilyOptionsBuilder {
        rocksdb_mutable_cf_options_set_write_buffer_size(native, writeBufferSize.toULong())
        return this
    }

    actual override fun writeBufferSize(): Long =
        rocksdb_mutable_cf_options_get_write_buffer_size(native).toLong()

    actual override fun setDisableAutoCompactions(
        disableAutoCompactions: Boolean,
    ): MutableColumnFamilyOptionsBuilder {
        rocksdb_mutable_cf_options_set_disable_auto_compactions(native, disableAutoCompactions.toUByte())
        return this
    }

    actual override fun disableAutoCompactions(): Boolean =
        rocksdb_mutable_cf_options_get_disable_auto_compactions(native).toBoolean()

    actual override fun setLevel0FileNumCompactionTrigger(
        level0FileNumCompactionTrigger: Int,
    ): MutableColumnFamilyOptionsBuilder {
        rocksdb_mutable_cf_options_set_level0_file_num_compaction_trigger(native, level0FileNumCompactionTrigger)
        return this
    }

    actual override fun level0FileNumCompactionTrigger(): Int =
        rocksdb_mutable_cf_options_get_level0_file_num_compaction_trigger(native)

    actual override fun setMaxCompactionBytes(maxCompactionBytes: Long): MutableColumnFamilyOptionsBuilder {
        rocksdb_mutable_cf_options_set_max_compaction_bytes(native, maxCompactionBytes.toULong())
        return this
    }

    actual override fun maxCompactionBytes(): Long =
        rocksdb_mutable_cf_options_get_max_compaction_bytes(native).toLong()

    actual override fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): MutableColumnFamilyOptionsBuilder {
        rocksdb_mutable_cf_options_set_max_bytes_for_level_base(native, maxBytesForLevelBase.toULong())
        return this
    }

    actual override fun maxBytesForLevelBase(): Long =
        rocksdb_mutable_cf_options_get_max_bytes_for_level_base(native).toLong()

    actual override fun setCompressionType(
        compressionType: CompressionType,
    ): MutableColumnFamilyOptionsBuilder {
        rocksdb_mutable_cf_options_set_compression(native, compressionType.value.toInt())
        return this
    }

    actual override fun compressionType(): CompressionType =
        getCompressionType(rocksdb_mutable_cf_options_get_compression(native).toByte())

    actual fun build(): MutableColumnFamilyOptions {
        val copy = rocksdb_mutable_cf_options_clone(native)
            ?: error("Unable to clone mutable column family options")
        return MutableColumnFamilyOptions(copy)
    }
}
