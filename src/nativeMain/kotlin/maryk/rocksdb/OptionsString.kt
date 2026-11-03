@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.sizeTToLong
import maryk.toByteArray
import platform.posix.size_tVar

/** Copy a borrowed RocksDB string using its explicit byte length. */
internal fun readOptionsString(read: (CPointer<size_tVar>) -> CPointer<ByteVar>?): String = memScoped {
    val length = alloc<size_tVar>()
    val value = read(length.ptr)
    if (value == null) {
        check(sizeTToLong(length.value) == 0L) { "RocksDB returned a null nonempty option string" }
        ""
    } else {
        value.toByteArray(length.value).decodeToString()
    }
}
