package maryk.rocksdb

/** Simple instance reference wrapper. */
expect class Holder<T> {
    /** Constructs a new Holder with null instance. */
    constructor()

    /**
     * Constructs a new Holder.
     * [value] the instance or null
     */
    constructor(value: T?)

    /** Get the instance reference. */
    fun getValue(): T?

    /** Set the instance reference. */
    fun setValue(value: T?)
}
