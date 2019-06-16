package maryk.rocksdb

import maryk.assertContains
import maryk.assertContainsExactly
import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.prepend0
import maryk.rocksdb.util.createTestDBFolder
import kotlin.jvm.Volatile
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlin.test.fail

class RocksDBTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("RocksDBTest")

    @Test
    fun open() {
        openRocksDB(createTestFolder()).use { db ->
            assertNotNull(db)
        }
    }

    @Test
    fun open_opt() {
        Options().setCreateIfMissing(true).use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                assertNotNull(db)
            }
        }
    }

    @Test
    fun openWhenOpen() {
        val dbPath = createTestFolder()

        openRocksDB(dbPath).use {
            try {
                openRocksDB(dbPath).use {
                    fail("Should have thrown an exception when opening the same db twice")
                }
            } catch (e: RocksDBException) {
                assertEquals(StatusCode.IOError, e.getStatus()?.getCode())
                assertEquals(StatusSubCode.None, e.getStatus()?.getSubCode())
                assertTrue(e.getStatus()?.getState()!!.contains("lock "))
            }
        }
    }

    @Test
    fun createColumnFamily() {
        val col1Name = "col1".encodeToByteArray()

        val testFolder = createTestFolder()
        openRocksDB(testFolder).use { db ->
            ColumnFamilyOptions().use { cfOpts ->
                db.createColumnFamily(
                    ColumnFamilyDescriptor(col1Name, cfOpts)
                ).use { col1 ->
                    assertNotNull(col1)
                    assertContentEquals(col1Name, col1.getName())
                }
            }
        }

        val cfHandles = mutableListOf<ColumnFamilyHandle>()
        openRocksDB(
            testFolder,
            listOf(
                ColumnFamilyDescriptor(defaultColumnFamily),
                ColumnFamilyDescriptor(col1Name)
            ),
            cfHandles
        ).use {
            try {
                assertEquals(2, cfHandles.size)
                assertNotNull(cfHandles[1])
                assertContentEquals(col1Name, cfHandles[1].getName())
            } finally {
                for (cfHandle in cfHandles) {
                    cfHandle.close()
                }
            }
        }
    }

    @Test
    fun createColumnFamilies() {
        val col1Name = "col1".encodeToByteArray()
        val col2Name = "col2".encodeToByteArray()

        val testFolder = createTestFolder()
        var cfHandles: List<ColumnFamilyHandle>
        openRocksDB(testFolder).use { db ->
            ColumnFamilyOptions().use { cfOpts ->
                cfHandles = db.createColumnFamilies(cfOpts, listOf(col1Name, col2Name))
                try {
                    assertNotNull(cfHandles)
                    assertEquals(2, cfHandles.size)
                    assertContentEquals(col1Name, cfHandles[0].getName())
                    assertContentEquals(col2Name, cfHandles[1].getName())
                } finally {
                    for (cfHandle in cfHandles) {
                        cfHandle.close()
                    }
                }
            }
        }

        cfHandles = mutableListOf()
        openRocksDB(
            testFolder,
            listOf(
                ColumnFamilyDescriptor(defaultColumnFamily),
                ColumnFamilyDescriptor(col1Name),
                ColumnFamilyDescriptor(col2Name)
            ),
            cfHandles
        ).use {
            try {
                assertEquals(3, cfHandles.size)
                assertNotNull(cfHandles[1])
                assertContentEquals(col1Name, cfHandles[1].getName())
                assertNotNull(cfHandles[2])
                assertContentEquals(col2Name, cfHandles[2].getName())
            } finally {
                for (cfHandle in cfHandles) {
                    cfHandle.close()
                }
            }
        }
    }

    @Test
    fun createColumnFamiliesFromDescriptors() {
        val col1Name = "col1".encodeToByteArray()
        val col2Name = "col2".encodeToByteArray()

        val testFolder = createTestFolder()
        var cfHandles: List<ColumnFamilyHandle>
        openRocksDB(testFolder).use { db ->
            ColumnFamilyOptions().use { cfOpts ->
                cfHandles = db.createColumnFamilies(
                    listOf(
                        ColumnFamilyDescriptor(col1Name, cfOpts),
                        ColumnFamilyDescriptor(col2Name, cfOpts)
                    )
                )
                try {
                    assertNotNull(cfHandles)
                    assertEquals(2, cfHandles.size)
                    assertContentEquals(col1Name, cfHandles[0].getName())
                    assertContentEquals(col2Name, cfHandles[1].getName())
                } finally {
                    for (cfHandle in cfHandles) {
                        cfHandle.close()
                    }
                }
            }
        }

        cfHandles = mutableListOf()
        openRocksDB(
            testFolder,
            listOf(
                ColumnFamilyDescriptor(defaultColumnFamily),
                ColumnFamilyDescriptor(col1Name),
                ColumnFamilyDescriptor(col2Name)
            ),
            cfHandles
        ).use {
            try {
                assertEquals(3, cfHandles.size)
                assertNotNull(cfHandles[1])
                assertContentEquals(col1Name, cfHandles[1].getName())
                assertNotNull(cfHandles[2])
                assertContentEquals(col2Name, cfHandles[2].getName())
            } finally {
                for (cfHandle in cfHandles) {
                    cfHandle.close()
                }
            }
        }
    }

    @Test
    fun put() {
        openRocksDB(createTestFolder()).use { db ->
            WriteOptions().use { opt ->
                db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
                db.put(opt, "key2".encodeToByteArray(), "12345678".encodeToByteArray())
                assertContentEquals("value".encodeToByteArray(), db["key1".encodeToByteArray()]!!)
                assertContentEquals("12345678".encodeToByteArray(), db["key2".encodeToByteArray()]!!)

                // put
                val key3 = sliceSegment("key3")
                val key4 = sliceSegment("key4")
                val value0 = sliceSegment("value 0")
                val value1 = sliceSegment("value 1")
                db.put(key3.data, key3.offset, key3.len, value0.data, value0.offset, value0.len)
                db.put(opt, key4.data, key4.offset, key4.len, value1.data, value1.offset, value1.len)

                // compare
                assertTrue(value0.isSamePayload(db.get(key3.data, key3.offset, key3.len)))
                assertTrue(value1.isSamePayload(db.get(key4.data, key4.offset, key4.len)))
            }
        }
    }

    private class Segment(val data: ByteArray, val offset: Int, val len: Int) {
        fun isSamePayload(value: ByteArray?): Boolean {
            if (value == null) {
                return false
            }
            if (value.size != len) {
                return false
            }

            for (i in value.indices) {
                if (data[i + offset] != value[i]) {
                    return false
                }
            }

            return true
        }
    }

    @Test
    fun write() {
        StringAppendOperator().use { stringAppendOperator ->
            Options().apply {
                setMergeOperator(stringAppendOperator)
                setCreateIfMissing(true)
            }.use { options ->
                openRocksDB(
                    options,
                    createTestFolder()
                ).use { db ->
                    WriteOptions().use { opts ->

                        WriteBatch().use { wb1 ->
                            wb1.put("key1".encodeToByteArray(), "aa".encodeToByteArray())
                            wb1.merge("key1".encodeToByteArray(), "bb".encodeToByteArray())

                            WriteBatch().use { wb2 ->
                                wb2.put("key2".encodeToByteArray(), "xx".encodeToByteArray())
                                wb2.merge("key2".encodeToByteArray(), "yy".encodeToByteArray())
                                db.write(opts, wb1)
                                db.write(opts, wb2)
                            }
                        }

                        assertContentEquals("aa,bb".encodeToByteArray(), db["key1".encodeToByteArray()])
                        assertContentEquals("xx,yy".encodeToByteArray(), db["key2".encodeToByteArray()])
                    }
                }
            }
        }
    }

    @Test
    fun getWithOutValue() {
        openRocksDB(createTestFolder()).use { db ->
            db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
            db.put("key2".encodeToByteArray(), "12345678".encodeToByteArray())
            val outValue = ByteArray(5)
            // not found value
            var getResult = db.get("keyNotFound".encodeToByteArray(), outValue)
            assertEquals(rocksDBNotFound, getResult)
            // found value which fits in outValue
            getResult = db.get("key1".encodeToByteArray(), outValue)
            assertNotEquals(rocksDBNotFound, getResult)
            assertContentEquals(outValue, "value".encodeToByteArray())
            // found value which fits partially
            getResult = db.get("key2".encodeToByteArray(), outValue)
            assertNotEquals(rocksDBNotFound, getResult)
            assertContentEquals(outValue, "12345".encodeToByteArray())
        }
    }

    @Test
    fun getWithOutValueReadOptions() {
        openRocksDB(createTestFolder()).use { db ->
            ReadOptions().use { rOpt ->
                db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
                db.put("key2".encodeToByteArray(), "12345678".encodeToByteArray())
                val outValue = ByteArray(5)
                // not found value
                var getResult = db.get(
                    rOpt, "keyNotFound".encodeToByteArray(),
                    outValue
                )
                assertEquals(rocksDBNotFound, getResult)
                // found value which fits in outValue
                getResult = db.get(rOpt, "key1".encodeToByteArray(), outValue)
                assertNotEquals(rocksDBNotFound, getResult)
                assertContentEquals(outValue, "value".encodeToByteArray())
                // found value which fits partially
                getResult = db.get(rOpt, "key2".encodeToByteArray(), outValue)
                assertNotEquals(rocksDBNotFound, getResult)
                assertContentEquals(outValue, "12345".encodeToByteArray())
            }
        }
    }

//    @Test
//    fun getOutOfArrayMaxSizeValue() {
//        val numberOfValueSplits = 10
//        val splitSize = Int.MAX_VALUE / numberOfValueSplits
//
//        val runtime = Runtime.getRuntime()
//        val neededMemory = splitSize.toLong() * (numberOfValueSplits.toLong() + 3)
//        val isEnoughMemory = runtime.maxMemory() - runtime.totalMemory() > neededMemory
//        assertTrue(isEnoughMemory)
//
//        val valueSplit = ByteArray(splitSize)
//        val key = "key".encodeToByteArray()
//
//        assertFailsWith<RocksDBException>("Requested array size exceeds VM limit") {
//            // merge (numberOfValueSplits + 1) valueSplit's to get value size exceeding Integer.MAX_VALUE
//            StringAppendOperator().use { stringAppendOperator ->
//                Options().apply {
//                    setCreateIfMissing(true)
//                    setMergeOperator(stringAppendOperator)
//                }.use { opt ->
//                    openRocksDB(opt, createTestFolder()).use { db ->
//                        db.put(key, valueSplit)
//                        for (i in 0 until numberOfValueSplits) {
//                            db.merge(key, valueSplit)
//                        }
//                        db.get(key)
//                    }
//                }
//            }
//        }
//    }

    @Test
    fun multiGetAsList() {
        openRocksDB(createTestFolder()).use { db ->
            ReadOptions().use { rOpt ->
                db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
                db.put("key2".encodeToByteArray(), "12345678".encodeToByteArray())
                val lookupKeys = mutableListOf(
                    "key1".encodeToByteArray(),
                    "key2".encodeToByteArray()
                )
                var results = db.multiGetAsList(lookupKeys)
                assertNotNull(results)
                assertEquals(lookupKeys.size, results.size)
                assertContainsExactly(results, "value".encodeToByteArray(), "12345678".encodeToByteArray())
                // test same method with ReadOptions
                results = db.multiGetAsList(rOpt, lookupKeys)
                assertNotNull(results)
                assertContains(results, "value".encodeToByteArray(), "12345678".encodeToByteArray())

                // remove existing key
                lookupKeys.removeAt(1)
                // add non existing key
                lookupKeys.add("key3".encodeToByteArray())
                results = db.multiGetAsList(lookupKeys)
                assertNotNull(results)
                assertContainsExactly(results, "value".encodeToByteArray(), null)
                // test same call with readOptions
                results = db.multiGetAsList(rOpt, lookupKeys)
                assertNotNull(results)
                assertContains(results, "value".encodeToByteArray())
            }
        }
    }

    @Test
    fun merge() {
        StringAppendOperator().use { stringAppendOperator ->
            Options().apply {
                setCreateIfMissing(true)
                setMergeOperator(stringAppendOperator)
            }.use { opt ->
                WriteOptions().use { wOpt ->
                    openRocksDB(
                        opt,
                        createTestFolder()
                    ).use { db ->
                        db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
                        assertContentEquals( "value".encodeToByteArray(), db["key1".encodeToByteArray()])
                        // merge key1 with another value portion
                        db.merge("key1".encodeToByteArray(), "value2".encodeToByteArray())
                        assertContentEquals("value,value2".encodeToByteArray(), db["key1".encodeToByteArray()])
                        // merge key1 with another value portion
                        db.merge(wOpt, "key1".encodeToByteArray(), "value3".encodeToByteArray())
                        assertContentEquals("value,value2,value3".encodeToByteArray(), db["key1".encodeToByteArray()])
                        // merge on non existent key shall insert the value
                        db.merge(wOpt, "key2".encodeToByteArray(), "xxxx".encodeToByteArray())
                        assertContentEquals("xxxx".encodeToByteArray(), db["key2".encodeToByteArray()])

                        val key3 = sliceSegment("key3")
                        val key4 = sliceSegment("key4")
                        val value0 = sliceSegment("value 0")
                        val value1 = sliceSegment("value 1")

                        db.merge(key3.data, key3.offset, key3.len, value0.data, value0.offset, value0.len)
                        db.merge(wOpt, key4.data, key4.offset, key4.len, value1.data, value1.offset, value1.len)

                        // compare
                        assertTrue(value0.isSamePayload(db.get(key3.data, key3.offset, key3.len)))
                        assertTrue(value1.isSamePayload(db.get(key4.data, key4.offset, key4.len)))
                    }
                }
            }
        }
    }

    @Test
    fun delete() {
        openRocksDB(createTestFolder()).use { db ->
            WriteOptions().use { wOpt ->
                db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
                db.put("key2".encodeToByteArray(), "12345678".encodeToByteArray())
                assertContentEquals("value".encodeToByteArray(), db["key1".encodeToByteArray()])
                assertContentEquals("12345678".encodeToByteArray(), db["key2".encodeToByteArray()])
                db.delete("key1".encodeToByteArray())
                db.delete(wOpt, "key2".encodeToByteArray())
                assertNull(db["key1".encodeToByteArray()])
                assertNull(db["key2".encodeToByteArray()])

                val key3 = sliceSegment("key3")
                val key4 = sliceSegment("key4")
                db.put("key3".encodeToByteArray(), "key3 value".encodeToByteArray())
                db.put("key4".encodeToByteArray(), "key4 value".encodeToByteArray())

                db.delete(key3.data, key3.offset, key3.len)
                db.delete(wOpt, key4.data, key4.offset, key4.len)

                assertNull(db["key3".encodeToByteArray()])
                assertNull(db["key4".encodeToByteArray()])
            }
        }
    }

    @Test
    fun deleteRange() {
        openRocksDB(createTestFolder()).use { db ->
            db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
            db.put("key2".encodeToByteArray(), "12345678".encodeToByteArray())
            db.put("key3".encodeToByteArray(), "abcdefg".encodeToByteArray())
            db.put("key4".encodeToByteArray(), "xyz".encodeToByteArray())
            assertContentEquals("value".encodeToByteArray(), db["key1".encodeToByteArray()])
            assertContentEquals("12345678".encodeToByteArray(), db["key2".encodeToByteArray()])
            assertContentEquals("abcdefg".encodeToByteArray(), db["key3".encodeToByteArray()])
            assertContentEquals("xyz".encodeToByteArray(), db["key4".encodeToByteArray()])
            db.deleteRange("key2".encodeToByteArray(), "key4".encodeToByteArray())
            assertContentEquals("value".encodeToByteArray(), db["key1".encodeToByteArray()])
            assertNull(db["key2".encodeToByteArray()])
            assertNull(db["key3".encodeToByteArray()])
            assertContentEquals("xyz".encodeToByteArray(), db["key4".encodeToByteArray()])
        }
    }

    @Test
    fun getIntProperty() {
        Options().apply {
            setCreateIfMissing(true)
            setMaxWriteBufferNumber(10)
            setMinWriteBufferNumberToMerge(10)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                WriteOptions().setDisableWAL(true).use { wOpt ->
                    db.put(wOpt, "key1".encodeToByteArray(), "value1".encodeToByteArray())
                    db.put(wOpt, "key2".encodeToByteArray(), "value2".encodeToByteArray())
                    db.put(wOpt, "key3".encodeToByteArray(), "value3".encodeToByteArray())
                    db.put(wOpt, "key4".encodeToByteArray(), "value4".encodeToByteArray())
                    assertTrue(db.getLongProperty("rocksdb.num-entries-active-mem-table") > 0)
                    assertTrue(db.getLongProperty("rocksdb.cur-size-active-mem-table") > 0)
                }
            }
        }
    }

    @Test
    fun fullCompactRange() {
        Options().apply {
            setCreateIfMissing(true)
            setDisableAutoCompactions(true)
            setCompactionStyle(CompactionStyle.LEVEL)
            setNumLevels(4)
            setWriteBufferSize((100 shl 10).toLong())
            setLevelZeroFileNumCompactionTrigger(3)
            setTargetFileSizeBase((200 shl 10).toLong())
            setTargetFileSizeMultiplier(1)
            setMaxBytesForLevelBase((500 shl 10).toLong())
            setMaxBytesForLevelMultiplier(1.0)
            setDisableAutoCompactions(false)
        }.use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // fill database with key/value pairs
                val b = ByteArray(10000)
                for (i in 0..199) {
                    Random.nextBytes(b)
                    db.put(i.toString().encodeToByteArray(), b)
                }
                db.compactRange()
            }
        }
    }

    @Test
    fun fullCompactRangeColumnFamily() {
        DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true).use { opt ->
            ColumnFamilyOptions().setDisableAutoCompactions(true).setCompactionStyle(CompactionStyle.LEVEL)
                .setNumLevels(4).setWriteBufferSize((100 shl 10).toLong()).setLevelZeroFileNumCompactionTrigger(3)
                .setTargetFileSizeBase((200 shl 10).toLong()).setTargetFileSizeMultiplier(1)
                .setMaxBytesForLevelBase((500 shl 10).toLong()).setMaxBytesForLevelMultiplier(1.0)
                .setDisableAutoCompactions(false).use { new_cf_opts ->
                    val columnFamilyDescriptors = listOf(
                        ColumnFamilyDescriptor(defaultColumnFamily),
                        ColumnFamilyDescriptor("new_cf".encodeToByteArray(), new_cf_opts)
                    )

                    // open database
                    val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
                    openRocksDB(
                        opt,
                        createTestFolder(),
                        columnFamilyDescriptors,
                        columnFamilyHandles
                    ).use { db ->
                        try {
                            // fill database with key/value pairs
                            val b = ByteArray(10000)
                            for (i in 0..199) {
                                Random.nextBytes(b)
                                db.put(
                                    columnFamilyHandles[1],
                                    i.toString().encodeToByteArray(), b
                                )
                            }
                            db.compactRange(columnFamilyHandles[1])
                        } finally {
                            for (handle in columnFamilyHandles) {
                                handle.close()
                            }
                        }
                    }
                }
        }
    }

    @Test
    fun compactRangeWithKeys() {
        Options().apply {
            setCreateIfMissing(true)
            setDisableAutoCompactions(true)
            setCompactionStyle(CompactionStyle.LEVEL)
            setNumLevels(4)
            setWriteBufferSize((100 shl 10).toLong())
            setLevelZeroFileNumCompactionTrigger(3)
            setTargetFileSizeBase((200 shl 10).toLong())
            setTargetFileSizeMultiplier(1)
            setMaxBytesForLevelBase((500 shl 10).toLong())
            setMaxBytesForLevelMultiplier(1.0)
            setDisableAutoCompactions(false)
        }.use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // fill database with key/value pairs
                val b = ByteArray(10000)
                for (i in 0..199) {
                    Random.nextBytes(b)
                    db.put(i.toString().encodeToByteArray(), b)
                }
                db.compactRange("0".encodeToByteArray(), "201".encodeToByteArray())
            }
        }
    }

    @Test
    fun compactRangeWithKeysColumnFamily() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { opt ->
            ColumnFamilyOptions().apply {
                setDisableAutoCompactions(true)
                setCompactionStyle(CompactionStyle.LEVEL)
                setNumLevels(4)
                setWriteBufferSize((100 shl 10).toLong())
                setLevelZeroFileNumCompactionTrigger(3)
                setTargetFileSizeBase((200 shl 10).toLong())
                setTargetFileSizeMultiplier(1)
                setMaxBytesForLevelBase((500 shl 10).toLong())
                setMaxBytesForLevelMultiplier(1.0)
                setDisableAutoCompactions(false)
            }.use { new_cf_opts ->
                    val columnFamilyDescriptors = listOf(
                        ColumnFamilyDescriptor(defaultColumnFamily),
                        ColumnFamilyDescriptor("new_cf".encodeToByteArray(), new_cf_opts)
                    )

                    // open database
                    val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
                    openRocksDB(
                        opt,
                        createTestFolder(),
                        columnFamilyDescriptors,
                        columnFamilyHandles
                    ).use { db ->
                        try {
                            // fill database with key/value pairs
                            val b = ByteArray(10000)
                            for (i in 0..199) {
                                Random.nextBytes(b)
                                db.put(
                                    columnFamilyHandles[1],
                                    i.toString().encodeToByteArray(), b
                                )
                            }
                            db.compactRange(
                                columnFamilyHandles[1],
                                "0".encodeToByteArray(), "201".encodeToByteArray()
                            )
                        } finally {
                            for (handle in columnFamilyHandles) {
                                handle.close()
                            }
                        }
                    }
                }
        }
    }

    @Test
    fun compactRangeToLevel() {
        val numKeysPerL0File = 100
        val keySize = 20
        val valueSize = 300
        val l0FileSize = numKeysPerL0File * (keySize + valueSize)
        val numL0Files = 10
        val keyInterval = 100
        Options().apply {
            setCreateIfMissing(true)
            setCompactionStyle(CompactionStyle.LEVEL)
            setNumLevels(5)

            // a slightly bigger write buffer than L0 file so that we can ensure manual flush always
            // go before background flush happens.
            setWriteBufferSize((l0FileSize * 2).toLong())

            // Disable auto L0 -> L1 compaction
            setLevelZeroFileNumCompactionTrigger(20)

            setTargetFileSizeBase((l0FileSize * 100).toLong())
            setTargetFileSizeMultiplier(1)

            // To disable auto compaction
            setMaxBytesForLevelBase((numL0Files * l0FileSize * 100).toLong())

            setMaxBytesForLevelMultiplier(2.0)
            setDisableAutoCompactions(true)
        }.use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // fill database with key/value pairs
                val value = ByteArray(valueSize)
                var intKey = 0
                for (round in 0..4) {
                    val initialKey = intKey
                    for (f in 1..numL0Files) {
                        for (i in 0 until numKeysPerL0File) {
                            intKey += keyInterval
                            Random.nextBytes(value)

                            db.put(
                                intKey.toString().prepend0(20).encodeToByteArray(),
                                value
                            )
                        }
                        db.flush(FlushOptions().setWaitForFlush(true))
                        // Make sure we do create one more L0 files.
                        assertEquals(
                            "" + f,
                            db.getProperty("rocksdb.num-files-at-level0")
                        )
                    }

                    // Compact all L0 files we just created
                    db.compactRange(
                        initialKey.toString().prepend0(20).encodeToByteArray(),
                        (intKey - 1).toString().prepend0(20).encodeToByteArray()
                    )
                    // Making sure there isn't any L0 files.
                    assertEquals("0", db.getProperty("rocksdb.num-files-at-level0"))
                    // Making sure there are some L1 files.
                    // Here we only use != 0 instead of a specific number
                    // as we don't want the test make any assumption on
                    // how compaction works.
                    assertNotEquals("0", db.getProperty("rocksdb.num-files-at-level1"))
                    // Because we only compacted those keys we issued
                    // in this round, there shouldn't be any L1 -> L2
                    // compaction.  So we expect zero L2 files here.
                    assertEquals("0", db.getProperty("rocksdb.num-files-at-level2"))
                }
            }
        }
    }

    @Test
    fun compactRangeToLevelColumnFamily() {
        val numKeysPerL0File = 100
        val keySize = 20
        val valueSize = 300
        val l0FileSize = numKeysPerL0File * (keySize + valueSize)
        val numL0Files = 10
        val keyInterval = 100

        DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true).use { opt ->
            ColumnFamilyOptions().setCompactionStyle(CompactionStyle.LEVEL).setNumLevels(5)
                .setWriteBufferSize((l0FileSize * 2).toLong())// a slightly bigger write buffer than L0 file
                // so that we can ensure manual flush always
                // go before background flush happens.
                .setLevelZeroFileNumCompactionTrigger(20)// Disable auto L0 -> L1 compaction
                .setTargetFileSizeBase((l0FileSize * 100).toLong()).setTargetFileSizeMultiplier(1)
                .setMaxBytesForLevelBase((numL0Files * l0FileSize * 100).toLong())// To disable auto compaction
                .setMaxBytesForLevelMultiplier(2.0).setDisableAutoCompactions(true).use { new_cf_opts ->
                    val columnFamilyDescriptors = listOf(
                        ColumnFamilyDescriptor(defaultColumnFamily),
                        ColumnFamilyDescriptor("new_cf".encodeToByteArray(), new_cf_opts)
                    )

                    val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
                    // open database
                    openRocksDB(
                        opt,
                        createTestFolder(),
                        columnFamilyDescriptors,
                        columnFamilyHandles
                    ).use { db ->
                        try {
                            // fill database with key/value pairs
                            val value = ByteArray(valueSize)
                            var intKey = 0
                            for (round in 0..4) {
                                val initialKey = intKey
                                for (f in 1..numL0Files) {
                                    for (i in 0 until numKeysPerL0File) {
                                        intKey += keyInterval
                                        Random.nextBytes(value)

                                        db.put(
                                            columnFamilyHandles[1],
                                            intKey.toString().prepend0(20).encodeToByteArray(),
                                            value
                                        )
                                    }
                                    db.flush(
                                        FlushOptions().setWaitForFlush(true),
                                        columnFamilyHandles[1]
                                    )
                                    // Make sure we do create one more L0 files.
                                    assertEquals(
                                        "" + f,
                                        db.getProperty(
                                            columnFamilyHandles[1],
                                            "rocksdb.num-files-at-level0"
                                        )
                                    )
                                }

                                // Compact all L0 files we just created
                                db.compactRange(
                                    columnFamilyHandles[1],
                                    initialKey.toString().prepend0(20).encodeToByteArray(),
                                    (intKey - 1).toString().prepend0(20).encodeToByteArray()
                                )
                                // Making sure there isn't any L0 files.
                                expect("0") {
                                    db.getProperty(
                                        columnFamilyHandles[1],
                                        "rocksdb.num-files-at-level0"
                                    )
                                }
                                // Making sure there are some L1 files.
                                // Here we only use != 0 instead of a specific number
                                // as we don't want the test make any assumption on
                                // how compaction works.
                                assertNotEquals(
                                    "0",
                                    db.getProperty(
                                        columnFamilyHandles[1],
                                        "rocksdb.num-files-at-level1"
                                    )
                                )
                                // Because we only compacted those keys we issued
                                // in this round, there shouldn't be any L1 -> L2
                                // compaction.  So we expect zero L2 files here.
                                expect("0") {
                                    db.getProperty(
                                        columnFamilyHandles[1],
                                        "rocksdb.num-files-at-level2"
                                    )
                                }
                            }
                        } finally {
                            for (handle in columnFamilyHandles) {
                                handle.close()
                            }
                        }
                    }
                }
        }
    }

    @Test
    fun pauseContinueBackgroundWork() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.pauseBackgroundWork()
                db.continueBackgroundWork()
                db.pauseBackgroundWork()
                db.continueBackgroundWork()
            }
        }
    }

    @Test
    fun enableDisableFileDeletions() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.disableFileDeletions()
                db.enableFileDeletions(false)
                db.disableFileDeletions()
                db.enableFileDeletions(true)
            }
        }
    }

    @Test
    fun setOptions() {
        DBOptions()
            .setCreateIfMissing(true)
            .setCreateMissingColumnFamilies(true).use { options ->
                ColumnFamilyOptions()
                    .setWriteBufferSize(4096).use { new_cf_opts ->
                        val columnFamilyDescriptors = listOf(
                            ColumnFamilyDescriptor(defaultColumnFamily),
                            ColumnFamilyDescriptor("new_cf".encodeToByteArray(), new_cf_opts)
                        )

                        // open database
                        val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
                        openRocksDB(
                            options,
                            createTestFolder(), columnFamilyDescriptors, columnFamilyHandles
                        ).use { db ->
                            try {
                                val mutableOptions = mutableColumnFamilyOptionsBuilder()
                                    .setWriteBufferSize(2048)
                                    .build()

                                db.setOptions(columnFamilyHandles[1], mutableOptions)
                            } finally {
                                for (handle in columnFamilyHandles) {
                                    handle.close()
                                }
                            }
                        }
                    }
            }
    }

//    @Test
//    fun destroyDB() {
//        Options().setCreateIfMissing(true).use { options ->
//            val dbPath = dbFolder.getRoot().getAbsolutePath()
//            openRocksDB(options, dbPath).use({ db -> db.put("key1".encodeToByteArray(), "value".encodeToByteArray()) })
//            assertTrue(dbFolder.getRoot().exists())
//            destroyRocksDB(dbPath, options)
//            assertFalse(dbFolder.getRoot().exists())
//        }
//    }

    @Test
    fun destroyDBFailIfOpen() {
        Options().setCreateIfMissing(true).use { options ->
            val dbPath = createTestFolder()
            openRocksDB(options, dbPath).use {
                // Fails as the db is open and locked.
                assertFailsWith<RocksDBException> {
                    destroyRocksDB(dbPath, options)
                }
            }
        }
    }

    @Test
    fun getApproximateMemTableStats() {
        val key1 = "key1".encodeToByteArray()
        val key2 = "key2".encodeToByteArray()
        val key3 = "key3".encodeToByteArray()
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                db.put(key1, key1)
                db.put(key2, key2)
                db.put(key3, key3)

                val stats = db.getApproximateMemTableStats(
                    Range(Slice(key1), Slice(key3))
                )

                assertNotNull(stats)
                assertTrue(stats.count > 1)
                assertTrue(stats.size > 1)
            }
        }
    }

    @Test
    fun enableAutoCompaction() {
        DBOptions().setCreateIfMissing(true).use { options ->
            val cfDescs = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily)
            )
            val cfHandles = mutableListOf<ColumnFamilyHandle>()
            openRocksDB(options, createTestFolder(), cfDescs, cfHandles).use { db ->
                try {
                    db.enableAutoCompaction(cfHandles)
                } finally {
                    for (cfHandle in cfHandles) {
                        cfHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun numberLevels() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                assertEquals(7, db.numberLevels())
            }
        }
    }

    @Test
    fun maxMemCompactionLevel() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                assertEquals(0, db.maxMemCompactionLevel())
            }
        }
    }

    @Test
    fun level0StopWriteTrigger() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                assertEquals(36, db.level0StopWriteTrigger())
            }
        }
    }

    @Test
    fun getName() {
        Options().setCreateIfMissing(true).use { options ->
            val dbPath = createTestFolder()
            openRocksDB(options, dbPath).use { db ->
                assertEquals(dbPath, db.getName())
            }
        }
    }

    @Test
    fun getEnv() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                assertEquals(db.getEnv(), getDefaultEnv())
            }
        }
    }

    @Test
    fun flush() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                FlushOptions().use { flushOptions ->
                    db.flush(flushOptions)
                }
            }
        }
    }

    @Test
    fun flushWal() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db -> db.flushWal(true) }
        }
    }

    @Test
    fun syncWal() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db -> db.syncWal() }
        }
    }

    @Test
    fun setPreserveDeletesSequenceNumber() {
        Options().setCreateIfMissing(true).use { options ->
            val dbPath = createTestFolder()
            openRocksDB(options, dbPath).use { db ->
                assertFalse(db.setPreserveDeletesSequenceNumber(db.getLatestSequenceNumber()))
            }
        }
    }

    @Test
    fun getLiveFiles() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                val livefiles = db.getLiveFiles(true)
                assertNotNull(livefiles)
                assertEquals(13, livefiles.manifestFileSize)
                assertEquals(3, livefiles.files.size)
                assertEquals("/CURRENT", livefiles.files[0])
                assertEquals("/MANIFEST-000001", livefiles.files[1])
                assertEquals("/OPTIONS-000005", livefiles.files[2])
            }
        }
    }

    @Test
    fun getSortedWalFiles() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                val logFiles = db.getSortedWalFiles()
                assertNotNull(logFiles)
                assertEquals(1, logFiles.size)
                assertEquals(WalFileType.kAliveLogFile, logFiles[0].type())
            }
        }
    }

    @Test
    fun deleteFile() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                db.deleteFile("unknown")
            }
        }
    }

    @Test
    fun getLiveFilesMetaData() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                val liveFilesMetaData = db.getLiveFilesMetaData()
                assertTrue(liveFilesMetaData.isEmpty())
            }
        }
    }

    @Test
    fun getColumnFamilyMetaData() {
        DBOptions().apply {
            setCreateIfMissing(true)
        }.use { options ->
            val cfDescs = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily)
            )
            val cfHandles = mutableListOf<ColumnFamilyHandle>()
            openRocksDB(options, "${createTestFolder()}columnFamilies4", cfDescs, cfHandles).use { db ->
                db.put(cfHandles[0], "key1".encodeToByteArray(), "value1".encodeToByteArray())
                try {
                    val cfMetadata = db.getColumnFamilyMetaData(cfHandles[0])
                    assertNotNull(cfMetadata)
                    assertContentEquals(cfMetadata.name(), defaultColumnFamily)
                    assertEquals(7, cfMetadata.levels().size)
                } finally {
                    for (cfHandle in cfHandles) {
                        cfHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun verifyChecksum() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                db.verifyChecksum()
            }
        }
    }

    @Test
    fun getPropertiesOfAllTables() {
        DBOptions().apply {
            setCreateIfMissing(true)
        }.use { options ->
            val cfDescs = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily)
            )
            val cfHandles = mutableListOf<ColumnFamilyHandle>()
            val dbPath = createTestFolder()
            openRocksDB(options, dbPath, cfDescs, cfHandles).use { db ->
                db.put(cfHandles[0], "key1".encodeToByteArray(), "value1".encodeToByteArray())
                try {
                    val properties = db.getPropertiesOfAllTables(cfHandles[0])
                    assertNotNull(properties)
                } finally {
                    for (cfHandle in cfHandles) {
                        cfHandle.close()
                    }
                }
            }
        }
    }

//    Strangly fails with a long list instead of map
//    @Test
//    fun getPropertiesOfTablesInRange() {
//        DBOptions().apply {
//            setCreateIfMissing(true)
//        }.use { options ->
//            val cfDescs = listOf(
//                ColumnFamilyDescriptor(defaultColumnFamily)
//            )
//            val cfHandles = mutableListOf<ColumnFamilyHandle>()
//            openRocksDB(options, createTestFolder(), cfDescs, cfHandles).use { db ->
//                db.put(cfHandles[0], "key1".encodeToByteArray(), "value1".encodeToByteArray())
//                db.put(cfHandles[0], "key2".encodeToByteArray(), "value2".encodeToByteArray())
//                db.put(cfHandles[0], "key3".encodeToByteArray(), "value3".encodeToByteArray())
//                try {
//                    val range = Range(
//                        Slice("key1".encodeToByteArray()),
//                        Slice("key3".encodeToByteArray())
//                    )
//                    val properties = db.getPropertiesOfTablesInRange(
//                        cfHandles[0], listOf(range)
//                    )
//                    assertNotNull(properties)
//                } finally {
//                    for (cfHandle in cfHandles) {
//                        cfHandle.close()
//                    }
//                }
//            }
//        }
//    }

    @Test
    fun suggestCompactRange() {
        DBOptions().apply {
            setCreateIfMissing(true)
        }.use { options ->
            val cfDescs = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily)
            )
            val cfHandles = mutableListOf<ColumnFamilyHandle>()
            openRocksDB(options, createTestFolder(), cfDescs, cfHandles).use { db ->
                db.put(cfHandles[0], "key1".encodeToByteArray(), "value1".encodeToByteArray())
                db.put(cfHandles[0], "key2".encodeToByteArray(), "value2".encodeToByteArray())
                db.put(cfHandles[0], "key3".encodeToByteArray(), "value3".encodeToByteArray())
                try {
                    val range = db.suggestCompactRange(cfHandles[0])
                    assertNotNull(range)
                } finally {
                    for (cfHandle in cfHandles) {
                        cfHandle.close()
                    }
                }
            }
        }
    }

    @Test
    fun promoteL0() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                db.promoteL0(2)
            }
        }
    }

    @Test
    fun startTrace() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                val traceOptions = TraceOptions()

                InMemoryTraceWriter().use { traceWriter ->
                    db.startTrace(traceOptions, traceWriter)
                    db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                    db.endTrace()

                    val writes = traceWriter.getWrites()
                    assertTrue(writes.isNotEmpty())
                }
            }
        }
    }

    @Test
    fun setDBOptions() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { options ->
            ColumnFamilyOptions().apply {
                setWriteBufferSize(4096)
            }.use { new_cf_opts ->
                val columnFamilyDescriptors = listOf(
                    ColumnFamilyDescriptor(defaultColumnFamily),
                    ColumnFamilyDescriptor("new_cf".encodeToByteArray(), new_cf_opts)
                )

                // open database
                val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
                openRocksDB(
                    options,
                    createTestFolder(), columnFamilyDescriptors, columnFamilyHandles
                ).use { db ->
                    try {
                        val mutableOptions = mutableDBOptionsBuilder()
                            .setBytesPerSync((1024 * 1027 * 7).toLong())
                            .setAvoidFlushDuringShutdown(false)
                            .build()

                        db.setDBOptions(mutableOptions)
                    } finally {
                        for (handle in columnFamilyHandles) {
                            handle.close()
                        }
                    }
                }
            }
        }
    }

    private class InMemoryTraceWriter : AbstractTraceWriter() {
        private val writes = mutableListOf<ByteArray>()
        @Volatile
        private var closed = false

        override fun write(data: Slice) {
            if (closed) {
                return
            }
            val slideData = data.data()
            val dataCopy = slideData.copyOf()
            writes.add(dataCopy)
        }

        override fun closeWriter() {
            closed = true
        }

        override fun getFileSize(): Long {
            var size: Long = 0
            for (i in writes.indices) {
                size += writes[i].size.toLong()
            }
            return size
        }

        fun getWrites(): List<ByteArray> {
            return writes
        }
    }

    private fun sliceSegment(key: String): Segment {
        val rawKey = ByteArray(key.length + 4)
        val keyBytes = key.encodeToByteArray()
        keyBytes.copyInto(rawKey, 2, 0, keyBytes.size)
        return Segment(rawKey, 2, key.length)
    }
}
