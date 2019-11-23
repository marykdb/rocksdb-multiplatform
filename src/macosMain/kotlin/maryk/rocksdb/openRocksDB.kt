package maryk.rocksdb

import maryk.wrapWithNullErrorThrower
import rocksdb.RocksDBColumnFamilyDescriptor
import rocksdb.RocksDBColumnFamilyHandle
import rocksdb.RocksDBColumnFamilyOptions
import rocksdb.RocksDBOptions
import rocksdb.columnFamilies
import rocksdb.createIfMissing

actual fun openRocksDB(path: String): RocksDB {
    return Unit.wrapWithNullErrorThrower { error ->
        rocksdb.RocksDB.databaseAtPath(
            path,
            RocksDBOptions().apply {
                createIfMissing = true
            },
            error
        )?.let { RocksDB(it) }
    } ?: throw RocksDBException("No Database could be opened at $path")
}

actual fun openRocksDB(options: Options, path: String): RocksDB {
    return Unit.wrapWithNullErrorThrower { error ->
        rocksdb.RocksDB.databaseAtPath(path, options.native, error)?.let { RocksDB(it) }
    } ?: throw RocksDBException("No Database could be opened at $path")
}

actual fun openRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB {
    val descriptors = createRocksDBColumnFamilyDescriptor(columnFamilyDescriptors)

    return Unit.wrapWithNullErrorThrower { error ->
        rocksdb.RocksDB.databaseAtPath(path, descriptors, RocksDBOptions(), error)?.let {
            RocksDB(it)
        }?.also { db ->
            convertAndAddColumnFamilyHandles(db, columnFamilyHandles)
        }
    } ?: throw RocksDBException("No Database could be opened at $path with given descriptors and handles for column families")
}

actual fun openRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB {
    val descriptors = createRocksDBColumnFamilyDescriptor(columnFamilyDescriptors)

    return Unit.wrapWithNullErrorThrower { error ->
        rocksdb.RocksDB.databaseAtPath(
            path,
            descriptors,
            RocksDBOptions(options.native, RocksDBColumnFamilyOptions()),
            error
        )?.let {
            RocksDB(it)
        }?.also { db ->
            convertAndAddColumnFamilyHandles(db, columnFamilyHandles)
        }
    } ?: throw RocksDBException("No Database could be opened at $path with given descriptors and handles for column families")
}

actual fun openReadOnlyRocksDB(path: String) = Unit.wrapWithNullErrorThrower { error ->
    rocksdb.RocksDB.databaseForReadOnlyAtPath(path, RocksDBOptions(), error)?.let {
        RocksDB(it)
    }
} ?: throw RocksDBException("No Database could be opened at $path")

actual fun openReadOnlyRocksDB(options: Options, path: String) = Unit.wrapWithNullErrorThrower { error ->
    rocksdb.RocksDB.databaseForReadOnlyAtPath(path, options.native, error)?.let {
        RocksDB(it)
    }
} ?: throw RocksDBException("No Database could be opened at $path")

actual fun openReadOnlyRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB {
    val descriptors = createRocksDBColumnFamilyDescriptor(columnFamilyDescriptors)

    return Unit.wrapWithNullErrorThrower { error ->
        rocksdb.RocksDB.databaseForReadOnlyAtPath(path, descriptors, RocksDBOptions(), error)?.let {
            RocksDB(it)
        }?.also { db ->
            convertAndAddColumnFamilyHandles(db, columnFamilyHandles)
        }
    } ?: throw RocksDBException("No Database could be opened at $path")
}

actual fun openReadOnlyRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB {
    val descriptors = createRocksDBColumnFamilyDescriptor(columnFamilyDescriptors)

    return Unit.wrapWithNullErrorThrower { error ->
        rocksdb.RocksDB.databaseAtPath(
            path,
            descriptors,
            RocksDBOptions(options.native, RocksDBColumnFamilyOptions()),
            error
        )?.let {
            RocksDB(it)
        }?.also { db ->
            convertAndAddColumnFamilyHandles(db, columnFamilyHandles)
        }
    } ?: throw RocksDBException("No Database could be opened at $path")
}

private fun convertAndAddColumnFamilyHandles(
    db: RocksDB,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
) {
    @Suppress("UNCHECKED_CAST")
    for (handle in (db.native.columnFamilies() as List<RocksDBColumnFamilyHandle>)) {
        val cfHandle = ColumnFamilyHandle(handle)
        columnFamilyHandles.add(cfHandle)
    }
}

private fun createRocksDBColumnFamilyDescriptor(columnFamilyDescriptors: List<ColumnFamilyDescriptor>): RocksDBColumnFamilyDescriptor {
    val descriptors = RocksDBColumnFamilyDescriptor()
    for (descriptor in columnFamilyDescriptors) {
        descriptors.addColumnFamilyWithName(descriptor.getName().decodeToString(), descriptor.getOptions().native)
    }
    return descriptors
}
