package maryk.rocksdb

import cnames.structs.rocksdb_sstfilereader_t
import kotlinx.cinterop.CPointer
import maryk.wrapWithErrorThrower
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_sstfilereader_create
import rocksdb.rocksdb_sstfilereader_destroy
import rocksdb.rocksdb_sstfilereader_get_table_properties
import rocksdb.rocksdb_sstfilereader_new_iterator
import rocksdb.rocksdb_sstfilereader_open
import rocksdb.rocksdb_sstfilereader_verify_checksum
import rocksdb.rocksdb_tableproperties_destroy

actual class SstFileReader actual constructor(
    options: Options
) : RocksObject() {
    private val native: CPointer<rocksdb_sstfilereader_t>
    private var ownedComparator: AbstractComparator? = null
    private var retainedReferences: List<Any> = emptyList()
    private val borrowedIterators = mutableSetOf<SstFileReaderIterator>()

    init {
        options.checkOwningHandle()
        retainedReferences = options.retainedNativeReferences()
        val created = requireNotNull(rocksdb_sstfilereader_create(options.native)) {
            "Unable to create SST file reader"
        }
        native = created
        ownedComparator = try {
            options.releaseOwnedComparator()
        } catch (throwable: Throwable) {
            rocksdb_sstfilereader_destroy(created)
            throw throwable
        }
    }

    actual fun open(filePath: String) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_sstfilereader_open(native, filePath, error)
        }
    }

    actual fun newIterator(readOptions: ReadOptions): SstFileReaderIterator {
        checkOwningHandle()
        readOptions.checkOpenForRead()
        val iterator = requireNotNull(rocksdb_sstfilereader_new_iterator(native, readOptions.native)) {
            "Unable to create SST file reader iterator"
        }
        return SstFileReaderIterator(iterator, this)
    }

    actual fun getTableProperties(): TableProperties {
        checkOwningHandle()
        val tableProperties = Unit.wrapWithNullErrorThrower { error ->
            rocksdb_sstfilereader_get_table_properties(native, error)
        } ?: throw RocksDBException("Unable to get SST file table properties")
        return try {
            TableProperties(tableProperties)
        } finally {
            rocksdb_tableproperties_destroy(tableProperties)
        }
    }

    actual fun verifyChecksum() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_sstfilereader_verify_checksum(native, error)
        }
    }

    internal fun unregisterBorrowedIterator(iterator: SstFileReaderIterator) {
        borrowedIterators.remove(iterator)
    }

    internal fun registerBorrowedIterator(iterator: SstFileReaderIterator) {
        borrowedIterators.add(iterator)
    }

    private fun invalidateBorrowedIterators() {
        if (borrowedIterators.isEmpty()) return
        val iterators = borrowedIterators.toList()
        borrowedIterators.clear()
        iterators.forEach { it.invalidateFromOwner() }
    }

    override fun close() {
        if (tryClose()) {
            invalidateBorrowedIterators()
            rocksdb_sstfilereader_destroy(native)
            ownedComparator?.closeFromOptions()
            ownedComparator = null
            retainedReferences = emptyList()
            super.close()
        }
    }
}
