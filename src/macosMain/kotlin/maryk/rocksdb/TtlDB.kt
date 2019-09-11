package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class TtlDB
    private constructor(nativeHandle: CPointer<*>)
: RocksDB(nativeHandle) {
    actual fun createColumnFamilyWithTtl(
        columnFamilyDescriptor: ColumnFamilyDescriptor,
        ttl: Int
    ): ColumnFamilyHandle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual fun openTtlDB(options: Options, db_path: String): TtlDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openTtlDB(
    options: Options,
    db_path: String,
    ttl: Int,
    readOnly: Boolean
): TtlDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun openTtlDB(
    options: DBOptions,
    db_path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>,
    ttlValues: List<Int>,
    readOnly: Boolean
): TtlDB {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
