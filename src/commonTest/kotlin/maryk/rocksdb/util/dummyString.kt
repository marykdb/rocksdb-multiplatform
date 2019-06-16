package maryk.rocksdb.util

import kotlin.random.Random

/**
 * Generate a random string of bytes.
 * @param len the length of the string to generate.
 * @return the random string of bytes
 */
fun dummyString(len: Int) =
    ByteArray(len).apply {
        Random.nextBytes(this)
    }
