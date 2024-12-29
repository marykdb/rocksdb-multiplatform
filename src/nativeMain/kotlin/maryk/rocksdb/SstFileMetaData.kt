package maryk.rocksdb

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import maryk.toByteArray
import platform.posix.uint64_tVar

actual class SstFileMetaData(
    internal val native: CPointer<cnames.structs.rocksdb_sst_file_metadata_t>
) : RocksObject() {
    actual fun fileName() = rocksdb.rocksdb_sst_file_metadata_get_relative_filename(native)!!.toKString()

    actual fun path() = rocksdb.rocksdb_sst_file_metadata_get_directory(native)!!.toKString()

    actual fun size() = rocksdb.rocksdb_sst_file_metadata_get_size(native).toLong()

    actual fun smallestSeqno(): Long {
        TODO("NOT FOUND")
    }

    actual fun largestSeqno(): Long {
        TODO("NOT FOUND")
    }

    actual fun smallestKey(): ByteArray {
        val length = nativeHeap.alloc<uint64_tVar>()
        return rocksdb.rocksdb_sst_file_metadata_get_smallestkey(native, length.ptr)!!.toByteArray(length.value)
    }

    actual fun largestKey(): ByteArray {
        val length = nativeHeap.alloc<uint64_tVar>()
        return rocksdb.rocksdb_sst_file_metadata_get_largestkey(native, length.ptr)!!.toByteArray(length.value)
    }

    actual fun numReadsSampled(): Long {
        TODO("NOT FOUND")
    }

    actual fun beingCompacted(): Boolean {
        TODO("NOT FOUND")
    }

    actual fun numEntries(): Long {
        TODO("NOT FOUND")
    }

    actual fun numDeletions(): Long {
        TODO("NOT FOUND")
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb.rocksdb_sst_file_metadata_destroy(native)
            super.close()
        }
    }
}
