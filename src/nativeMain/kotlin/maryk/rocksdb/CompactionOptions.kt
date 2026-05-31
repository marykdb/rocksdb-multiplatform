@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_compactionoptions_fifo_t
import cnames.structs.rocksdb_compactionoptions_t
import cnames.structs.rocksdb_compactionoptions_universal_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import maryk.asUInt32
import maryk.asUInt64
import maryk.toCheckedInt
import maryk.toCheckedLong
import maryk.toBoolean
import maryk.toUByte
import rocksdb.rocksdb_compactionoptions_create
import rocksdb.rocksdb_compactionoptions_destroy
import rocksdb.rocksdb_compactionoptions_fifo_allow_compaction
import rocksdb.rocksdb_compactionoptions_fifo_create
import rocksdb.rocksdb_compactionoptions_fifo_destroy
import rocksdb.rocksdb_compactionoptions_fifo_max_table_files_size
import rocksdb.rocksdb_compactionoptions_fifo_set_allow_compaction
import rocksdb.rocksdb_compactionoptions_fifo_set_max_table_files_size
import rocksdb.rocksdb_compactionoptions_get_compression
import rocksdb.rocksdb_compactionoptions_get_max_subcompactions
import rocksdb.rocksdb_compactionoptions_get_output_file_size_limit
import rocksdb.rocksdb_compactionoptions_set_compression
import rocksdb.rocksdb_compactionoptions_set_max_subcompactions
import rocksdb.rocksdb_compactionoptions_set_output_file_size_limit
import rocksdb.rocksdb_compactionoptions_universal_create
import rocksdb.rocksdb_compactionoptions_universal_destroy
import rocksdb.rocksdb_compactionoptions_universal_max_merge_width
import rocksdb.rocksdb_compactionoptions_universal_max_size_amplification_percent
import rocksdb.rocksdb_compactionoptions_universal_min_merge_width
import rocksdb.rocksdb_compactionoptions_universal_set_compression_size_percent
import rocksdb.rocksdb_compactionoptions_universal_set_max_merge_width
import rocksdb.rocksdb_compactionoptions_universal_set_max_size_amplification_percent
import rocksdb.rocksdb_compactionoptions_universal_set_min_merge_width
import rocksdb.rocksdb_compactionoptions_universal_set_size_ratio
import rocksdb.rocksdb_compactionoptions_universal_set_stop_style
import rocksdb.rocksdb_compactionoptions_universal_size_ratio
import rocksdb.rocksdb_compactionoptions_universal_stop_style
import rocksdb.rocksdb_compactionoptions_universal_compression_size_percent

actual class CompactionOptions internal constructor(
    internal val native: CPointer<rocksdb_compactionoptions_t>
) : RocksObject() {
    actual constructor() : this(
        requireNotNull(rocksdb_compactionoptions_create()) {
            "Unable to allocate RocksDB compaction options"
        }
    )

    override fun close() {
        if (tryClose()) {
            rocksdb_compactionoptions_destroy(native)
            super.close()
        }
    }

    actual fun compression(): CompressionType =
        checkOwningHandle().let { getCompressionType(rocksdb_compactionoptions_get_compression(native).toByte()) }

    actual fun setCompression(compression: CompressionType): CompactionOptions {
        checkOwningHandle()
        rocksdb_compactionoptions_set_compression(native, compression.value.toInt())
        return this
    }

    actual fun outputFileSizeLimit(): Long {
        checkOwningHandle()
        val value = rocksdb_compactionoptions_get_output_file_size_limit(native)
        return if (value == ULong.MAX_VALUE) {
            -1
        } else {
            value.toCheckedLong("compaction output file size limit")
        }
    }

    actual fun setOutputFileSizeLimit(limit: Long): CompactionOptions {
        checkOwningHandle()
        require(limit == -1L || limit >= 0L) {
            "output file size limit must be non-negative or -1 for unlimited: $limit"
        }
        val nativeLimit = if (limit == -1L) ULong.MAX_VALUE else limit.toULong()
        rocksdb_compactionoptions_set_output_file_size_limit(native, nativeLimit)
        return this
    }

    actual fun maxSubcompactions(): Int {
        checkOwningHandle()
        return rocksdb_compactionoptions_get_max_subcompactions(native).toCheckedInt("compaction max subcompactions")
    }

    actual fun setMaxSubcompactions(count: Int): CompactionOptions {
        checkOwningHandle()
        rocksdb_compactionoptions_set_max_subcompactions(native, count.asUInt32())
        return this
    }
}

actual class CompactionOptionsFIFO internal constructor(
    internal val native: CPointer<rocksdb_compactionoptions_fifo_t>
) : RocksObject() {
    actual constructor() : this(
        requireNotNull(rocksdb_compactionoptions_fifo_create()) {
            "Unable to allocate RocksDB FIFO compaction options"
        }
    )

    override fun close() {
        if (tryClose()) {
            rocksdb_compactionoptions_fifo_destroy(native)
            super.close()
        }
    }

    actual fun setMaxTableFilesSize(size: Long): CompactionOptionsFIFO {
        checkOwningHandle()
        rocksdb_compactionoptions_fifo_set_max_table_files_size(native, size.asUInt64())
        return this
    }

    actual fun maxTableFilesSize(): Long {
        checkOwningHandle()
        return rocksdb_compactionoptions_fifo_max_table_files_size(native).toCheckedLong("FIFO compaction max table files size")
    }

    actual fun setAllowCompaction(allow: Boolean): CompactionOptionsFIFO {
        checkOwningHandle()
        rocksdb_compactionoptions_fifo_set_allow_compaction(native, allow.toUByte())
        return this
    }

    actual fun allowCompaction(): Boolean {
        checkOwningHandle()
        return rocksdb_compactionoptions_fifo_allow_compaction(native).toBoolean()
    }
}

actual class CompactionOptionsUniversal internal constructor(
    internal val native: CPointer<rocksdb_compactionoptions_universal_t>
) : RocksObject() {
    actual constructor() : this(
        requireNotNull(rocksdb_compactionoptions_universal_create()) {
            "Unable to allocate RocksDB universal compaction options"
        }
    )

    override fun close() {
        if (tryClose()) {
            rocksdb_compactionoptions_universal_destroy(native)
            super.close()
        }
    }

    actual fun setSizeRatio(sizeRatio: Int): CompactionOptionsUniversal {
        checkOwningHandle()
        rocksdb_compactionoptions_universal_set_size_ratio(native, sizeRatio)
        return this
    }

    actual fun sizeRatio(): Int {
        checkOwningHandle()
        return rocksdb_compactionoptions_universal_size_ratio(native)
    }

    actual fun setMinMergeWidth(width: Int): CompactionOptionsUniversal {
        checkOwningHandle()
        rocksdb_compactionoptions_universal_set_min_merge_width(native, width)
        return this
    }

    actual fun minMergeWidth(): Int {
        checkOwningHandle()
        return rocksdb_compactionoptions_universal_min_merge_width(native)
    }

    actual fun setMaxMergeWidth(width: Int): CompactionOptionsUniversal {
        checkOwningHandle()
        rocksdb_compactionoptions_universal_set_max_merge_width(native, width)
        return this
    }

    actual fun maxMergeWidth(): Int {
        checkOwningHandle()
        return rocksdb_compactionoptions_universal_max_merge_width(native)
    }

    actual fun setMaxSizeAmplificationPercent(percent: Int): CompactionOptionsUniversal {
        checkOwningHandle()
        rocksdb_compactionoptions_universal_set_max_size_amplification_percent(native, percent)
        return this
    }

    actual fun maxSizeAmplificationPercent(): Int {
        checkOwningHandle()
        return rocksdb_compactionoptions_universal_max_size_amplification_percent(native)
    }

    actual fun setCompressionSizePercent(percent: Int): CompactionOptionsUniversal {
        checkOwningHandle()
        rocksdb_compactionoptions_universal_set_compression_size_percent(native, percent)
        return this
    }

    actual fun compressionSizePercent(): Int {
        checkOwningHandle()
        return rocksdb_compactionoptions_universal_compression_size_percent(native)
    }

    actual fun setStopStyle(stopStyle: CompactionStopStyle): CompactionOptionsUniversal {
        checkOwningHandle()
        rocksdb_compactionoptions_universal_set_stop_style(native, stopStyle.value.toInt())
        return this
    }

    actual fun stopStyle(): CompactionStopStyle {
        checkOwningHandle()
        return getCompactionStopStyle(rocksdb_compactionoptions_universal_stop_style(native).toByte())
    }
}
