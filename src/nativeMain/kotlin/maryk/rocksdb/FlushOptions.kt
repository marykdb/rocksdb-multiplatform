@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_flushoptions_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import maryk.toBoolean
import maryk.toUByte
import rocksdb.rocksdb_flushoptions_get_wait
import rocksdb.rocksdb_flushoptions_get_allow_write_stall
import rocksdb.rocksdb_flushoptions_get_listener_wait
import rocksdb.rocksdb_flushoptions_set_listener_wait
import rocksdb.rocksdb_flushoptions_create
import rocksdb.rocksdb_flushoptions_destroy
import rocksdb.rocksdb_flushoptions_set_allow_write_stall
import rocksdb.rocksdb_flushoptions_set_wait

actual class FlushOptions internal constructor(
    internal val native: CPointer<rocksdb_flushoptions_t>
) : RocksObject() {
    actual constructor() : this(
        requireNotNull(rocksdb_flushoptions_create()) { "Unable to allocate flush options" }
    )

    override fun close() {
        if (tryClose()) {
            rocksdb_flushoptions_destroy(native)
            super.close()
        }
    }

    actual fun setWaitForFlush(wait: Boolean): FlushOptions {
        checkOwningHandle()
        rocksdb_flushoptions_set_wait(native, wait.toUByte())
        return this
    }

    actual fun waitForFlush(): Boolean {
        checkOwningHandle()
        return rocksdb_flushoptions_get_wait(native).toBoolean()
    }

    actual fun setAllowWriteStall(allow: Boolean): FlushOptions {
        checkOwningHandle()
        rocksdb_flushoptions_set_allow_write_stall(native, allow.toUByte())
        return this
    }

    actual fun allowWriteStall(): Boolean {
        checkOwningHandle()
        return rocksdb_flushoptions_get_allow_write_stall(native).toBoolean()
    }

    actual fun setListenerWait(listenerWait: Boolean): FlushOptions {
        checkOwningHandle()
        rocksdb_flushoptions_set_listener_wait(native, listenerWait.toUByte())
        return this
    }

    actual fun listenerWait(): Boolean {
        checkOwningHandle()
        return rocksdb_flushoptions_get_listener_wait(native).toBoolean()
    }
}
