package maryk.rocksdb

/** Key and value arrays owned by this result, independently of the iterator and DB. */
data class ScanEntry(val key: ByteArray, val value: ByteArray)

/**
 * A bounded page of copied entries. [nextKey] is the inclusive first unread key,
 * or null at the end. Pass it as [scanPage]'s startKey using the same bounds,
 * direction, column family and snapshot. Without a shared snapshot, intervening
 * writes can change subsequent pages.
 */
data class ScanPage(val entries: List<ScanEntry>, val nextKey: ByteArray?)

/**
 * Reads at most [limit] entries in DB comparator order, or its reverse when
 * [reverse] is true. Bounds are [lowerBound, upperBound) in that comparator's
 * order; RocksDB enforces them. [startKey] is inclusive and normally comes from
 * a previous page. A fresh iterator/read-options pair is closed before return.
 * [snapshot] is borrowed and must belong to this DB and remain open for the call.
 * Total-order seeks are enabled; the table format must support them.
 */
fun RocksDB.scanPage(
    limit: Int,
    lowerBound: ByteArray? = null,
    upperBound: ByteArray? = null,
    startKey: ByteArray? = null,
    reverse: Boolean = false,
    columnFamilyHandle: ColumnFamilyHandle? = null,
    snapshot: Snapshot? = null,
): ScanPage {
    require(limit > 0) { "Page limit must be positive" }
    val lower = lowerBound?.let { Slice(it.copyOf()) }
    try {
        val upper = upperBound?.let { Slice(it.copyOf()) }
        try {
            return ReadOptions().use { read ->
                read.setTotalOrderSeek(true)
                if (lower != null) read.setIterateLowerBound(lower)
                if (upper != null) read.setIterateUpperBound(upper)
                if (snapshot != null) read.setSnapshot(snapshot)
                val iterator = if (columnFamilyHandle == null) newIterator(read)
                else newIterator(columnFamilyHandle, read)
                iterator.use {
                    when {
                        startKey != null && reverse -> iterator.seekForPrev(startKey)
                        startKey != null -> iterator.seek(startKey)
                        reverse -> iterator.seekToLast()
                        else -> iterator.seekToFirst()
                    }
                    val entries = ArrayList<ScanEntry>(minOf(limit, 256))
                    while (entries.size < limit && iterator.isValid()) {
                        entries.add(ScanEntry(iterator.key().copyOf(), iterator.value().copyOf()))
                        if (reverse) iterator.prev() else iterator.next()
                    }
                    val nextKey = if (iterator.isValid()) iterator.key().copyOf() else null
                    iterator.status()
                    ScanPage(entries, nextKey)
                }
            }
        } finally {
            upper?.close()
        }
    } finally {
        lower?.close()
    }
}

/**
 * Scans a byte prefix using an unsigned bytewise comparator. This helper must
 * not be used with a different comparator; use [scanPage] with that comparator's
 * bounds instead. Empty prefixes cover the whole DB; all-FF prefixes have no
 * finite upper bound. Does not require a configured RocksDB prefix extractor.
 */
fun RocksDB.scanPrefixPage(
    prefix: ByteArray,
    limit: Int,
    startKey: ByteArray? = null,
    reverse: Boolean = false,
    columnFamilyHandle: ColumnFamilyHandle? = null,
    snapshot: Snapshot? = null,
): ScanPage = scanPage(
    limit = limit,
    lowerBound = prefix.takeIf { it.isNotEmpty() },
    upperBound = bytewisePrefixLimit(prefix),
    startKey = startKey,
    reverse = reverse,
    columnFamilyHandle = columnFamilyHandle,
    snapshot = snapshot,
)

private fun bytewisePrefixLimit(prefix: ByteArray): ByteArray? {
    for (index in prefix.lastIndex downTo 0) {
        val value = prefix[index].toInt() and 0xff
        if (value != 0xff) return prefix.copyOf(index + 1).also { it[index] = (value + 1).toByte() }
    }
    return null
}
