package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class RemoveEmptyValueCompactionFilter actual constructor() : AbstractCompactionFilter<Slice>(createNewRemoveEmptyValueCompactionFilter0())

fun createNewRemoveEmptyValueCompactionFilter0(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
