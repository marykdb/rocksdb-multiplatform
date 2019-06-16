package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransactionLogIteratorTest {
    private fun createTestFolder() = createTestDBFolder("TransactionLogIteratorTest")

    @Test
    fun transactionLogIterator() {
        Options().apply {
            setCreateIfMissing(true)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.getUpdatesSince(0).use {
                    //no-op
                }
            }
        }
    }

    @Test
    fun getBatch() {
        val numberOfPuts = 5
        Options().apply {
            setCreateIfMissing(true)
            setWalTtlSeconds(1000)
            setWalSizeLimitMB(10)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                for (i in 0 until numberOfPuts) {
                    db.put(
                        i.toString().encodeToByteArray(),
                        i.toString().encodeToByteArray()
                    )
                }
                db.flush(FlushOptions().setWaitForFlush(true))

                // the latest sequence number is 5 because 5 puts
                // were written beforehand
                assertEquals(numberOfPuts.toLong(), db.getLatestSequenceNumber())

                // insert 5 writes into a cf
                db.createColumnFamily(
                    ColumnFamilyDescriptor("new_cf".encodeToByteArray())
                ).use { cfHandle ->
                    for (i in 0 until numberOfPuts) {
                        db.put(
                            cfHandle, i.toString().encodeToByteArray(),
                            i.toString().encodeToByteArray()
                        )
                    }
                    // the latest sequence number is 10 because
                    // (5 + 5) puts were written beforehand
                    assertEquals((numberOfPuts + numberOfPuts).toLong(), db.getLatestSequenceNumber())

                    // Get updates since the beginning
                    db.getUpdatesSince(0).use { transactionLogIterator ->
                        assertTrue(transactionLogIterator.isValid())
                        transactionLogIterator.status()

                        // The first sequence number is 1
                        val batchResult = transactionLogIterator.getBatch()
                        assertEquals(1, batchResult.sequenceNumber())
                    }
                }
            }
        }
    }

    @Test
    fun transactionLogIteratorStallAtLastRecord() {
        Options().apply {
            setCreateIfMissing(true)
            setWalTtlSeconds(1000)
            setWalSizeLimitMB(10)
        }.use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                // Get updates since the beginning
                db.getUpdatesSince(0).use { transactionLogIterator ->
                    transactionLogIterator.status()
                    assertTrue(transactionLogIterator.isValid())
                    transactionLogIterator.next()
                    assertFalse(transactionLogIterator.isValid())
                    transactionLogIterator.status()
                    db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())
                    transactionLogIterator.next()
                    transactionLogIterator.status()
                    assertTrue(transactionLogIterator.isValid())
                }
            }
        }
    }

    @Test
    fun transactionLogIteratorCheckAfterRestart() {
        val numberOfKeys = 2
        val testFolder = createTestFolder()
        Options().apply {
            setCreateIfMissing(true)
            setWalTtlSeconds(1000)
            setWalSizeLimitMB(10)
        }.use { options ->
            openRocksDB(
                options,
                testFolder
            ).use { db ->
                db.put("key1".encodeToByteArray(), "value1".encodeToByteArray())
                db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())
                db.flush(FlushOptions().setWaitForFlush(true))
            }

            // reopen
            openRocksDB(
                options,
                testFolder
            ).use { db ->
                assertEquals(numberOfKeys.toLong(), db.getLatestSequenceNumber())

                db.getUpdatesSince(0).use { transactionLogIterator ->
                    for (i in 0 until numberOfKeys) {
                        transactionLogIterator.status()
                        assertTrue(transactionLogIterator.isValid())
                        transactionLogIterator.next()
                    }
                }
            }
        }
    }
}
