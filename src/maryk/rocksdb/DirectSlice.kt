package maryk.rocksdb

import maryk.ByteBuffer

expect class DirectSlice: AbstractSlice<ByteBuffer> {
    /**
     * Constructs a slice
     * where the data is taken from
     * a String.
     *
     * @param str The string
     */
    constructor(str: String)

    /**
     * Constructs a slice where the data is
     * read from the provided
     * ByteBuffer up to a certain length
     *
     * @param data The buffer containing the data
     * @param length The length of the data to use for the slice
     */
    constructor(data: ByteBuffer, length: Int)

    /**
     * Constructs a slice where the data is
     * read from the provided
     * ByteBuffer
     *
     * @param data The bugger containing the data
     */
    constructor(data: ByteBuffer)

    /**
     * Retrieves the byte at a specific offset
     * from the underlying data
     *
     * @param offset The (zero-based) offset of the byte to retrieve
     *
     * @return the requested byte
     */
    operator fun get(offset: Int): Byte

    override fun clear()

    override fun removePrefix(n: Int)
}

expect val DirectSliceNone: DirectSlice
