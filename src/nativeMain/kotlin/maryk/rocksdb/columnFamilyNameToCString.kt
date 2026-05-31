package maryk.rocksdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.set

internal fun MemScope.columnFamilyNameToCString(name: ByteArray): CArrayPointer<ByteVar> {
    require(name.none { it == 0.toByte() }) { "column family names cannot contain NUL bytes" }

    val pointer = allocArray<ByteVar>(name.size + 1)
    for (index in name.indices) {
        pointer[index] = name[index]
    }
    pointer[name.size] = 0
    return pointer
}
