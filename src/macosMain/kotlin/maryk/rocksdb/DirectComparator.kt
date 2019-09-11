package maryk.rocksdb

import kotlinx.cinterop.CPointer
import maryk.rocksdb.ComparatorType.JAVA_DIRECT_COMPARATOR

actual abstract class DirectComparator actual constructor(copt: ComparatorOptions) :
    AbstractComparator<DirectSlice>() {

    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>): CPointer<*> {
        return createNewDirectComparator0(nativeParameterHandles[0])
    }

    override fun getComparatorType() = JAVA_DIRECT_COMPARATOR
}

private fun createNewDirectComparator0(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
