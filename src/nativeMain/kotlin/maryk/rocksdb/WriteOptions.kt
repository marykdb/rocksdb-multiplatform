package maryk.rocksdb

import rocksdb.RocksDBWriteOptions

actual class WriteOptions private constructor(
    internal val native: RocksDBWriteOptions
) : RocksObject() {
    actual constructor() : this(RocksDBWriteOptions())

    actual fun setSync(flag: Boolean): WriteOptions {
        native.syncWrites = flag
        return this
    }

    actual fun sync() = native.syncWrites

    actual fun setDisableWAL(flag: Boolean): WriteOptions {
        native.disableWriteAheadLog = flag
        return this
    }

    actual fun disableWAL() = native.disableWriteAheadLog

    actual fun setIgnoreMissingColumnFamilies(ignoreMissingColumnFamilies: Boolean): WriteOptions {
        native.ignoreMissingColumnFamilies = ignoreMissingColumnFamilies
        return this
    }

    actual fun ignoreMissingColumnFamilies() = native.ignoreMissingColumnFamilies

    actual fun setNoSlowdown(noSlowdown: Boolean): WriteOptions {
        native.noSlowdown = noSlowdown
        return this
    }

    actual fun noSlowdown(): Boolean {
        return native.noSlowdown
    }

    actual fun setLowPri(lowPri: Boolean): WriteOptions {
        native.setLowPriority(lowPri)
        return this
    }

    actual fun lowPri(): Boolean {
        return native.lowPriority
    }
}
