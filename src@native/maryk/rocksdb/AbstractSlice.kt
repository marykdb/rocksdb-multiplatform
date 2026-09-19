package maryk.rocksdb

actual abstract class AbstractSlice<T> protected constructor() : RocksMutableObject() {
    abstract var data: T

    actual fun data(): T {
        return this.getData()
    }

    protected abstract fun getData(): T

    actual abstract fun removePrefix(n: Int)

    actual abstract fun clear()

    abstract operator fun get(offset: Int): Byte

    actual open fun size(): Int {
        throw NotImplementedError()
    }

    actual open fun empty(): Boolean {
        throw NotImplementedError()
    }

    actual open fun toString(hex: Boolean): String {
        throw NotImplementedError()
    }

    actual override fun toString(): String {
        return toString(false)
    }

    actual open fun compare(other: AbstractSlice<*>): Int {
        val minLength = minOf(this.size(), other.size())

        for (i in 0 until minLength) {
            val a = this[i].toInt() and 0xFF
            val b = other[i].toInt() and 0xFF
            if (a != b) {
                return a - b
            }
        }

        return this.size() - other.size()
    }

    actual override fun hashCode(): Int {
        return toString().hashCode()
    }

    actual override fun equals(other: Any?) = when (other) {
        null, !is AbstractSlice<*> -> false
        else -> {
            this.compare(other) == 0
        }
    }

    actual fun startsWith(prefix: AbstractSlice<*>): Boolean {
        if (prefix.size() == 0) return true

        if (prefix.size() > this.size()) return false

        for (i in 0 until prefix.size()) {
            if (this[i] != prefix[i]) {
                return false
            }
        }
        return true
    }

    override fun disposeInternal() {
    }
}
