@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import maryk.asSizeT

internal fun setNativeCompressionPerLevel(
    options: CPointer<rocksdb_options_t>,
    compressionLevels: List<CompressionType>,
) {
    val values = IntArray(compressionLevels.size) { compressionLevels[it].value.toUByte().toInt() }
    if (values.isEmpty()) {
        rocksdb.rocksdb_options_set_compression_per_level(options, null, 0.asSizeT())
    } else {
        values.usePinned {
            rocksdb.rocksdb_options_set_compression_per_level(options, it.addressOf(0), values.size.asSizeT())
        }
    }
}
