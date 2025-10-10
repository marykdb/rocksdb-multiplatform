@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_write_buffer_manager_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT
import rocksdb.rocksdb_write_buffer_manager_create_with_cache
import rocksdb.rocksdb_write_buffer_manager_destroy

actual class WriteBufferManager internal constructor(
    internal val native: CPointer<rocksdb_write_buffer_manager_t>,
    private val allowStallFlag: Boolean,
) : RocksObject() {
    actual constructor(
        bufferSize: Long,
        cache: Cache,
        allowStall: Boolean,
    ) : this(
        requireNotNull(
            rocksdb_write_buffer_manager_create_with_cache(
                bufferSize.asSizeT(),
                cache.native,
                allowStall,
            ),
        ),
        allowStall,
    )

    actual constructor(
        bufferSize: Long,
        cache: Cache,
    ) : this(bufferSize, cache, false)

    actual fun allowStall(): Boolean = allowStallFlag

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_write_buffer_manager_destroy(native)
            super.close()
        }
    }
}
