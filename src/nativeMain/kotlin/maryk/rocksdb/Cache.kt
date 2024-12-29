@file:OptIn(ExperimentalNativeApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_cache_t
import kotlinx.cinterop.CPointer
import kotlin.experimental.ExperimentalNativeApi

actual abstract class Cache() : RocksObject() {
    protected lateinit var native: CPointer<rocksdb_cache_t>

    actual fun getUsage(): Long {
        assert(isOwningHandle());
        return rocksdb.rocksdb_cache_get_usage(native).toLong()
    }

    actual fun getPinnedUsage(): Long {
        assert(isOwningHandle());
        return rocksdb.rocksdb_cache_get_pinned_usage(native).toLong()
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb.rocksdb_free(native)
        }
        super.close()
    }
}
