package maryk.rocksdb.util

import maryk.ByteBuffer
import maryk.rocksdb.AbstractComparator
import maryk.rocksdb.ComparatorOptions

/**
 * This is a Java implementation of a Comparator for Java int
 * keys.
 *
 * This comparator assumes keys are (at least) four bytes, so
 * the caller must guarantee that in accessing other APIs in
 * combination with this comparator.
 *
 * The performance of Comparators implemented in Java is always
 * less than their C++ counterparts due to the bridging overhead,
 * as such you likely don't want to use this apart from benchmarking
 * or testing.
 */
class IntComparator(copt: ComparatorOptions?) : AbstractComparator(copt) {
    override fun name(): String {
        return "rocksdb.java.IntComparator"
    }

    override fun compare(a: ByteBuffer, b: ByteBuffer): Int {
        return compareIntKeys(a, b)
    }

    /**
     * Compares integer keys
     * so that they are in ascending order
     *
     * @param a 4-bytes representing an integer key
     * @param b 4-bytes representing an integer key
     *
     * @return negative if a &lt; b, 0 if a == b, positive otherwise
     */
    private fun compareIntKeys(a: ByteBuffer, b: ByteBuffer): Int {
        val iA: Int = a.getInt()
        val iB: Int = b.getInt()

        // protect against int key calculation overflow
        val diff = iA.toLong() - iB
        val result: Int
        result = if (diff < Int.MIN_VALUE) {
            Int.MIN_VALUE
        } else if (diff > Int.MAX_VALUE) {
            Int.MAX_VALUE
        } else {
            diff.toInt()
        }
        return result
    }
}
