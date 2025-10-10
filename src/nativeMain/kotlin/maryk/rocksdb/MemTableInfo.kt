@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_memtableinfo_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toByteArray
import platform.posix.size_tVar

actual class MemTableInfo internal constructor(
    private val columnFamilyNameValue: String,
    private val firstSeqnoValue: Long,
    private val earliestSeqnoValue: Long,
    private val numEntriesValue: Long,
    private val numDeletesValue: Long
) {
    internal constructor(native: CPointer<rocksdb_memtableinfo_t>) : this(
        columnFamilyNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb.rocksdb_memtableinfo_cf_name(native, length.ptr)
                ?.toByteArray(length.value)
                ?.decodeToString()
                ?: ""
        },
        firstSeqnoValue = rocksdb.rocksdb_memtableinfo_first_seqno(native).toLong(),
        earliestSeqnoValue = rocksdb.rocksdb_memtableinfo_earliest_seqno(native).toLong(),
        numEntriesValue = rocksdb.rocksdb_memtableinfo_num_entries(native).toLong(),
        numDeletesValue = rocksdb.rocksdb_memtableinfo_num_deletes(native).toLong()
    )

    actual fun columnFamilyName(): String = columnFamilyNameValue

    actual fun firstSeqno(): Long = firstSeqnoValue

    actual fun earliestSeqno(): Long = earliestSeqnoValue

    actual fun numEntries(): Long = numEntriesValue

    actual fun numDeletes(): Long = numDeletesValue
}
