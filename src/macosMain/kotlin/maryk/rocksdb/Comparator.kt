package maryk.rocksdb

import kotlinx.cinterop.CPointer
import maryk.rocksdb.ComparatorType.JAVA_COMPARATOR

actual abstract class Comparator actual constructor(copt: ComparatorOptions) : AbstractComparator<Slice>() {
    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>): CPointer<*> {
        return createNewComparator0(nativeParameterHandles[0])
    }

    override fun getComparatorType() = JAVA_COMPARATOR
}

private fun createNewComparator0(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
