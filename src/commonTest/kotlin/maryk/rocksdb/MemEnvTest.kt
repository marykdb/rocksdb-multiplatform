package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MemEnvTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun memEnvFillAndReopen() {
        val keys = arrayOf("aaa".encodeToByteArray(), "bbb".encodeToByteArray(), "ccc".encodeToByteArray())
        val values = arrayOf("foo".encodeToByteArray(), "bar".encodeToByteArray(), "baz".encodeToByteArray())

        MemEnv(getDefaultEnv()).use { env ->
            Options().apply {
                setCreateIfMissing(true)
                setEnv(env)
            }.use { options ->
                FlushOptions().apply {
                    setWaitForFlush(true)
                }.use { flushOptions ->
                    openRocksDB(options, "dir/db").use { db ->
                        // write key/value pairs using MemEnv
                        for (i in keys.indices) {
                            db.put(keys[i], values[i])
                        }

                        // read key/value pairs using MemEnv
                        for (i in keys.indices) {
                            assertContentEquals(values[i], db[keys[i]])
                        }

                        // Check iterator access
                        db.newIterator().use { iterator ->
                            iterator.seekToFirst()
                            for (i in keys.indices) {
                                assertTrue(iterator.isValid())
                                assertContentEquals(keys[i], iterator.key())
                                assertContentEquals(values[i], iterator.value())
                                iterator.next()
                            }
                            // reached end of database
                            assertFalse(iterator.isValid())
                        }

                        // flush
                        db.flush(flushOptions)

                        // read key/value pairs after flush using MemEnv
                        for (i in keys.indices) {
                            assertContentEquals(values[i], db[keys[i]])
                        }
                    }

                    options.setCreateIfMissing(false)

                    // After reopen the values shall still be in the mem env.
                    // as long as the env is not freed.
                    openRocksDB(options, "dir/db").use { db ->
                        // read key/value pairs using MemEnv
                        for (i in keys.indices) {
                            assertContentEquals(values[i], db[keys[i]])
                        }
                    }
                }
            }
        }
    }

    @Test
    fun multipleDatabaseInstances() {
        // db - keys
        val keys = arrayOf("aaa".encodeToByteArray(), "bbb".encodeToByteArray(), "ccc".encodeToByteArray())
        // otherDb - keys
        val otherKeys = arrayOf("111".encodeToByteArray(), "222".encodeToByteArray(), "333".encodeToByteArray())
        // values
        val values = arrayOf("foo".encodeToByteArray(), "bar".encodeToByteArray(), "baz".encodeToByteArray())

        MemEnv(getDefaultEnv()).use { env ->
            Options().apply {
                setCreateIfMissing(true)
                setEnv(env)
            }.use { options ->
                openRocksDB(options, "dir/db").use { db ->
                    openRocksDB(options, "dir/otherDb").use { otherDb ->
                        // write key/value pairs using MemEnv
                        // to db and to otherDb.
                        for (i in keys.indices) {
                            db.put(keys[i], values[i])
                            otherDb.put(otherKeys[i], values[i])
                        }

                        // verify key/value pairs after flush using MemEnv
                        for (i in keys.indices) {
                            // verify db
                            assertNull(db[otherKeys[i]])
                            assertContentEquals(values[i], db[keys[i]])

                            // verify otherDb
                            assertNull(otherDb[keys[i]])
                            assertContentEquals(values[i], otherDb[otherKeys[i]])
                        }
                    }
                }
            }
        }
    }

    @Test
    fun createIfMissingFalse() {
        MemEnv(getDefaultEnv()).use { env ->
            Options().apply {
                setCreateIfMissing(false)
                setEnv(env)
            }.use { options ->
                assertFailsWith<RocksDBException> {
                    openRocksDB(options, "db/dir").use {
                        // shall throw an exception because db dir does not
                        // exist.
                    }
                }
            }
        }
    }
}
