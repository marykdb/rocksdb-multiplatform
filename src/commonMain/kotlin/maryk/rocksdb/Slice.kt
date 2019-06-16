package maryk.rocksdb

/**
 * Base class for slices which will receive
 * byte[] based access to the underlying data.
 *
 * byte[] backed slices typically perform better with
 * small keys and values. When using larger keys and
 * values consider using [org.rocksdb.DirectSlice]
 */
expect class Slice : AbstractSlice<ByteArray> {
    /**
     * Constructs a slice where the data is taken from
     * a String.
     *
     * @param str String value.
     */
    constructor(str: String)

    /**
     * Constructs a slice where the data is a copy of
     * the byte array from a specific offset.
     *
     * @param data byte array.
     * @param offset offset within the byte array.
     */
    constructor(data: ByteArray, offset: Int)

    /**
     * Constructs a slice where the data is a copy of
     * the byte array.
     *
     * @param data byte array.
     */
    constructor(data: ByteArray)
}
