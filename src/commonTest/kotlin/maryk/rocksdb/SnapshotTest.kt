package maryk.rocksdb

import maryk.assertContentEquals
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SnapshotTest {
    private fun createTestFolder() = createTestDBFolder("SnapshotTest")

    @Test
    fun snapshots() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                // Get new Snapshot of database
                db.getSnapshot().use { snapshot ->
                    assertNotNull(snapshot)
                    assertTrue(snapshot.getSequenceNumber() > 0)
                    assertEquals(1, snapshot.getSequenceNumber())
                    ReadOptions().use { readOptions ->
                        // set snapshot in ReadOptions
                        readOptions.setSnapshot(snapshot)

                        // retrieve key value pair
                        assertContentEquals("value".encodeToByteArray(), db.get("key".encodeToByteArray()))
                        // retrieve key value pair created before
                        // the snapshot was made
                        assertContentEquals(
                            "value".encodeToByteArray(),
                            db.get(
                                readOptions,
                                "key".encodeToByteArray()
                            )
                        )
                        // add new key/value pair
                        db.put("newkey".encodeToByteArray(), "newvalue".encodeToByteArray())
                        // using no snapshot the latest db entries
                        // will be taken into account
                        assertContentEquals("newvalue".encodeToByteArray(), db.get("newkey".encodeToByteArray()))
                        // snapshopot was created before newkey
                        assertNull(db.get(readOptions, "newkey".encodeToByteArray()))
                        // Retrieve snapshot from read options
                        readOptions.snapshot().use { sameSnapshot ->
                            readOptions.setSnapshot(sameSnapshot)
                            // results must be the same with new Snapshot
                            // instance using the same native pointer
                            assertContentEquals(
                                "value".encodeToByteArray(),
                                db.get(
                                    readOptions,
                                    "key".encodeToByteArray()
                                )
                            )
                            // update key value pair to newvalue
                            db.put("key".encodeToByteArray(), "newvalue".encodeToByteArray())
                            // read with previously created snapshot will
                            // read previous version of key value pair
                            assertContentEquals(
                                "value".encodeToByteArray(),
                                db.get(
                                    readOptions,
                                    "key".encodeToByteArray()
                                )
                            )
                            // read for newkey using the snapshot must be
                            // null
                            assertNull(db.get(readOptions, "newkey".encodeToByteArray()))
                            // setting null to snapshot in ReadOptions leads
                            // to no Snapshot being used.
                            readOptions.setSnapshot(null)
                            assertContentEquals(
                                "newvalue".encodeToByteArray(),
                                db.get(
                                    readOptions,
                                    "newkey".encodeToByteArray()
                                )
                            )
                            // release Snapshot
                            db.releaseSnapshot(snapshot)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun iteratorWithSnapshot() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())

                // Get new Snapshot of database
                // set snapshot in ReadOptions
                db.getSnapshot().use { snapshot ->
                    ReadOptions().setSnapshot(snapshot).use { readOptions ->
                        db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())

                        // iterate over current state of db
                        db.newIterator().use { iterator ->
                            iterator.seekToFirst()
                            assertTrue(iterator.isValid())
                            assertContentEquals("key".encodeToByteArray(), iterator.key())
                            iterator.next()
                            assertTrue(iterator.isValid())
                            assertContentEquals("key2".encodeToByteArray(), iterator.key())
                            iterator.next()
                            assertFalse(iterator.isValid())
                        }

                        // iterate using a snapshot
                        db.newIterator(readOptions).use { snapshotIterator ->
                            snapshotIterator.seekToFirst()
                            assertTrue(snapshotIterator.isValid())
                            assertContentEquals("key".encodeToByteArray(), snapshotIterator.key())
                            snapshotIterator.next()
                            assertFalse(snapshotIterator.isValid())
                        }

                        // release Snapshot
                        db.releaseSnapshot(snapshot)
                    }
                }
            }
        }
    }

    @Test
    fun iteratorWithSnapshotOnColumnFamily() {
        Options()
            .setCreateIfMissing(true).use { options ->
                openRocksDB(
                    options,
                    createTestFolder()
                ).use { db ->

                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())

                    // Get new Snapshot of database
                    // set snapshot in ReadOptions
                    db.getSnapshot().use { snapshot ->
                        ReadOptions()
                            .setSnapshot(snapshot).use { readOptions ->
                                db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())

                                // iterate over current state of column family
                                db.newIterator(
                                    db.getDefaultColumnFamily()
                                ).use { iterator ->
                                    iterator.seekToFirst()
                                    assertTrue(iterator.isValid())
                                    assertContentEquals("key".encodeToByteArray(), iterator.key())
                                    iterator.next()
                                    assertTrue(iterator.isValid())
                                    assertContentEquals("key2".encodeToByteArray(), iterator.key())
                                    iterator.next()
                                    assertFalse(iterator.isValid())
                                }

                                // iterate using a snapshot on default column family
                                db.newIterator(
                                    db.getDefaultColumnFamily(), readOptions
                                ).use { snapshotIterator ->
                                    snapshotIterator.seekToFirst()
                                    assertTrue(snapshotIterator.isValid())
                                    assertContentEquals("key".encodeToByteArray(), snapshotIterator.key())
                                    snapshotIterator.next()
                                    assertFalse(snapshotIterator.isValid())

                                    // release Snapshot
                                    db.releaseSnapshot(snapshot)
                                }
                            }
                    }
                }
            }
    }
}
