package maryk.rocksdb

import maryk.allocateByteBuffer
import maryk.decodeToString
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals

class MergeTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("MergeTest")

    @Test
    fun stringOption() {
        Options().apply {
            setCreateIfMissing(true)
            setMergeOperatorName("stringappend")
        }.use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // writing aa under key
                db.put("key".encodeToByteArray(), "aa".encodeToByteArray())
                // merge bb under key
                db.merge("key".encodeToByteArray(), "bb".encodeToByteArray())

                val value = db["key".encodeToByteArray()]!!
                val strValue = value.decodeToString()
                assertEquals("aa,bb", strValue)
            }
        }
    }

    private fun longToByteArray(l: Long): ByteArray {
        val buf = allocateByteBuffer(Long.SIZE_BYTES / Byte.SIZE_BYTES)
        buf.putLong(l)
        return buf.array()
    }

    private fun longFromByteArray(a: ByteArray): Long {
        val buf = allocateByteBuffer(Long.SIZE_BYTES / Byte.SIZE_BYTES)
        buf.put(a)
        buf.flip()
        return buf.getLong()
    }

    @Test
    fun uint64AddOption() {
        Options().apply {
            setCreateIfMissing(true)
            setMergeOperatorName("uint64add")
        }.use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // writing (long)100 under key
                db.put("key".encodeToByteArray(), longToByteArray(100))
                // merge (long)1 under key
                db.merge("key".encodeToByteArray(), longToByteArray(1))

                val value = db["key".encodeToByteArray()]!!
                val longValue = longFromByteArray(value)
                assertEquals(101, longValue)
            }
        }
    }

    @Test
    fun cFStringOption() {
        ColumnFamilyOptions().apply {
            setMergeOperatorName("stringappend")
        }.use { cfOpt1 ->
            ColumnFamilyOptions().apply {
                setMergeOperatorName("stringappend")
            }.use { cfOpt2 ->
                val cfDescriptors = listOf(
                    ColumnFamilyDescriptor(defaultColumnFamily, cfOpt1),
                    ColumnFamilyDescriptor(defaultColumnFamily, cfOpt2)
                )

                val columnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
                DBOptions().apply {
                    setCreateIfMissing(true)
                    setCreateMissingColumnFamilies(true)
                }.use { opt ->
                    openRocksDB(
                        opt,
                        createTestFolder(),
                        cfDescriptors,
                        columnFamilyHandleList
                    ).use { db ->
                        try {
                            // writing aa under key
                            db.put(
                                columnFamilyHandleList[1],
                                "cfkey".encodeToByteArray(), "aa".encodeToByteArray()
                            )
                            // merge bb under key
                            db.merge(
                                columnFamilyHandleList[1],
                                "cfkey".encodeToByteArray(), "bb".encodeToByteArray()
                            )

                            val value = db.get(
                                columnFamilyHandleList[1],
                                "cfkey".encodeToByteArray()
                            )
                            val strValue = value?.decodeToString()
                            assertEquals("aa,bb", strValue)
                        } finally {
                            for (handle in columnFamilyHandleList) {
                                handle.close()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun cFUInt64AddOption() {
        ColumnFamilyOptions().apply {
            setMergeOperatorName("uint64add")
        }.use { cfOpt1 ->
            ColumnFamilyOptions().apply {
                setMergeOperatorName("uint64add")
            }.use { cfOpt2 ->
                val cfDescriptors = listOf(
                    ColumnFamilyDescriptor(defaultColumnFamily, cfOpt1),
                    ColumnFamilyDescriptor(defaultColumnFamily, cfOpt2)
                )

                val columnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
                DBOptions().apply {
                    setCreateIfMissing(true)
                    setCreateMissingColumnFamilies(true)
                }.use { opt ->
                    openRocksDB(
                        opt,
                        createTestFolder(),
                        cfDescriptors,
                        columnFamilyHandleList
                    ).use { db ->
                        try {
                            // writing (long)100 under key
                            db.put(
                                columnFamilyHandleList[1],
                                "cfkey".encodeToByteArray(), longToByteArray(100)
                            )
                            // merge (long)1 under key
                            db.merge(
                                columnFamilyHandleList[1],
                                "cfkey".encodeToByteArray(), longToByteArray(1)
                            )

                            val value = db.get(
                                columnFamilyHandleList[1],
                                "cfkey".encodeToByteArray()
                            )!!
                            val longValue = longFromByteArray(value)
                            assertEquals(101, longValue)
                        } finally {
                            for (handle in columnFamilyHandleList) {
                                handle.close()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun operatorOption() {
        StringAppendOperator().use { stringAppendOperator ->
            Options().apply {
                setCreateIfMissing(true)
                setMergeOperator(stringAppendOperator)
            }.use { opt ->
                openRocksDB(
                    opt,
                    createTestFolder()
                ).use { db ->
                    // Writing aa under key
                    db.put("key".encodeToByteArray(), "aa".encodeToByteArray())

                    // Writing bb under key
                    db.merge("key".encodeToByteArray(), "bb".encodeToByteArray())

                    val value = db["key".encodeToByteArray()]
                    val strValue = value?.decodeToString()

                    assertEquals("aa,bb", strValue)
                }
            }
        }
    }

    @Test
    fun uint64AddOperatorOption() {
        UInt64AddOperator().use { uint64AddOperator ->
            Options().apply {
                setCreateIfMissing(true)
                setMergeOperator(uint64AddOperator)
            }.use { opt ->
                openRocksDB(
                    opt,
                    createTestFolder()
                ).use { db ->
                    // Writing (long)100 under key
                    db.put("key".encodeToByteArray(), longToByteArray(100))

                    // Writing (long)1 under key
                    db.merge("key".encodeToByteArray(), longToByteArray(1))

                    val value = db["key".encodeToByteArray()]!!
                    val longValue = longFromByteArray(value)

                    assertEquals(101, longValue)
                }
            }
        }
    }

    @Test
    fun cFOperatorOption() {
        StringAppendOperator().use { stringAppendOperator ->
            ColumnFamilyOptions().apply {
                setMergeOperator(stringAppendOperator)
            }.use { cfOpt1 ->
                ColumnFamilyOptions().apply {
                    setMergeOperator(stringAppendOperator)
                }.use { cfOpt2 ->
                    val cfDescriptors = listOf(
                        ColumnFamilyDescriptor(defaultColumnFamily, cfOpt1),
                        ColumnFamilyDescriptor("new_cf".encodeToByteArray(), cfOpt2)
                    )
                    val columnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
                    DBOptions().apply {
                        setCreateIfMissing(true)
                        setCreateMissingColumnFamilies(true)
                    }.use { opt ->
                        openRocksDB(
                            opt,
                            createTestFolder(),
                            cfDescriptors,
                            columnFamilyHandleList
                        ).use { db ->
                            try {
                                // writing aa under key
                                db.put(
                                    columnFamilyHandleList[1],
                                    "cfkey".encodeToByteArray(), "aa".encodeToByteArray()
                                )
                                // merge bb under key
                                db.merge(
                                    columnFamilyHandleList[1],
                                    "cfkey".encodeToByteArray(), "bb".encodeToByteArray()
                                )
                                var value = db.get(
                                    columnFamilyHandleList[1],
                                    "cfkey".encodeToByteArray()
                                )
                                val strValue = value?.decodeToString()

                                // Test also with createColumnFamily
                                ColumnFamilyOptions().apply {
                                    setMergeOperator(stringAppendOperator)
                                }.use { cfHandleOpts ->
                                    db.createColumnFamily(
                                        ColumnFamilyDescriptor(
                                            "new_cf2".encodeToByteArray(),
                                            cfHandleOpts
                                        )
                                    ).use { cfHandle ->
                                        // writing xx under cfkey2
                                        db.put(
                                            cfHandle,
                                            "cfkey2".encodeToByteArray(),
                                            "xx".encodeToByteArray()
                                        )
                                        // merge yy under cfkey2
                                        db.merge(
                                            cfHandle, WriteOptions(), "cfkey2".encodeToByteArray(),
                                            "yy".encodeToByteArray()
                                        )
                                        value = db.get(cfHandle, "cfkey2".encodeToByteArray())
                                        val strValueTmpCf = value?.decodeToString()

                                        assertEquals("aa,bb", strValue)
                                        assertEquals("xx,yy", strValueTmpCf)
                                    }
                                }
                            } finally {
                                for (columnFamilyHandle in columnFamilyHandleList) {
                                    columnFamilyHandle.close()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun cFUInt64AddOperatorOption() {
        UInt64AddOperator().use { uint64AddOperator ->
            ColumnFamilyOptions().apply {
                setMergeOperator(uint64AddOperator)
            }.use { cfOpt1 ->
                ColumnFamilyOptions().apply {
                    setMergeOperator(uint64AddOperator)
                }.use { cfOpt2 ->
                    val cfDescriptors = listOf(
                        ColumnFamilyDescriptor(defaultColumnFamily, cfOpt1),
                        ColumnFamilyDescriptor("new_cf".encodeToByteArray(), cfOpt2)
                    )
                    val columnFamilyHandleList = mutableListOf<ColumnFamilyHandle>()
                    DBOptions().apply {
                        setCreateIfMissing(true)
                        setCreateMissingColumnFamilies(true)
                    }.use { opt ->
                        openRocksDB(
                            opt,
                            createTestFolder(),
                            cfDescriptors,
                            columnFamilyHandleList
                        ).use { db ->
                            try {
                                // writing (long)100 under key
                                db.put(
                                    columnFamilyHandleList[1],
                                    "cfkey".encodeToByteArray(), longToByteArray(100)
                                )
                                // merge (long)1 under key
                                db.merge(
                                    columnFamilyHandleList[1],
                                    "cfkey".encodeToByteArray(), longToByteArray(1)
                                )
                                var value = db.get(
                                    columnFamilyHandleList[1],
                                    "cfkey".encodeToByteArray()
                                )!!
                                val longValue = longFromByteArray(value)

                                // Test also with createColumnFamily
                                ColumnFamilyOptions().apply {
                                    setMergeOperator(uint64AddOperator)
                                }.use { cfHandleOpts ->
                                    db.createColumnFamily(
                                        ColumnFamilyDescriptor(
                                            "new_cf2".encodeToByteArray(),
                                            cfHandleOpts
                                        )
                                    ).use { cfHandle ->
                                        // writing (long)200 under cfkey2
                                        db.put(
                                            cfHandle,
                                            "cfkey2".encodeToByteArray(),
                                            longToByteArray(200)
                                        )
                                        // merge (long)50 under cfkey2
                                        db.merge(
                                            cfHandle, WriteOptions(), "cfkey2".encodeToByteArray(),
                                            longToByteArray(50)
                                        )
                                        value = db.get(cfHandle, "cfkey2".encodeToByteArray())!!
                                        val longValueTmpCf = longFromByteArray(value)

                                        assertEquals(101, longValue)
                                        assertEquals(250, longValueTmpCf)
                                    }
                                }
                            } finally {
                                for (columnFamilyHandle in columnFamilyHandleList) {
                                    columnFamilyHandle.close()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun operatorGcBehaviour() {
        StringAppendOperator().use { stringAppendOperator ->
            val testFolder = createTestFolder()

            Options().apply {
                setCreateIfMissing(true)
                setMergeOperator(stringAppendOperator)
            }.use { opt ->
                openRocksDB(
                    opt,
                    testFolder
                ).use {
                    //no-op
                }
            }

            // test reuse
            Options().apply {
                setMergeOperator(stringAppendOperator)
            }.use { opt ->
                openRocksDB(
                    opt,
                    testFolder
                ).use {
                    //no-op
                }
            }

            // test param init
            StringAppendOperator().use { stringAppendOperator2 ->
                Options().apply {
                    setMergeOperator(stringAppendOperator2)
                }.use { opt ->
                    openRocksDB(
                        opt,
                        testFolder
                    ).use {
                        //no-op
                    }
                }
            }

            // test replace one with another merge operator instance
            Options().apply {
                setMergeOperator(stringAppendOperator)
            }.use { opt ->
                StringAppendOperator().use { newStringAppendOperator ->
                    opt.setMergeOperator(newStringAppendOperator)
                    openRocksDB(
                        opt,
                        testFolder
                    ).use {
                        //no-op
                    }
                }
            }
        }
    }

    @Test
    fun uint64AddOperatorGcBehaviour() {
        UInt64AddOperator().use { uint64AddOperator ->
            val testFolder = createTestFolder()

            Options().apply {
                setCreateIfMissing(true)
                setMergeOperator(uint64AddOperator)
            }.use { opt ->
                openRocksDB(
                    opt,
                    testFolder
                ).use {
                    //no-op
                }
            }

            // test reuse
            Options().apply {
                setMergeOperator(uint64AddOperator)
            }.use { opt ->
                openRocksDB(
                    opt,
                    testFolder
                ).use {
                    //no-op
                }
            }

            // test param init
            UInt64AddOperator().use { uint64AddOperator2 ->
                Options().apply {
                    setMergeOperator(uint64AddOperator2)
                }.use { opt ->
                    openRocksDB(
                        opt,
                        testFolder
                    ).use {
                        //no-op
                    }
                }
            }

            // test replace one with another merge operator instance
            Options().apply {
                setMergeOperator(uint64AddOperator)
            }.use { opt ->
                UInt64AddOperator().use { newUInt64AddOperator ->
                    opt.setMergeOperator(newUInt64AddOperator)
                    openRocksDB(
                        opt,
                        testFolder
                    ).use {
                        //no-op
                    }
                }
            }
        }
    }

    @Test
    fun emptyStringInSetMergeOperatorByName() {
        Options().apply {
            setMergeOperatorName("")
        }.use {
            ColumnFamilyOptions().apply {
                setMergeOperatorName("")
            }.use {
                //no-op
            }
        }
    }
}
