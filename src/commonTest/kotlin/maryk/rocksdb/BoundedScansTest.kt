package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.*

class BoundedScansTest {
    init { loadRocksDBLibrary() }

    @Test
    fun forwardAndReversePaginationRespectBoundsAndReturnFirstUnreadKey() {
        openRocksDB(createTestDBFolder("scan-pages")).use { db ->
            repeat(10) { db.put(byteArrayOf(it.toByte()), byteArrayOf((it + 20).toByte())) }
            for (reverse in listOf(false, true)) {
                val expected = (2..7).let { if (reverse) it.reversed().toList() else it.toList() }
                val keys = mutableListOf<Int>()
                var next: ByteArray? = null
                do {
                    val page = db.scanPage(2, byteArrayOf(2), byteArrayOf(8), next, reverse)
                    keys.addAll(page.entries.map { it.key[0].toInt() })
                    assertTrue(page.entries.size <= 2)
                    next = page.nextKey
                } while (next != null)
                assertEquals(expected, keys)
            }
            assertContentEquals(byteArrayOf(2), db.scanPage(1, byteArrayOf(2), byteArrayOf(8), byteArrayOf(0)).entries.single().key)
            assertContentEquals(byteArrayOf(7), db.scanPage(1, byteArrayOf(2), byteArrayOf(8), byteArrayOf(9), true).entries.single().key)
            assertTrue(db.scanPage(2, byteArrayOf(20), byteArrayOf(21)).entries.isEmpty())
            assertFailsWith<IllegalArgumentException> { db.scanPage(0) }
            assertFailsWith<IllegalArgumentException> { db.scanPage(-1) }
        }
    }

    @Test
    fun binaryPrefixesIncludeEmptyNulAndAllFfCases() {
        openRocksDB(createTestDBFolder("scan-prefixes")).use { db ->
            val keys = listOf(byteArrayOf(), byteArrayOf(0), byteArrayOf(0, -1), byteArrayOf(1), byteArrayOf(-1), byteArrayOf(-1, 0), byteArrayOf(-1, -1), byteArrayOf(-1, -1, 0))
            keys.forEach { db.put(it, it) }
            for (prefix in listOf(byteArrayOf(), byteArrayOf(0), byteArrayOf(-1), byteArrayOf(-1, -1))) {
                val matching = keys.filter { key -> key.size >= prefix.size && prefix.indices.all { key[it] == prefix[it] } }
                for (reverse in listOf(false, true)) {
                    val expected = if (reverse) matching.reversed() else matching
                    val actual = mutableListOf<ScanEntry>()
                    var next: ByteArray? = null
                    do {
                        val page = db.scanPrefixPage(prefix, 2, next, reverse)
                        actual.addAll(page.entries)
                        next = page.nextKey
                    } while (next != null)
                    assertEquals(expected.size, actual.size)
                    expected.zip(actual).forEach { (key, entry) -> assertContentEquals(key, entry.key); assertContentEquals(key, entry.value) }
                }
            }
        }
    }

    @Test
    fun snapshotPaginationAndColumnFamiliesAreIsolatedAndResultsOutliveDb() {
        lateinit var saved: ScanPage
        openRocksDB(createTestDBFolder("scan-snapshot-cf")).use { db ->
            ColumnFamilyOptions().use { options ->
                db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), options)).use { cf ->
                    repeat(6) { db.put(cf, byteArrayOf(it.toByte()), byteArrayOf(it.toByte())) }
                    db.put(byteArrayOf(100), byteArrayOf(100))
                    db.withSnapshot { read ->
                        val first = db.scanPage(2, columnFamilyHandle = cf, snapshot = read.snapshot())
                        db.delete(cf, byteArrayOf(2))
                        db.put(cf, byteArrayOf(3), byteArrayOf(99))
                        db.put(cf, byteArrayOf(6), byteArrayOf(6))
                        saved = db.scanPage(10, startKey = first.nextKey, columnFamilyHandle = cf, snapshot = read.snapshot())
                        assertEquals(listOf(2, 3, 4, 5), saved.entries.map { it.key[0].toInt() })
                        assertContentEquals(byteArrayOf(3), saved.entries[1].value)
                        assertNull(saved.nextKey)
                    }
                    assertEquals(listOf(100), db.scanPage(20).entries.map { it.key[0].toInt() })
                    val owned = db.scanPage(1, columnFamilyHandle = cf).entries.single()
                    owned.value[0] = 77
                    assertContentEquals(byteArrayOf(0), db.get(cf, byteArrayOf(0)))
                }
            }
        }
        assertEquals(listOf(2, 3, 4, 5), saved.entries.map { it.key[0].toInt() })
    }

    @Test
    fun rangesUseDatabaseComparatorRatherThanAssumingBytewiseOrdering() {
        Options().setCreateIfMissing(true).setComparator(BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR).use { options ->
            openRocksDB(options, createTestDBFolder("scan-custom-order")).use { db ->
                repeat(6) { db.put(byteArrayOf(it.toByte()), byteArrayOf(it.toByte())) }
                assertEquals(listOf(4, 3, 2), db.scanPage(10, byteArrayOf(4), byteArrayOf(1)).entries.map { it.key[0].toInt() })
                assertEquals(listOf(2, 3, 4), db.scanPage(10, byteArrayOf(4), byteArrayOf(1), reverse = true).entries.map { it.key[0].toInt() })
                val first = db.scanPage(2, byteArrayOf(4), byteArrayOf(1))
                assertEquals(listOf(2), db.scanPage(2, byteArrayOf(4), byteArrayOf(1), first.nextKey).entries.map { it.key[0].toInt() })
            }
        }
    }
}
