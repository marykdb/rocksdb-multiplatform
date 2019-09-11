package maryk.rocksdb

actual object MemoryUtil {
    actual fun getApproximateMemoryUsageByType(
        dbs: List<RocksDB>?,
        caches: Set<Cache>?
    ): Map<MemoryUsageType, Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
