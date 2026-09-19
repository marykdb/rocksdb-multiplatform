package maryk.rocksdb

/** Simple instance reference wrapper. */
actual class Holder<T> {
    private var value: T?

    /** Constructs a new Holder with null instance. */
    actual constructor() {
        this.value = null
    }

    /**
     * Constructs a new Holder.
     * [value] the instance or null
     */
    actual constructor(value: T?) {
        this.value = value
    }

    /** Get the instance reference. */
    actual fun getValue() = this.value

    /** Set the instance reference. */
    actual fun setValue(value: T?) {
        this.value = value
    }
}
