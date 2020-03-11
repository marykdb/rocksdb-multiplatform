package maryk.rocksdb.util

import maryk.ByteBuffer
import maryk.rocksdb.AbstractComparator
import maryk.rocksdb.ComparatorOptions
import kotlin.math.min

/**
 * This is a common implementation of the C++
 * equivalent ReverseBytewiseComparatorImpl using [ByteBuffer]
 *
 * The performance of Comparators implemented in Java is always
 * less than their C++ counterparts due to the bridging overhead,
 * as such you likely don't want to use this apart from benchmarking
 * and you most likely instead wanted
 * [maryk.rocksdb.BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR]
 */
class ReverseBytewiseComparator(copt: ComparatorOptions?) : AbstractComparator(copt) {
    override fun name() = "rocksdb.java.ReverseBytewiseComparator"

    override fun compare(a: ByteBuffer, b: ByteBuffer): Int {
        return -BytewiseComparator.compare(a, b)
    }

    override fun findShortestSeparator(
        start: ByteBuffer,
        limit: ByteBuffer
    ) {
        // Find length of common prefix
        val minLength: Int = min(start.remaining(), limit.remaining())
        var diffIndex = 0
        while (diffIndex < minLength &&
            start.get(diffIndex) == limit.get(diffIndex)
        ) {
            diffIndex++
        }
        require(diffIndex <= minLength)
        if (diffIndex == minLength) {
            // Do not shorten if one string is a prefix of the other
            //
            // We could handle cases like:
            //     V
            // A A 2 X Y
            // A A 2
            // in a similar way as BytewiseComparator::FindShortestSeparator().
            // We keep it simple by not implementing it. We can come back to it
            // later when needed.
        } else {
            val startByte: Int = start.get(diffIndex).toInt() and 0xff
            val limitByte: Int = limit.get(diffIndex).toInt() and 0xff
            if (startByte > limitByte && diffIndex < start.remaining() - 1) {
                // Case like
                //     V
                // A A 3 A A
                // A A 1 B B
                //
                // or
                //     v
                // A A 2 A A
                // A A 1 B B
                // In this case "AA2" will be good.
                start.limit(diffIndex + 1)
                require(BytewiseComparator.compare(start.duplicate(), limit.duplicate()) > 0)
            }
        }
    }
}
