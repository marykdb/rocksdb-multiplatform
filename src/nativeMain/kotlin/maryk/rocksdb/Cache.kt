@file:OptIn(ExperimentalNativeApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_cache_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import maryk.sizeTToLong
import kotlin.experimental.ExperimentalNativeApi

actual abstract class Cache() : RocksObject() {
    internal lateinit var native: CPointer<rocksdb_cache_t>

    @OptIn(UnsafeNumber::class)
    actual fun getUsage(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb.rocksdb_cache_get_usage(native), "cache usage")
    }

    @OptIn(UnsafeNumber::class)
    actual fun getPinnedUsage(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb.rocksdb_cache_get_pinned_usage(native), "cache pinned usage")
    }

    override fun close() {
        if (tryClose()) {
            rocksdb.rocksdb_cache_destroy(native)
        }
        super.close()
    }
}
