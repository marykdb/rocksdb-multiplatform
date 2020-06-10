package maryk.rocksdb

import rocksdb.RocksDBSstFileMetaData
import maryk.toByteArray

actual class SstFileMetaData(
    internal val native: RocksDBSstFileMetaData
) : RocksObject() {
    actual fun fileName(): String {
        return native.name
    }

    actual fun path(): String {
        return native.dbPath
    }

    actual fun size(): Long {
        return native.size.toLong()
    }

    actual fun smallestSeqno(): Long {
        return native.smallestSeqno.toLong()
    }

    actual fun largestSeqno(): Long {
        return native.largestSeqno.toLong()
    }

    actual fun smallestKey(): ByteArray {
        return native.smallestKey.toByteArray()
    }

    actual fun largestKey(): ByteArray {
        return native.largestKey.toByteArray()
    }

    actual fun numReadsSampled(): Long {
        return native.numReadsSampled().toLong()
    }

    actual fun beingCompacted(): Boolean {
        return native.beingCompacted
    }

    actual fun numEntries(): Long {
        return native.numEntries().toLong()
    }

    actual fun numDeletions(): Long {
        return native.numDeletions().toLong()
    }
}
