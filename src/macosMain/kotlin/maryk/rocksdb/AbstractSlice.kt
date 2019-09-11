package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractSlice<T> : RocksMutableObject {
    protected constructor() : super(null, false)

    protected constructor(nativeHandle: CPointer<*>): super(nativeHandle, true)

    actual fun data(): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual abstract fun removePrefix(n: Int)

    actual abstract fun clear()

    actual fun size(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun empty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun toString(hex: Boolean): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun toString(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compare(other: AbstractSlice<*>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun hashCode(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun equals(other: Any?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun startsWith(prefix: AbstractSlice<*>?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disposeInternal(handle: CPointer<*>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

internal fun createNewSliceFromString(str: String): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
