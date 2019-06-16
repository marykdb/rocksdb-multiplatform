package maryk.rocksdb

/**
 * EncodingType
 *
 * The value will determine how to encode keys
 * when writing to a new SST file.
 *
 *
 * This value will be stored
 * inside the SST file which will be used when reading from
 * the file, which makes it possible for users to choose
 * different encoding type when reopening a DB. Files with
 * different encoding types can co-exist in the same DB and
 * can be read.
 */
expect enum class EncodingType {
    /**
     * Always write full keys without any special encoding.
     */
    kPlain,
    /**
     *
     * Find opportunity to write the same prefix once for multiple rows.
     * In some cases, when a key follows a previous key with the same prefix,
     * instead of writing out the full key, it just writes out the size of the
     * shared prefix, as well as other bytes, to save some bytes.
     *
     *
     * When using this option, the user is required to use the same prefix
     * extractor to make sure the same prefix will be extracted from the same key.
     * The Name() value of the prefix extractor will be stored in the file. When
     * reopening the file, the name of the options.prefix_extractor given will be
     * bitwise compared to the prefix extractors stored in the file. An error
     * will be returned if the two don't match.
     */
    kPrefix;

    /**
     * Returns the byte value of the enumerations value
     *
     * @return byte representation
     */
    fun getValue(): Byte
}
