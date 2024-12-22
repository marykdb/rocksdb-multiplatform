@file:OptIn(ExperimentalNativeApi::class)

package maryk.rocksdb

import rocksdb.RocksDBReadOptions
import kotlin.experimental.ExperimentalNativeApi

actual class ReadOptions private constructor(val native: RocksDBReadOptions) : RocksObject() {
    actual constructor() : this(RocksDBReadOptions())

    actual fun verifyChecksums(): Boolean {
        assert(isOwningHandle())
        return native.verifyChecksums()
    }

    actual fun setVerifyChecksums(verifyChecksums: Boolean): ReadOptions {
        assert(isOwningHandle())
        native.setVerifyChecksums(verifyChecksums)
        return this
    }

    actual fun fillCache(): Boolean {
        assert(isOwningHandle())
        return native.fillCache()
    }

    actual fun setFillCache(fillCache: Boolean): ReadOptions {
        assert(isOwningHandle())
        native.setFillCache(fillCache)
        return this
    }

    actual fun prefixSameAsStart(): Boolean {
        assert(isOwningHandle())
        return native.prefixSameAsStart
    }

    actual fun setPrefixSameAsStart(prefixSameAsStart: Boolean): ReadOptions {
        native.prefixSameAsStart = prefixSameAsStart
        return this;
    }
}
