package maryk.rocksdb.util

import maryk.ByteBuffer

/**
 * Compares the first `count` bytes of two areas of memory.  Returns
 * zero if they are the same, a value less than zero if `x` is
 * lexically less than `y`, or a value greater than zero if `x`
 * is lexically greater than `y`.  Note that lexical order is determined
 * as if comparing unsigned char arrays.
 *
 * Similar to [memcmp.c](https://github.com/gcc-mirror/gcc/blob/master/libiberty/memcmp.c).
 *
 * @param x the first value to compare with
 * @param y the second value to compare against
 * @param count the number of bytes to compare
 *
 * @return the result of the comparison
 */
fun memCompare(
    x: ByteBuffer, y: ByteBuffer,
    count: Int
): Int {
    for (idx in 0 until count) {
        val aa: Int = x.get(idx).toInt() and 0xff
        val bb: Int = y.get(idx).toInt() and 0xff
        if (aa != bb) {
            return aa - bb
        }
    }
    return 0
}
