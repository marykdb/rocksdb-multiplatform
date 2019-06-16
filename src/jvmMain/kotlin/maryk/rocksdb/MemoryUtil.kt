package maryk.rocksdb

import org.rocksdb.MemoryUtil

actual object MemoryUtil {
    actual fun getApproximateMemoryUsageByType(
        dbs: List<RocksDB>?,
        caches: Set<Cache>?
    ) = MemoryUtil.getApproximateMemoryUsageByType(dbs, caches)
}
