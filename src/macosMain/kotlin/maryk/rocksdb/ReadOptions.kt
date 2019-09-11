package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class ReadOptions private constructor(nativeHandle: CPointer<*>) : RocksObject(nativeHandle) {
    actual constructor() : this(newReadOptions())

    actual constructor(verifyChecksums: Boolean, fillCache: Boolean) : this(newReadOptions(verifyChecksums, fillCache))

    actual constructor(other: ReadOptions) : this(copyReadOptions(other.nativeHandle)) {
        TODO("implement")
    }

    actual fun verifyChecksums(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setVerifyChecksums(verifyChecksums: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun fillCache(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setFillCache(fillCache: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun snapshot(): Snapshot? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSnapshot(snapshot: Snapshot?): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun readTier(): ReadTier {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setReadTier(readTier: ReadTier): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tailing(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTailing(tailing: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun totalOrderSeek(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTotalOrderSeek(totalOrderSeek: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun prefixSameAsStart(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setPrefixSameAsStart(prefixSameAsStart: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun pinData(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setPinData(pinData: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun backgroundPurgeOnIteratorCleanup(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBackgroundPurgeOnIteratorCleanup(backgroundPurgeOnIteratorCleanup: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun readaheadSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setReadaheadSize(readaheadSize: Long): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSkippableInternalKeys(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSkippableInternalKeys(maxSkippableInternalKeys: Long): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ignoreRangeDeletions(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIgnoreRangeDeletions(ignoreRangeDeletions: Boolean): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIterateLowerBound(iterateLowerBound: Slice?): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun iterateLowerBound(): Slice? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIterateUpperBound(iterateUpperBound: Slice?): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun iterateUpperBound(): Slice? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTableFilter(tableFilter: AbstractTableFilter): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIterStartSeqnum(startSeqnum: Long): ReadOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun iterStartSeqnum(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun newReadOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newReadOptions(verifyChecksums: Boolean, fillCache: Boolean): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun copyReadOptions(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
