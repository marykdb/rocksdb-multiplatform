package maryk.rocksdb.util

import maryk.ByteBuffer
import maryk.rocksdb.AbstractComparator
import maryk.rocksdb.ComparatorOptions
import kotlin.math.min
import maryk.limit

/**
 * This is a Common implementation of the C++
 * equivalent BytewiseComparatorImpl using [ByteBuffer]
 *
 * The performance of Comparators implemented in Java is always
 * less than their C++ counterparts due to the bridging overhead,
 * as such you likely don't want to use this apart from benchmarking
 * and you most likely instead wanted
 * [maryk.rocksdb.BuiltinComparator.BYTEWISE_COMPARATOR]
 */
class BytewiseComparator(copt: ComparatorOptions?) : AbstractComparator(copt) {
    override fun name() = "rocksdb.java.BytewiseComparator"

    override fun compare(a: ByteBuffer, b: ByteBuffer): Int = compare(a, b)

    override fun findShortestSeparator(
        start: ByteBuffer,
        limit: ByteBuffer
    ) {
        // Find length of common prefix
        val minLength: Int = min(start.remaining(), limit.remaining())
        var diffIndex = 0
        while (diffIndex < minLength &&
            start[diffIndex] == limit[diffIndex]
        ) {
            diffIndex++
        }
        if (diffIndex >= minLength) {
            // Do not shorten if one string is a prefix of the other
        } else {
            val startByte: Int = start[diffIndex].toInt() and 0xff
            val limitByte: Int = limit[diffIndex].toInt() and 0xff
            if (startByte >= limitByte) {
                // Cannot shorten since limit is smaller than start or start is
                // already the shortest possible.
                return
            }
            require(startByte < limitByte)
            if (diffIndex < limit.remaining() - 1 || startByte + 1 < limitByte) {
                start.put(diffIndex, ((start[diffIndex].toInt() and 0xff) + 1).toByte())
                start.limit(diffIndex + 1)
            } else {
                //     v
                // A A 1 A A A
                // A A 2
                //
                // Incrementing the current byte will make start bigger than limit, we
                // will skip this byte, and find the first non 0xFF byte in start and
                // increment it.
                diffIndex++
                while (diffIndex < start.remaining()) {
                    // Keep moving until we find the first non 0xFF byte to
                    // increment it
                    if (start[diffIndex].toInt() and 0xff <
                        0xff
                    ) {
                        start.put(diffIndex, ((start[diffIndex].toInt() and 0xff) + 1).toByte())
                        start.limit(diffIndex + 1)
                        break
                    }
                    diffIndex++
                }
            }
            require(compare(start, limit) < 0)
        }
    }

    override fun findShortSuccessor(key: ByteBuffer) {
        // Find first character that can be incremented
        val n: Int = key.remaining()
        for (i in 0 until n) {
            val byt: Int = key[i].toInt() and 0xff
            if (byt != 0xff) {
                key.put(i, (byt + 1).toByte())
                key.limit(i + 1)
                return
            }
        }
        // *key is a run of 0xffs.  Leave it alone.
    }

    companion object {
        fun compare(a: ByteBuffer?, b: ByteBuffer?): Int {
            requireNotNull(a)
            requireNotNull(b)
            val minLen: Int = if (a.remaining() < b.remaining()) a.remaining() else b.remaining()
            var r: Int = memCompare(a, b, minLen)
            if (r == 0) {
                if (a.remaining() < b.remaining()) {
                    r = -1
                } else if (a.remaining() > b.remaining()) {
                    r = +1
                }
            }
            return r
        }
    }
}
