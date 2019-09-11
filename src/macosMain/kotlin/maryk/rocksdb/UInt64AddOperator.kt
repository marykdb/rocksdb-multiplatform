package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class UInt64AddOperator actual constructor() : MergeOperator(newSharedUInt64AddOperator())

fun newSharedUInt64AddOperator(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
