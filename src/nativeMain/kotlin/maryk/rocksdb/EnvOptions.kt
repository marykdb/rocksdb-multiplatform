@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_envoptions_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.convert
import maryk.asSizeT
import maryk.asUInt64
import maryk.toBoolean
import maryk.toCheckedLong
import maryk.toUByte
import rocksdb.rocksdb_envoptions_create
import rocksdb.rocksdb_envoptions_destroy

actual class EnvOptions actual constructor() : RocksObject() {
    internal val native: CPointer<rocksdb_envoptions_t> =
        requireNotNull(rocksdb_envoptions_create()) { "Unable to allocate RocksDB environment options" }

    private var rateLimiter: RateLimiter? = null

    override fun close() {
        if (tryClose()) {
            rocksdb_envoptions_destroy(native)
            super.close()
        }
    }
    actual fun setUseMmapReads(value: Boolean): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_use_mmap_reads(native, value.toUByte())
        return this
    }

    actual fun useMmapReads(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_use_mmap_reads(native).toBoolean()
    }

    actual fun setUseMmapWrites(value: Boolean): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_use_mmap_writes(native, value.toUByte())
        return this
    }

    actual fun useMmapWrites(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_use_mmap_writes(native).toBoolean()
    }

    actual fun setUseDirectReads(value: Boolean): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_use_direct_reads(native, value.toUByte())
        return this
    }

    actual fun useDirectReads(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_use_direct_reads(native).toBoolean()
    }

    actual fun setUseDirectWrites(value: Boolean): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_use_direct_writes(native, value.toUByte())
        return this
    }

    actual fun useDirectWrites(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_use_direct_writes(native).toBoolean()
    }

    actual fun setAllowFallocate(value: Boolean): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_allow_fallocate(native, value.toUByte())
        return this
    }

    actual fun allowFallocate(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_allow_fallocate(native).toBoolean()
    }

    actual fun setSetFdCloexec(value: Boolean): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_fd_cloexec(native, value.toUByte())
        return this
    }

    actual fun setFdCloexec(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_fd_cloexec(native).toBoolean()
    }

    actual fun setBytesPerSync(value: Long): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_bytes_per_sync(native, value.asUInt64())
        return this
    }

    actual fun bytesPerSync(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_bytes_per_sync(native).toCheckedLong("environment bytes_per_sync")
    }

    actual fun setFallocateWithKeepSize(value: Boolean): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_fallocate_with_keep_size(native, value.toUByte())
        return this
    }

    actual fun fallocateWithKeepSize(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_fallocate_with_keep_size(native).toBoolean()
    }

    actual fun setCompactionReadaheadSize(value: Long): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_compaction_readahead_size(native, value.asSizeT())
        return this
    }

    actual fun compactionReadaheadSize(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_compaction_readahead_size(native).convert<ULong>().toCheckedLong("environment compaction_readahead_size")
    }

    actual fun setWritableFileMaxBufferSize(value: Long): EnvOptions {
        checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_writable_file_max_buffer_size(native, value.asSizeT())
        return this
    }

    actual fun writableFileMaxBufferSize(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_envoptions_get_writable_file_max_buffer_size(native).convert<ULong>().toCheckedLong("environment writable_file_max_buffer_size")
    }

    actual fun setRateLimiter(rateLimiter: RateLimiter): EnvOptions {
        checkOwningHandle()
        rateLimiter.checkOwningHandle()
        rocksdb.rocksdb_envoptions_set_rate_limiter(native, rateLimiter.native)
        this.rateLimiter = rateLimiter
        return this
    }
}
