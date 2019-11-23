package maryk.rocksdb.util

import maryk.rocksdb.ComparatorOptions
import maryk.rocksdb.DirectComparator
import maryk.rocksdb.DirectSlice
import maryk.wrapByteBuffer
import kotlin.math.min

actual class DirectBytewiseComparator actual constructor(copt: ComparatorOptions) : DirectComparator(copt) {
    actual override fun name() = "maryk.rocksdb.kotlin.DirectBytewiseComparator"

    actual override fun compare(a: DirectSlice, b: DirectSlice) = a.compare(b)

    actual override fun findShortestSeparator(start: String, limit: DirectSlice): String? {
        val startBytes = start.encodeToByteArray()

        // Find length of common prefix
        val minLength: Int = min(startBytes.size, limit.size())
        var diffIndex = 0
        while (diffIndex < minLength && startBytes[diffIndex] == limit[diffIndex]) {
            diffIndex++
        }

        if (diffIndex >= minLength) {
            // Do not shorten if one string is a prefix of the other
            return null
        }

        val diffByte = startBytes[diffIndex]
        if (diffByte < 0xff && diffByte + 1 < limit[diffIndex]) {

            val shortest = startBytes.copyOfRange(0, diffIndex + 1)
            shortest[diffIndex]++
            assert(wrapByteBuffer(shortest).compareTo(limit.data()) < 0)
            return shortest.decodeToString()
        }

        return null
    }

    actual override fun findShortSuccessor(key: String): String? {
        val keyBytes = key.encodeToByteArray()

        // Find first character that can be incremented
        val n = keyBytes.size
        for (i in 0 until n) {
            val byt = keyBytes[i]
            if (byt.toInt() != 0xff) {
                val shortSuccessor = keyBytes.copyOfRange(0, i + 1)
                shortSuccessor[i]++
                return shortSuccessor.decodeToString()
            }
        }
        // *key is a run of 0xffs.  Leave it alone.
        return null
    }
}
