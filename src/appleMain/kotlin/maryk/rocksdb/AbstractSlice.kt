package maryk.rocksdb

import kotlinx.cinterop.Arena
import kotlinx.cinterop.AutofreeScope
import rocksdb.RocksDBSlice

actual abstract class AbstractSlice<T> protected constructor(
    nativeCreator: (scope: AutofreeScope) -> RocksDBSlice
) : RocksMutableObject() {
    internal val arena = Arena()
    val native = nativeCreator(arena)

    actual fun data(): T {
        return this.getData()
    }

    protected abstract fun getData(): T

    actual abstract fun removePrefix(n: Int)

    actual abstract fun clear()

    actual fun size(): Int {
        return native.size().toInt()
    }

    actual fun empty(): Boolean {
        return native.empty()
    }

    actual fun toString(hex: Boolean): String {
        return native.toString(hex)!!
    }

    actual override fun toString(): String {
        return native.toString(false)!!
    }

    actual fun compare(other: AbstractSlice<*>): Int {
        return native.compare(other.native)
    }

    actual override fun hashCode(): Int {
        return toString().hashCode()
    }

    actual override fun equals(other: Any?) = when (other) {
        null, !is AbstractSlice<*> -> false
        else -> {
            native.compare(other.native) == 0
        }
    }

    actual fun startsWith(prefix: AbstractSlice<*>): Boolean {
        return native.startsWith(prefix.native)
    }

    override fun disposeInternal() {
        arena.clear()
    }
}
