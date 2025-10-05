@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_compactionoptions_fifo_t
import cnames.structs.rocksdb_compactionoptions_t
import cnames.structs.rocksdb_compactionoptions_universal_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
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
    actual constructor() : this(rocksdb_compactionoptions_create()!!)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_compactionoptions_destroy(native)
            super.close()
        }
    }

    actual fun compression(): CompressionType =
        getCompressionType(rocksdb_compactionoptions_get_compression(native).toByte())

    actual fun setCompression(compression: CompressionType): CompactionOptions {
        rocksdb_compactionoptions_set_compression(native, compression.value.toInt())
        return this
    }

    actual fun outputFileSizeLimit(): Long =
        rocksdb_compactionoptions_get_output_file_size_limit(native).toLong()

    actual fun setOutputFileSizeLimit(limit: Long): CompactionOptions {
        rocksdb_compactionoptions_set_output_file_size_limit(native, limit.toULong())
        return this
    }

    actual fun maxSubcompactions(): Int =
        rocksdb_compactionoptions_get_max_subcompactions(native).toInt()

    actual fun setMaxSubcompactions(count: Int): CompactionOptions {
        rocksdb_compactionoptions_set_max_subcompactions(native, count.toUInt())
        return this
    }
}

actual class CompactionOptionsFIFO internal constructor(
    internal val native: CPointer<rocksdb_compactionoptions_fifo_t>
) : RocksObject() {
    actual constructor() : this(rocksdb_compactionoptions_fifo_create()!!)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_compactionoptions_fifo_destroy(native)
            super.close()
        }
    }

    actual fun setMaxTableFilesSize(size: Long): CompactionOptionsFIFO {
        rocksdb_compactionoptions_fifo_set_max_table_files_size(native, size.toULong())
        return this
    }

    actual fun maxTableFilesSize(): Long =
        rocksdb_compactionoptions_fifo_max_table_files_size(native).toLong()

    actual fun setAllowCompaction(allow: Boolean): CompactionOptionsFIFO {
        rocksdb_compactionoptions_fifo_set_allow_compaction(native, allow.toUByte())
        return this
    }

    actual fun allowCompaction(): Boolean =
        rocksdb_compactionoptions_fifo_allow_compaction(native).toBoolean()
}

actual class CompactionOptionsUniversal internal constructor(
    internal val native: CPointer<rocksdb_compactionoptions_universal_t>
) : RocksObject() {
    actual constructor() : this(rocksdb_compactionoptions_universal_create()!!)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_compactionoptions_universal_destroy(native)
            super.close()
        }
    }

    actual fun setSizeRatio(sizeRatio: Int): CompactionOptionsUniversal {
        rocksdb_compactionoptions_universal_set_size_ratio(native, sizeRatio)
        return this
    }

    actual fun sizeRatio(): Int = rocksdb_compactionoptions_universal_size_ratio(native)

    actual fun setMinMergeWidth(width: Int): CompactionOptionsUniversal {
        rocksdb_compactionoptions_universal_set_min_merge_width(native, width)
        return this
    }

    actual fun minMergeWidth(): Int =
        rocksdb_compactionoptions_universal_min_merge_width(native)

    actual fun setMaxMergeWidth(width: Int): CompactionOptionsUniversal {
        rocksdb_compactionoptions_universal_set_max_merge_width(native, width)
        return this
    }

    actual fun maxMergeWidth(): Int =
        rocksdb_compactionoptions_universal_max_merge_width(native)

    actual fun setMaxSizeAmplificationPercent(percent: Int): CompactionOptionsUniversal {
        rocksdb_compactionoptions_universal_set_max_size_amplification_percent(native, percent)
        return this
    }

    actual fun maxSizeAmplificationPercent(): Int =
        rocksdb_compactionoptions_universal_max_size_amplification_percent(native)

    actual fun setCompressionSizePercent(percent: Int): CompactionOptionsUniversal {
        rocksdb_compactionoptions_universal_set_compression_size_percent(native, percent)
        return this
    }

    actual fun compressionSizePercent(): Int =
        rocksdb_compactionoptions_universal_compression_size_percent(native)

    actual fun setStopStyle(stopStyle: CompactionStopStyle): CompactionOptionsUniversal {
        rocksdb_compactionoptions_universal_set_stop_style(native, stopStyle.value.toInt())
        return this
    }

    actual fun stopStyle(): CompactionStopStyle =
        getCompactionStopStyle(rocksdb_compactionoptions_universal_stop_style(native).toByte())
}
