package maryk.rocksdb


/**
 * Bloom filter policy that uses a bloom filter with approximately
 * the specified number of bits per key.
 *
 * Note: if you are using a custom comparator that ignores some parts
 * of the keys being compared, you must not use this `BloomFilter`
 * and must provide your own FilterPolicy that also ignores the
 * corresponding parts of the keys. For example, if the comparator
 * ignores trailing spaces, it would be incorrect to use a
 * FilterPolicy (like `BloomFilter`) that does not ignore
 * trailing spaces in keys.
 */
expect class BloomFilter
    /**
     * BloomFilter constructor
     *
     * bits_per_key: bits per key in bloom filter. A good value for bits_per_key
     * is 10, which yields a filter with ~ 1% false positive rate.
     *
     * **default bits_per_key**: 10
     *
     * use_block_based_builder: use block based filter rather than full filter.
     * If you want to builder full filter, it needs to be set to false.
     *
     * **default mode: block based filter**
     *
     * Callers must delete the result after any database that is using the
     * result has been closed.
     *
     * @param bitsPerKey number of bits to use
     * @param useBlockBasedMode use block based mode or full filter mode
     */
    constructor(bitsPerKey: Int, useBlockBasedMode: Boolean)
: FilterPolicy {

    /**
     * BloomFilter constructor
     *
     * Callers must delete the result after any database that is using the
     * result has been closed.
     */
    constructor()

    /**
     * BloomFilter constructor
     *
     * bits_per_key: bits per key in bloom filter. A good value for bits_per_key
     * is 10, which yields a filter with ~ 1% false positive rate.
     *
     * Callers must delete the result after any database that is using the
     * result has been closed.
     *
     * @param bitsPerKey number of bits to use
     */
    constructor(bitsPerKey: Int)
}
