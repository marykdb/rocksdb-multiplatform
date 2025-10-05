@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_flushoptions_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import maryk.toUByte
import rocksdb.rocksdb_flushoptions_create
import rocksdb.rocksdb_flushoptions_destroy
import rocksdb.rocksdb_flushoptions_set_allow_write_stall
import rocksdb.rocksdb_flushoptions_set_wait

actual class FlushOptions internal constructor(
    internal val native: CPointer<rocksdb_flushoptions_t>,
    private var waitForFlushValue: Boolean,
    private var allowWriteStallValue: Boolean
) : RocksObject() {
    actual constructor() : this(rocksdb_flushoptions_create()!!, false, false)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_flushoptions_destroy(native)
            super.close()
        }
    }

    actual fun setWaitForFlush(wait: Boolean): FlushOptions {
        waitForFlushValue = wait
        rocksdb_flushoptions_set_wait(native, wait.toUByte())
        return this
    }

    actual fun waitForFlush(): Boolean = waitForFlushValue

    actual fun setAllowWriteStall(allow: Boolean): FlushOptions {
        allowWriteStallValue = allow
        rocksdb_flushoptions_set_allow_write_stall(native, allow.toUByte())
        return this
    }

    actual fun allowWriteStall(): Boolean = allowWriteStallValue
}
