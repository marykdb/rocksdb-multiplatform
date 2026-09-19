package maryk.rocksdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.UByteVarOf
import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT
import platform.posix.size_t
import platform.posix.size_tVar

@OptIn(UnsafeNumber::class)
actual class RemoveEmptyValueCompactionFilter actual constructor() : AbstractCompactionFilter<Slice>() {
    override fun name(): String = "RemoveEmptyValueCompactionFilter"

    override fun filter(
        level: Int,
        key: CPointer<ByteVar>?,
        keyLength: size_t,
        existingValue: CPointer<ByteVar>?,
        valueLength: size_t,
        newValue: CPointer<CPointerVarOf<CPointer<ByteVar>>>?,
        newValueLength: CPointer<size_tVar>?,
        valueChanged: CPointer<UByteVarOf<UByte>>?,
    ): Boolean = valueLength == 0.asSizeT()
}
