@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.set
import rocksdb.rocksdb_column_family_handle_destroy

internal inline fun <T : RocksDB> wrapOpenedDb(
    closeNativeDb: () -> Unit,
    releaseComparator: () -> AbstractComparator?,
    createDb: (List<AbstractComparator>) -> T,
): T {
    var comparator: AbstractComparator? = null
    var db: T? = null
    try {
        comparator = releaseComparator()
        db = createDb(listOfNotNull(comparator))
        comparator = null
        return db
    } catch (throwable: Throwable) {
        db?.close() ?: run {
            closeNativeDb()
            comparator?.closeFromOptions()
        }
        throw throwable
    }
}

internal inline fun <T : RocksDB> wrapOpenedColumnFamilies(
    handles: CArrayPointer<CPointerVar<rocksdb_column_family_handle_t>>,
    count: Int,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>,
    closeNativeDb: () -> Unit,
    createDb: (List<AbstractComparator>) -> T,
): T {
    val outputStartSize = columnFamilyHandles.size
    val wrappedHandles = ArrayList<ColumnFamilyHandle>(count)
    val ownedComparators = ArrayList<AbstractComparator>(columnFamilyDescriptors.size)
    var pendingComparator: AbstractComparator? = null
    var db: T? = null

    try {
        for (descriptor in columnFamilyDescriptors) {
            val comparator = descriptor.getOptions().releaseOwnedComparator()
            if (comparator != null) {
                pendingComparator = comparator
                ownedComparators += comparator
                pendingComparator = null
            }
        }

        db = createDb(ownedComparators)

        repeat(count) { index ->
            val handle = ColumnFamilyHandle(handles[index]!!)
            try {
                wrappedHandles += handle
                columnFamilyHandles += handle
            } catch (throwable: Throwable) {
                handle.close()
                handles[index] = null
                throw throwable
            }
        }

        return db
    } catch (throwable: Throwable) {
        try {
            while (columnFamilyHandles.size > outputStartSize) {
                columnFamilyHandles.removeAt(columnFamilyHandles.lastIndex)
            }
        } catch (_: Throwable) {
        }

        wrappedHandles.forEach { it.close() }
        for (index in wrappedHandles.size until count) {
            handles[index]?.let { rocksdb_column_family_handle_destroy(it) }
        }

        db?.close() ?: run {
            closeNativeDb()
            ownedComparators.forEach { it.closeFromOptions() }
            pendingComparator?.closeFromOptions()
        }
        throw throwable
    }
}
