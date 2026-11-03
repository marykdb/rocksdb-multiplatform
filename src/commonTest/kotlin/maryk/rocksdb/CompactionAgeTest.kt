package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CompactionAgeTest {
    init { loadRocksDBLibrary() }

    @Test
    fun startupDefaultsAndResetsPreserveTheEngineSentinel() {
        Options().use { options ->
            assertEquals(engineDefaultCompactionAgeSeconds, options.ttl())
            assertEquals(engineDefaultCompactionAgeSeconds, options.periodicCompactionSeconds())
            options.setTtl(0).setPeriodicCompactionSeconds(0)
            assertEquals(0L, options.ttl())
            assertEquals(0L, options.periodicCompactionSeconds())
            options.setTtl(engineDefaultCompactionAgeSeconds)
                .setPeriodicCompactionSeconds(engineDefaultCompactionAgeSeconds)
            assertEquals(engineDefaultCompactionAgeSeconds, options.ttl())
            assertEquals(engineDefaultCompactionAgeSeconds, options.periodicCompactionSeconds())
        }
        ColumnFamilyOptions().use { options ->
            assertEquals(engineDefaultCompactionAgeSeconds, options.ttl())
            assertEquals(engineDefaultCompactionAgeSeconds, options.periodicCompactionSeconds())
            options.setTtl(0).setPeriodicCompactionSeconds(0)
                .setTtl(engineDefaultCompactionAgeSeconds)
                .setPeriodicCompactionSeconds(engineDefaultCompactionAgeSeconds)
            assertEquals(engineDefaultCompactionAgeSeconds, options.ttl())
            assertEquals(engineDefaultCompactionAgeSeconds, options.periodicCompactionSeconds())
        }
    }

    @Test
    fun liveResetAcceptsTheSentinelAndLeavesTheDatabaseUsable() {
        val path = createTestDBFolder("CompactionAgeTest_live_reset")
        Options().setCreateIfMissing(true).setTtl(7200).setPeriodicCompactionSeconds(3600).use { options ->
            openRocksDB(options, path).use { db ->
                ColumnFamilyOptions().setTtl(5400).setPeriodicCompactionSeconds(1800).use { cfOptions ->
                    db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions)).use { other ->
                        val original = db.effectiveColumnFamilyOptions(other)
                        assertEquals("5400", original["ttl"])
                        assertEquals("1800", original["periodic_compaction_seconds"])
                        mutableColumnFamilyOptionsBuilder().use { builder ->
                            builder.setTtl(0).setPeriodicCompactionSeconds(0)
                            builder.build().use { db.setOptions(it) }
                            builder.setTtl(engineDefaultCompactionAgeSeconds)
                                .setPeriodicCompactionSeconds(engineDefaultCompactionAgeSeconds)
                            assertEquals(engineDefaultCompactionAgeSeconds, builder.ttl())
                            assertEquals(engineDefaultCompactionAgeSeconds, builder.periodicCompactionSeconds())
                            builder.build().use { db.setOptions(it) }
                            assertEquals("-2", db.effectiveColumnFamilyOptions()["ttl"])
                            assertEquals("-2", db.effectiveColumnFamilyOptions()["periodic_compaction_seconds"])
                            assertEquals("5400", db.effectiveColumnFamilyOptions(other)["ttl"])
                            assertEquals("1800", db.effectiveColumnFamilyOptions(other)["periodic_compaction_seconds"])
                            builder.build().use { db.setOptions(other, it) }
                        }
                        assertEquals("-2", db.effectiveColumnFamilyOptions(other)["ttl"])
                        assertEquals("-2", db.effectiveColumnFamilyOptions(other)["periodic_compaction_seconds"])
                        assertEquals("-2", db.effectiveColumnFamilyOptions()["ttl"])
                        assertEquals("-2", db.effectiveColumnFamilyOptions()["periodic_compaction_seconds"])
                        assertEquals("5400", original["ttl"])
                        assertEquals("1800", original["periodic_compaction_seconds"])
                    }
                }
                db.put(byteArrayOf(1), byteArrayOf(2))
                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                assertContentEquals(byteArrayOf(2), db.get(byteArrayOf(1)))
            }
        }
        parseMutableColumnFamilyOptions("ttl=-2;periodic_compaction_seconds=-2").use { builder ->
            assertEquals(engineDefaultCompactionAgeSeconds, builder.ttl())
            assertEquals(engineDefaultCompactionAgeSeconds, builder.periodicCompactionSeconds())
        }
    }
}
