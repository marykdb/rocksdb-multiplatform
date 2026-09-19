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
import maryk.toCheckedLong
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
            requireNotNull(rocksdb.rocksdb_memtableinfo_cf_name(native, length.ptr)) {
                "RocksDB returned null memtable column family name"
            }.toByteArray(length.value).decodeToString()
        },
        firstSeqnoValue = rocksdb.rocksdb_memtableinfo_first_seqno(native).toCheckedLong("memtable first sequence number"),
        earliestSeqnoValue = rocksdb.rocksdb_memtableinfo_earliest_seqno(native).toCheckedLong("memtable earliest sequence number"),
        numEntriesValue = rocksdb.rocksdb_memtableinfo_num_entries(native).toCheckedLong("memtable entry count"),
        numDeletesValue = rocksdb.rocksdb_memtableinfo_num_deletes(native).toCheckedLong("memtable delete count")
    )

    actual fun columnFamilyName(): String = columnFamilyNameValue

    actual fun firstSeqno(): Long = firstSeqnoValue

    actual fun earliestSeqno(): Long = earliestSeqnoValue

    actual fun numEntries(): Long = numEntriesValue

    actual fun numDeletes(): Long = numDeletesValue
}
