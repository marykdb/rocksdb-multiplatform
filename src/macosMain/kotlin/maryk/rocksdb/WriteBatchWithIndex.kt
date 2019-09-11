package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class WriteBatchWithIndex : AbstractWriteBatch {
    actual constructor() : super(newWriteBatchWithIndex())

    actual constructor(overwriteKey: Boolean) : super(newWriteBatchWithIndex(overwriteKey))

    actual constructor(
        fallbackIndexComparator: AbstractComparator<out AbstractSlice<*>>,
        reservedBytes: Int,
        overwriteKey: Boolean
    ) : super(
        newWriteBatchWithIndex(
            fallbackIndexComparator.nativeHandle,
            fallbackIndexComparator.getComparatorType().getValue(),
            reservedBytes,
            overwriteKey
        )
    )

    actual fun newIterator(columnFamilyHandle: ColumnFamilyHandle): WBWIRocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIterator(): WBWIRocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIteratorWithBase(
        columnFamilyHandle: ColumnFamilyHandle,
        baseIterator: RocksIterator
    ): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIteratorWithBase(baseIterator: RocksIterator): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getFromBatch(
        columnFamilyHandle: ColumnFamilyHandle,
        options: DBOptions,
        key: ByteArray
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getFromBatch(options: DBOptions, key: ByteArray): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getFromBatchAndDB(
        db: RocksDB,
        columnFamilyHandle: ColumnFamilyHandle,
        options: ReadOptions,
        key: ByteArray
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getFromBatchAndDB(
        db: RocksDB,
        options: ReadOptions,
        key: ByteArray
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun singleDelete(key: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun newWriteBatchWithIndex(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newWriteBatchWithIndex(overwriteKey: Boolean): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newWriteBatchWithIndex(
    nativeHandle: CPointer<*>,
    value: Byte,
    reservedBytes: Int,
    overwriteKey: Boolean
): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
