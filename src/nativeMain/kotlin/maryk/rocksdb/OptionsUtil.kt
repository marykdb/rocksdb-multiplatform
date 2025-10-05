@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_configoptions_t
import cnames.structs.rocksdb_loaded_cf_options_t
import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_free
import rocksdb.rocksdb_optionsutil_descriptor_name
import rocksdb.rocksdb_optionsutil_descriptor_options
import rocksdb.rocksdb_optionsutil_descriptors_count
import rocksdb.rocksdb_optionsutil_descriptors_destroy
import rocksdb.rocksdb_optionsutil_get_latest_options_file_name
import rocksdb.rocksdb_optionsutil_load_latest_options
import rocksdb.rocksdb_optionsutil_load_options_from_file

actual object OptionsUtil {
    @Throws(RocksDBException::class)
    actual fun loadLatestOptions(
        configOptions: ConfigOptions,
        dbPath: String,
        dbOptions: DBOptions,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>,
    ) {
        loadDescriptors(configOptions, dbPath, dbOptions, columnFamilyDescriptors) { config, path, options, error ->
            rocksdb_optionsutil_load_latest_options(config, path, options, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun loadOptionsFromFile(
        configOptions: ConfigOptions,
        optionsFilePath: String,
        dbOptions: DBOptions,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>,
    ) {
        loadDescriptors(configOptions, optionsFilePath, dbOptions, columnFamilyDescriptors) { config, path, options, error ->
            rocksdb_optionsutil_load_options_from_file(config, path, options, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun getLatestOptionsFileName(dbPath: String, env: Env): String {
        val pointer = Unit.wrapWithNullErrorThrower { error ->
            rocksdb_optionsutil_get_latest_options_file_name(dbPath, env.native, error)
        }
        val fileName = pointer?.toKString().orEmpty()
        pointer?.let { rocksdb_free(it) }
        return fileName
    }

    private inline fun loadDescriptors(
        configOptions: ConfigOptions,
        path: String,
        dbOptions: DBOptions,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>,
        crossinline loader: (
            config: CValuesRef<rocksdb_configoptions_t>?,
            path: String?,
            options: CPointer<rocksdb_options_t>,
            error: CValuesRef<CPointerVar<ByteVar>>,
        ) -> CPointer<rocksdb_loaded_cf_options_t>?,
    ) {
        columnFamilyDescriptors.clear()
        val descriptorSet = Unit.wrapWithNullErrorThrower { error ->
            loader(configOptions.native, path, dbOptions.native, error)
        }
        descriptorSet?.let { bundle ->
            try {
                populateDescriptors(bundle, columnFamilyDescriptors)
            } finally {
                rocksdb_optionsutil_descriptors_destroy(bundle)
            }
        }
    }

    private fun populateDescriptors(
        bundle: CPointer<rocksdb_loaded_cf_options_t>?,
        columnFamilyDescriptors: MutableList<ColumnFamilyDescriptor>,
    ) {
        val count = rocksdb_optionsutil_descriptors_count(bundle).toInt()
        if (count == 0) return
        repeat(count) { index ->
            memScoped {
                val length = alloc<ULongVar>()
                val namePtr = rocksdb_optionsutil_descriptor_name(bundle, index.toULong(), length.ptr)
                val name = namePtr?.readBytes(length.value.toInt()) ?: ByteArray(0)
                namePtr?.let { rocksdb_free(it) }
                val optionsPtr = rocksdb_optionsutil_descriptor_options(bundle, index.toULong())
                requireNotNull(optionsPtr) { "Column family options pointer was null" }
                val cfOptions = ColumnFamilyOptions.wrap(optionsPtr, owning = true)
                columnFamilyDescriptors += ColumnFamilyDescriptor(name, cfOptions)
            }
        }
    }
}
