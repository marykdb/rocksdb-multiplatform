// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).

package maryk.rocksdb

import maryk.encodeToByteArray
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Abstract tests for both Comparator and DirectComparator */
abstract class AbstractComparatorTest {
    /**
     * Get a comparator which will expect Integer keys
     * and determine an ascending order
     *
     * @return An integer ascending order key comparator
     */
    abstract val ascendingIntKeyComparator: AbstractComparator

    /**
     * Test which stores random keys into the database
     * using an @see getAscendingIntKeyComparator
     * it then checks that these keys are read back in
     * ascending order
     *
     * @param db_path A path where we can store database
     * files temporarily
     *
     * @throws RocksDBException
     */
    fun testRoundtrip(db_path: String) {
        ascendingIntKeyComparator.use { comparator ->
            Options().apply {
                setCreateIfMissing(true)
                setComparator(comparator)
            }.use { opt ->
                // store 10,000 random integer keys
                val iterations = 10000
                openRocksDB(opt, db_path).use { db ->
                    val value = "value".encodeToByteArray()
                    var i = 0
                    while (i < iterations) {
                        val key = Random.nextBytes(4)
                        // does key already exist (avoid duplicates)
                        if (i > 0 && db[key] != null) {
                            i-- // generate a different key
                        } else {
                            db.put(key, value)
                        }
                        i++
                    }
                }

                // re-open db and read from start to end
                // integer keys should be in ascending
                // order as defined by SimpleIntComparator
                openRocksDB(opt, db_path).use { db ->
                    db.newIterator().use {
                        var lastKey = Int.MIN_VALUE
                        var count = 0
                        it.seekToFirst()
                        while (it.isValid()) {
                            val thisKey = byteToInt(it.key())
                            assertTrue(thisKey > lastKey)
                            lastKey = thisKey
                            count++
                            it.next()
                        }
                        assertEquals(iterations, count)
                    }
                }
            }
        }
    }

    /**
     * Test which stores random keys into a column family
     * in the database
     * using an @see getAscendingIntKeyComparator
     * it then checks that these keys are read back in
     * ascending order
     *
     * @param db_path A path where we can store database
     * files temporarily
     */
    fun testRoundtripCf(db_path: String) {
        ascendingIntKeyComparator.use { comparator ->
            val cfDescriptors = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily),
                ColumnFamilyDescriptor(
                    "new_cf".encodeToByteArray(),
                    ColumnFamilyOptions().setComparator(comparator)
                )
            )

            val cfHandles = mutableListOf<ColumnFamilyHandle>()

            DBOptions().apply {
                setCreateIfMissing(true)
                setCreateMissingColumnFamilies(true)
            }.use { opt ->
                // store 10,000 random integer keys
                val iterations = 10000

                openRocksDB(
                    opt, db_path,
                    cfDescriptors,
                    cfHandles
                ).use { db ->
                    try {
                        assertEquals(2, cfDescriptors.size)
                        assertEquals(2, cfHandles.size)

                        val value = "value".encodeToByteArray()
                        var i = 0
                        while (i < iterations) {
                            val key = Random.nextBytes(4)
                            if (i > 0 && db.get(cfHandles[1], key) != null) {
                                // does key already exist (avoid duplicates)
                                i-- // generate a different key
                            } else {
                                db.put(cfHandles[1], key, value)
                            }
                            i++
                        }
                    } finally {
                        for (handle in cfHandles) {
                            handle.close()
                        }
                    }
                    cfHandles.clear()
                }

                // re-open db and read from start to end
                // integer keys should be in ascending
                // order as defined by SimpleIntComparator
                openRocksDB(
                    opt, db_path,
                    cfDescriptors, cfHandles
                ).use { db ->
                    db.newIterator(cfHandles[1]).use {
                        try {
                            assertEquals(2, cfDescriptors.size)
                            assertEquals(2, cfHandles.size)

                            it.seekToFirst()
                            var lastKey = Int.MIN_VALUE
                            var count = 0
                            it.seekToFirst()
                            while (it.isValid()) {
                                val thisKey = byteToInt(it.key())
                                assertTrue(thisKey > lastKey)
                                lastKey = thisKey
                                count++
                                it.next()
                            }

                            assertEquals(iterations, count)
                        } finally {
                            for (handle in cfHandles) {
                                handle.close()
                            }
                        }
                        cfHandles.clear()
                    }
                }
            }
        }
    }

    /**
     * Compares integer keys
     * so that they are in ascending order
     *
     * @param a 4-bytes representing an integer key
     * @param b 4-bytes representing an integer key
     *
     * @return negative if a < b, 0 if a == b, positive otherwise
     */
    protected fun compareIntKeys(a: ByteArray, b: ByteArray): Int {
        val iA = byteToInt(a)
        val iB = byteToInt(b)

        // protect against int key calculation overflow
        val diff = iA.toDouble() - iB
        return when {
            diff < Int.MIN_VALUE -> Int.MIN_VALUE
            diff > Int.MAX_VALUE -> Int.MAX_VALUE
            else -> diff.toInt()
        }
    }
}
