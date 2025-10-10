@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_writestallcondition_t
import cnames.structs.rocksdb_writestallinfo_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import maryk.toByteArray
import platform.posix.size_tVar

actual class WriteStallInfo internal constructor(
    private val columnFamilyNameValue: String,
    private val currentConditionValue: WriteStallCondition,
    private val previousConditionValue: WriteStallCondition
) {
    internal constructor(native: CPointer<rocksdb_writestallinfo_t>) : this(
        columnFamilyNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb.rocksdb_writestallinfo_cf_name(native, length.ptr)
                ?.toByteArray(length.value)
                ?.decodeToString()
                ?: ""
        },
        currentConditionValue = native.toCondition { rocksdb.rocksdb_writestallinfo_cur(it) },
        previousConditionValue = native.toCondition { rocksdb.rocksdb_writestallinfo_prev(it) }
    )

    actual fun columnFamilyName(): String = columnFamilyNameValue

    actual fun currentCondition(): WriteStallCondition = currentConditionValue

    actual fun previousCondition(): WriteStallCondition = previousConditionValue
}

private inline fun CPointer<rocksdb_writestallinfo_t>.toCondition(
    fetch: (CPointer<rocksdb_writestallinfo_t>) -> CPointer<rocksdb_writestallcondition_t>?
): WriteStallCondition {
    val pointer = fetch(this) ?: return WriteStallCondition.NORMAL
    val raw = pointer.reinterpret<UByteVar>().pointed.value
    return writeStallConditionFromValue(raw)
}
