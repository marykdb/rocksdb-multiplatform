package maryk.rocksdb

import maryk.toByteArray
import maryk.toNSData
import maryk.wrapWithNullErrorThrower
import rocksdb.RocksDBIndexedWriteBatch
import rocksdb.getFromBatchAndDB
import rocksdb.getFromBatchAndDBAndColumnFamily

actual class WriteBatchWithIndex(
    internal val native: RocksDBIndexedWriteBatch
) : AbstractWriteBatch(native) {
    actual constructor() : this(RocksDBIndexedWriteBatch())

    actual constructor(overwriteKey: Boolean) : this(RocksDBIndexedWriteBatch(overwriteKey))

    actual fun newIterator(columnFamilyHandle: ColumnFamilyHandle): WBWIRocksIterator {
        return WBWIRocksIterator(native.iteratorInColumnFamily(columnFamilyHandle.native))
    }

    actual fun newIterator(): WBWIRocksIterator {
        return WBWIRocksIterator(native.iterator())
    }

    actual fun newIteratorWithBase(
        columnFamilyHandle: ColumnFamilyHandle,
        baseIterator: RocksIterator
    ) = RocksIterator(
        native.iteratorWithBase(baseIterator.native, columnFamilyHandle.native)
    )

    actual fun newIteratorWithBase(baseIterator: RocksIterator) = RocksIterator(
        native.iteratorWithBase(baseIterator.native)
    )

    actual fun getFromBatch(
        columnFamilyHandle: ColumnFamilyHandle,
        options: DBOptions,
        key: ByteArray
    ) = wrapWithNullErrorThrower { error ->
        native.getFromBatchAndColumnFamily(options.native, columnFamilyHandle.native, key.toNSData(), error)?.toByteArray()
    }

    actual fun getFromBatch(options: DBOptions, key: ByteArray)= wrapWithNullErrorThrower { error ->
        native.getFromBatch(options.native, key.toNSData(), error)?.toByteArray()
    }

    actual fun getFromBatchAndDB(
        db: RocksDB,
        columnFamilyHandle: ColumnFamilyHandle,
        options: ReadOptions,
        key: ByteArray
    ) = wrapWithNullErrorThrower { error ->
        native.getFromBatchAndDBAndColumnFamily(db.native, options.native, columnFamilyHandle.native, key.toNSData(), error)?.toByteArray()
    }

    actual fun getFromBatchAndDB(
        db: RocksDB,
        options: ReadOptions,
        key: ByteArray
    ) = wrapWithNullErrorThrower { error ->
        native.getFromBatchAndDB(db.native, options.native, key.toNSData(), error)?.toByteArray()
    }
}
