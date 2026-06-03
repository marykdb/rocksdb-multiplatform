@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)
@file:Suppress("unused")

package maryk.rocksdb

import kotlinx.cinterop.ptr
import cnames.structs.rocksdb_column_family_handle_t
import cnames.structs.rocksdb_column_family_metadata_t
import cnames.structs.rocksdb_export_import_files_metadata_t
import cnames.structs.rocksdb_iterator_t
import cnames.structs.rocksdb_pinnableslice_t
import cnames.structs.rocksdb_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pin
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import maryk.asSizeT
import maryk.asUInt64
import maryk.byteArrayToCPointer
import maryk.checkAndThrowRocksDBError
import maryk.consumeRocksDBError
import maryk.sizeTToInt
import maryk.toCheckedLong
import maryk.toBoolean
import maryk.toByteArray
import maryk.toUByte
import maryk.usePointer
import maryk.usePointers
import maryk.wrapWithErrorThrower
import maryk.wrapWithMultiErrorThrower
import maryk.wrapWithNullErrorThrower
import platform.posix.memcpy
import platform.posix.size_t
import platform.posix.size_tVar
import platform.posix.uint64_tVar
import rocksdb.rocksdb_cancel_all_background_work
import rocksdb.rocksdb_close
import rocksdb.rocksdb_column_family_metadata_get_level_count
import rocksdb.rocksdb_column_family_metadata_get_level_metadata
import rocksdb.rocksdb_compact_range
import rocksdb.rocksdb_compact_range_cf
import rocksdb.rocksdb_compact_range_cf_opt
import rocksdb.rocksdb_create_column_family
import rocksdb.rocksdb_create_iterator
import rocksdb.rocksdb_create_iterator_cf
import rocksdb.rocksdb_create_iterators
import rocksdb.rocksdb_get_updates_since
import rocksdb.rocksdb_delete
import rocksdb.rocksdb_delete_cf
import rocksdb.rocksdb_delete_file_in_range_cf
import rocksdb.rocksdb_delete_range_cf
import rocksdb.rocksdb_destroy_db
import rocksdb.rocksdb_disable_file_deletions
import rocksdb.rocksdb_drop_column_family
import rocksdb.rocksdb_enable_file_deletions
import rocksdb.rocksdb_flush
import rocksdb.rocksdb_flush_cf
import rocksdb.rocksdb_flush_cfs
import rocksdb.rocksdb_flush_wal
import rocksdb.rocksdb_get_column_family_metadata
import rocksdb.rocksdb_get_default_column_family_handle
import rocksdb.rocksdb_get_latest_sequence_number
import rocksdb.rocksdb_get_pinned
import rocksdb.rocksdb_get_pinned_cf
import rocksdb.rocksdb_free
import rocksdb.rocksdb_ingest_external_file
import rocksdb.rocksdb_ingest_external_file_cf
import rocksdb.rocksdb_iter_destroy
import rocksdb.rocksdb_key_may_exist
import rocksdb.rocksdb_key_may_exist_cf
import rocksdb.rocksdb_level_metadata_destroy
import rocksdb.rocksdb_level_metadata_get_file_count
import rocksdb.rocksdb_level_metadata_get_level
import rocksdb.rocksdb_level_metadata_get_size
import rocksdb.rocksdb_level_metadata_get_sst_file_metadata
import rocksdb.rocksdb_list_column_families
import rocksdb.rocksdb_list_column_families_destroy
import rocksdb.rocksdb_livefiles
import rocksdb.rocksdb_livefiles_column_family_name
import rocksdb.rocksdb_livefiles_count
import rocksdb.rocksdb_livefiles_destroy
import rocksdb.rocksdb_livefiles_largestkey
import rocksdb.rocksdb_livefiles_level
import rocksdb.rocksdb_livefiles_name
import rocksdb.rocksdb_livefiles_path
import rocksdb.rocksdb_livefiles_size
import rocksdb.rocksdb_livefiles_smallestkey
import rocksdb.rocksdb_merge
import rocksdb.rocksdb_merge_cf
import rocksdb.rocksdb_multi_get
import rocksdb.rocksdb_multi_get_cf
import rocksdb.rocksdb_property_int
import rocksdb.rocksdb_property_int_cf
import rocksdb.rocksdb_put
import rocksdb.rocksdb_put_cf
import rocksdb.rocksdb_pinnableslice_destroy
import rocksdb.rocksdb_pinnableslice_value
import rocksdb.rocksdb_sst_file_metadata_destroy
import rocksdb.rocksdb_sst_file_metadata_get_directory
import rocksdb.rocksdb_sst_file_metadata_get_relative_filename
import rocksdb.rocksdb_sst_file_metadata_get_size
import rocksdb.rocksdb_sst_file_metadata_get_smallestkey
import rocksdb.rocksdb_set_perf_level
import rocksdb.rocksdb_write
import rocksdb.rocksdb_try_catch_up_with_primary
import kotlin.experimental.ExperimentalNativeApi
import kotlin.math.min

actual val defaultColumnFamily = "default".encodeToByteArray()
actual val rocksDBNotFound = -1

private inline fun <T> CPointer<rocksdb_pinnableslice_t>.usePinnedSlice(block: (CPointer<rocksdb_pinnableslice_t>) -> T): T {
    try {
        return block(this)
    } finally {
        rocksdb_pinnableslice_destroy(this)
    }
}

private fun CPointer<rocksdb_pinnableslice_t>.toByteArray(): ByteArray = memScoped {
    val valueLength = alloc<size_tVar>()
    val value = rocksdb_pinnableslice_value(this@toByteArray, valueLength.ptr)
    if (valueLength.value == 0.asSizeT()) {
        ByteArray(0)
    } else {
        requireNotNull(value) {
            "RocksDB returned null pinned value for ${valueLength.value} bytes."
        }.toByteArray(valueLength.value)
    }
}

private fun CPointer<rocksdb_pinnableslice_t>.copyTo(value: ByteArray, offset: Int = 0, maxLength: Int = value.size): Int = memScoped {
    if (offset < 0 || maxLength < 0 || offset > value.size - maxLength) {
        throw IndexOutOfBoundsException("offset=$offset, length=$maxLength, array size=${value.size}")
    }
    val valueLength = alloc<size_tVar>()
    val pointer = rocksdb_pinnableslice_value(this@copyTo, valueLength.ptr)
    val length = sizeTToInt(valueLength.value, "RocksDB value")
    val copyLength = min(length, maxLength)
    if (copyLength > 0) {
        val source = requireNotNull(pointer) {
            "RocksDB returned null pinned value for ${valueLength.value} bytes."
        }
        value.usePinned { pinned ->
            memcpy(pinned.addressOf(offset), source, copyLength.asSizeT())
        }
    }
    return@memScoped length
}

private fun Holder<ByteArray>?.setKeyMayExistValue(
    valueFound: UByte,
    value: CPointer<ByteVar>?,
    valueLength: size_t,
) {
    this ?: return
    if (!valueFound.toBoolean()) {
        setValue(null)
        return
    }
    setValue(
        if (valueLength == 0.asSizeT()) {
            ByteArray(0)
        } else {
            requireNotNull(value) {
                "RocksDB reported a key-may-exist value with $valueLength bytes but returned null."
            }.toByteArray(valueLength)
        }
    )
}

private inline fun <T> usePinnedKeys(
    keys: List<ByteArray>,
    emptyKeyPointer: CPointer<ByteVar>,
    pointers: CPointer<CPointerVar<ByteVar>>,
    sizes: CPointer<size_tVar>,
    block: () -> T
): T {
    val pinnedKeys = ArrayList<kotlinx.cinterop.Pinned<ByteArray>>(keys.size)
    try {
        keys.forEachIndexed { index, bytes ->
            sizes[index] = bytes.size.asSizeT()
            if (bytes.isEmpty()) {
                pointers[index] = emptyKeyPointer
            } else {
                val pinned = bytes.pin()
                pinnedKeys += pinned
                pointers[index] = pinned.addressOf(0)
            }
        }
        return block()
    } finally {
        pinnedKeys.forEach { it.unpin() }
    }
}

private fun freePropertyMapEntries(
    entries: CPointer<CPointerVar<ByteVar>>,
    pointerCount: Int,
) {
    repeat(pointerCount) { index ->
        entries[index]?.let { rocksdb_free(it) }
    }
    rocksdb_free(entries)
}

private fun propertyMapPointerCount(numEntriesValue: size_t): Int {
    val numEntries = sizeTToInt(numEntriesValue, "property map entry count")
    if (numEntries > Int.MAX_VALUE / 2) {
        throw IllegalStateException("property map entry count is too large to release safely: $numEntries entries")
    }
    return numEntries * 2
}

private fun releaseInvalidPropertyMapEntries(
    entries: CPointer<CPointerVar<ByteVar>>,
    numEntriesValue: size_t,
) {
    val pointerCount = try {
        propertyMapPointerCount(numEntriesValue)
    } catch (throwable: Throwable) {
        rocksdb_free(entries)
        throw throwable
    }
    freePropertyMapEntries(entries, pointerCount)
}

internal inline fun createColumnFamilyHandle(
    crossinline create: (CValuesRef<CPointerVar<ByteVar>>) -> CPointer<rocksdb_column_family_handle_t>?,
): ColumnFamilyHandle = memScoped {
    val errorRef = alloc<CPointerVar<ByteVar>>()
    errorRef.value = null
    val handle = try {
        create(errorRef.ptr)
    } catch (throwable: Throwable) {
        throw consumeRocksDBError(errorRef) ?: throwable
    }

    // RocksDB's C create-column-family APIs allocate their handle wrapper before
    // calling DB::CreateColumnFamily. On error the returned wrapper may contain
    // an invalid rep, so do not pass an error result to the public destroy API.
    checkAndThrowRocksDBError(errorRef)
    val validHandle = requireNotNull(handle) { "RocksDB returned a null column family handle" }
    try {
        ColumnFamilyHandle(validHandle)
    } catch (throwable: Throwable) {
        rocksdb.rocksdb_column_family_handle_destroy(validHandle)
        throw throwable
    }
}

actual open class RocksDB
internal constructor(
    internal val native: CPointer<rocksdb_t>,
    ownedComparators: List<AbstractComparator> = emptyList(),
    retainedReferences: List<Any> = emptyList(),
)
    : RocksObject() {
    private val defaultReadOptions = ReadOptions()
    private val defaultWriteOptions = WriteOptions()
    private val ownedComparators = ownedComparators.toMutableList()
    private val retainedReferences = retainedReferences.toMutableList()
    private val borrowedIterators = mutableSetOf<RocksIterator>()
    private val borrowedTransactionLogIterators = mutableSetOf<TransactionLogIterator>()
    private val borrowedSnapshots = mutableSetOf<Snapshot>()
    private val columnFamilyHandles = mutableSetOf<ColumnFamilyHandle>()
    private var perfLevel: PerfLevel = PerfLevel.UNINITIALIZED

    private fun checkOpenForRead(readOptions: ReadOptions = defaultReadOptions) {
        checkOwningHandle()
        readOptions.checkOpenForRead()
    }

    private fun checkOpenForWrite(writeOptions: WriteOptions = defaultWriteOptions) {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
    }

    private fun checkOpenColumnFamily(columnFamilyHandle: ColumnFamilyHandle) {
        columnFamilyHandle.checkOwningHandle()
    }

    actual fun getName(): String {
        checkOwningHandle()
        val name = rocksdb.rocksdb_get_name(native) ?: return ""
        return try {
            name.toKString()
        } finally {
            rocksdb_free(name)
        }
    }

    actual override fun close() {
        if (tryClose()) {
            invalidateBorrowedIterators()
            invalidateBorrowedTransactionLogIterators()
            releaseBorrowedSnapshots()
            invalidateColumnFamilyHandles()
            closeDefaultReferences()
            rocksdb_close(native)
            closeOwnedComparators()
            clearRetainedReferences()
            super.close()
        }
    }

    internal fun unregisterBorrowedIterator(iterator: RocksIterator) {
        borrowedIterators.remove(iterator)
    }

    internal fun registerBorrowedIterator(iterator: RocksIterator) {
        borrowedIterators.add(iterator)
    }

    private fun borrowIterator(iterator: RocksIterator): RocksIterator =
        iterator.also(borrowedIterators::add)

    internal fun unregisterBorrowedTransactionLogIterator(iterator: TransactionLogIterator) {
        borrowedTransactionLogIterators.remove(iterator)
    }

    internal fun registerBorrowedTransactionLogIterator(iterator: TransactionLogIterator) {
        borrowedTransactionLogIterators.add(iterator)
    }

    private fun borrowTransactionLogIterator(iterator: TransactionLogIterator): TransactionLogIterator =
        iterator.also(borrowedTransactionLogIterators::add)

    internal fun unregisterBorrowedSnapshot(snapshot: Snapshot) {
        borrowedSnapshots.remove(snapshot)
    }

    internal fun registerBorrowedSnapshot(snapshot: Snapshot) {
        borrowedSnapshots.add(snapshot)
    }

    protected fun invalidateBorrowedIterators() {
        if (borrowedIterators.isEmpty()) return
        val iterators = borrowedIterators.toList()
        borrowedIterators.clear()
        iterators.forEach { it.invalidateFromOwner() }
    }

    protected fun invalidateBorrowedTransactionLogIterators() {
        if (borrowedTransactionLogIterators.isEmpty()) return
        val iterators = borrowedTransactionLogIterators.toList()
        borrowedTransactionLogIterators.clear()
        iterators.forEach { it.invalidateFromOwner() }
    }

    protected fun releaseBorrowedSnapshots() {
        if (borrowedSnapshots.isEmpty()) return
        val snapshots = borrowedSnapshots.toList()
        borrowedSnapshots.clear()
        snapshots.forEach { it.invalidateFromOwner(this) }
    }

    internal fun registerColumnFamilyHandle(handle: ColumnFamilyHandle): ColumnFamilyHandle {
        columnFamilyHandles += handle
        return handle.attachTo(this)
    }

    internal fun unregisterColumnFamilyHandle(handle: ColumnFamilyHandle) {
        columnFamilyHandles.remove(handle)
    }

    protected fun invalidateColumnFamilyHandles() {
        if (columnFamilyHandles.isEmpty()) return
        val handles = columnFamilyHandles.toList()
        columnFamilyHandles.clear()
        handles.forEach { it.invalidateFromOwner() }
    }

    protected fun closeDefaultReferences() {
        defaultReadOptions.close()
        defaultWriteOptions.close()
    }

    protected fun closeOwnedComparators() {
        ownedComparators.forEach { it.closeFromOptions() }
        ownedComparators.clear()
    }

    protected fun clearRetainedReferences() {
        retainedReferences.clear()
    }

    protected fun retainOwnedComparator(comparator: AbstractComparator) {
        ownedComparators += comparator
    }

    private fun retainNativeReferences(references: List<Any>) {
        retainedReferences.addAll(references)
    }

    internal fun closeNonOwningReferences() {
        closeDefaultReferences()
        closeOwnedComparators()
        clearRetainedReferences()
    }

    actual open fun closeE() {
        this.close()
    }

    actual fun createColumnFamily(columnFamilyDescriptor: ColumnFamilyDescriptor): ColumnFamilyHandle =
        run {
            checkOwningHandle()
            columnFamilyDescriptor.getOptions().checkOwningHandle()
            val handle = createColumnFamilyHandle { error ->
                memScoped {
                    rocksdb_create_column_family(
                        native,
                        columnFamilyDescriptor.getOptions().native,
                        columnFamilyNameToCString(columnFamilyDescriptor.getName()),
                        error
                    )
                }
            }
            try {
                columnFamilyDescriptor.getOptions().releaseOwnedComparator()?.let {
                    retainOwnedComparator(it)
                }
                retainNativeReferences(columnFamilyDescriptor.getOptions().retainedNativeReferences())
            } catch (throwable: Throwable) {
                handle.close()
                throw throwable
            }
            registerColumnFamilyHandle(handle)
        }

    actual fun createColumnFamilyWithImport(
        columnFamilyDescriptor: ColumnFamilyDescriptor,
        importColumnFamilyOptions: ImportColumnFamilyOptions,
        metadata: ExportImportFilesMetaData
    ): ColumnFamilyHandle {
        checkOwningHandle()
        columnFamilyDescriptor.getOptions().checkOwningHandle()
        importColumnFamilyOptions.checkOwningHandle()
        metadata.checkOwningHandle()
        val handle = createColumnFamilyHandle { error ->
            memScoped {
                rocksdb.maryk_rocksdb_create_column_family_with_import(
                    native,
                    columnFamilyDescriptor.getOptions().native,
                    columnFamilyNameToCString(columnFamilyDescriptor.getName()),
                    importColumnFamilyOptions.native,
                    metadata.native,
                    error
                )
            }
        }
        try {
            columnFamilyDescriptor.getOptions().releaseOwnedComparator()?.let {
                retainOwnedComparator(it)
            }
            retainNativeReferences(columnFamilyDescriptor.getOptions().retainedNativeReferences())
        } catch (throwable: Throwable) {
            handle.close()
            throw throwable
        }
        return registerColumnFamilyHandle(handle)
    }

    actual fun createColumnFamilyWithImport(
        columnFamilyDescriptor: ColumnFamilyDescriptor,
        importColumnFamilyOptions: ImportColumnFamilyOptions,
        metadata: List<ExportImportFilesMetaData>
    ): ColumnFamilyHandle {
        require(metadata.isNotEmpty()) { "metadata must not be empty" }
        checkOwningHandle()
        columnFamilyDescriptor.getOptions().checkOwningHandle()
        importColumnFamilyOptions.checkOwningHandle()
        metadata.forEach { it.checkOwningHandle() }
        val handle = createColumnFamilyHandle { error ->
            memScoped {
                val metadataArray = allocArray<CPointerVar<rocksdb_export_import_files_metadata_t>>(metadata.size)
                metadata.forEachIndexed { index, item ->
                    metadataArray[index] = item.native
                }
                rocksdb.maryk_rocksdb_create_column_family_with_import_list(
                    native,
                    columnFamilyDescriptor.getOptions().native,
                    columnFamilyNameToCString(columnFamilyDescriptor.getName()),
                    importColumnFamilyOptions.native,
                    metadataArray,
                    metadata.size.asSizeT(),
                    error
                )
            }
        }
        try {
            columnFamilyDescriptor.getOptions().releaseOwnedComparator()?.let {
                retainOwnedComparator(it)
            }
            retainNativeReferences(columnFamilyDescriptor.getOptions().retainedNativeReferences())
        } catch (throwable: Throwable) {
            handle.close()
            throw throwable
        }
        return registerColumnFamilyHandle(handle)
    }

    actual fun createColumnFamilies(
        columnFamilyOptions: ColumnFamilyOptions,
        columnFamilyNames: List<ByteArray>
    ): List<ColumnFamilyHandle> {
        checkOwningHandle()
        columnFamilyOptions.checkOwningHandle()
        val createdHandles = mutableListOf<ColumnFamilyHandle>()
        try {
            for (name in columnFamilyNames) {
                createdHandles += registerColumnFamilyHandle(createColumnFamilyHandle { error ->
                    memScoped {
                        rocksdb_create_column_family(
                            native,
                            columnFamilyOptions.native,
                            columnFamilyNameToCString(name),
                            error
                        )
                    }
                })
            }
            columnFamilyOptions.releaseOwnedComparator()?.let {
                retainOwnedComparator(it)
            }
            retainNativeReferences(columnFamilyOptions.retainedNativeReferences())
            return createdHandles.toList()
        } catch (throwable: Throwable) {
            if (createdHandles.isNotEmpty()) {
                columnFamilyOptions.releaseOwnedComparator()?.let {
                    retainOwnedComparator(it)
                }
            }
            createdHandles.forEach { it.close() }
            throw throwable
        }
    }

    actual fun createColumnFamilies(columnFamilyDescriptors: List<ColumnFamilyDescriptor>): List<ColumnFamilyHandle> {
        checkOwningHandle()
        columnFamilyDescriptors.forEach { it.getOptions().checkOwningHandle() }
        val createdHandles = mutableListOf<ColumnFamilyHandle>()
        try {
            for (descriptor in columnFamilyDescriptors) {
                createdHandles += registerColumnFamilyHandle(createColumnFamilyHandle { error ->
                    memScoped {
                        rocksdb_create_column_family(
                            native,
                            descriptor.getOptions().native,
                            columnFamilyNameToCString(descriptor.getName()),
                            error
                        )
                    }
                })
            }
            for (descriptor in columnFamilyDescriptors) {
                descriptor.getOptions().releaseOwnedComparator()?.let {
                    retainOwnedComparator(it)
                }
                retainNativeReferences(descriptor.getOptions().retainedNativeReferences())
            }
            return createdHandles.toList()
        } catch (throwable: Throwable) {
            for (index in createdHandles.indices) {
                columnFamilyDescriptors[index].getOptions().releaseOwnedComparator()?.let {
                    retainOwnedComparator(it)
                }
            }
            createdHandles.forEach { it.close() }
            throw throwable
        }
    }

    actual fun dropColumnFamily(columnFamilyHandle: ColumnFamilyHandle) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            rocksdb_drop_column_family(native, columnFamilyHandle.native, error)
        }
    }

    actual fun dropColumnFamilies(columnFamilies: List<ColumnFamilyHandle>) {
        if (columnFamilies.isEmpty()) return
        checkOwningHandle()
        columnFamilies.forEach(::checkOpenColumnFamily)
        wrapWithErrorThrower { error ->
            memScoped {
                val cfHandles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilies.size)

                columnFamilies.forEachIndexed { index, handle ->
                    cfHandles[index] = handle.native
                }

                rocksdb.rocksdb_drop_column_families(native, cfHandles, columnFamilies.size.asSizeT(), error)
            }
        }
    }

    actual fun put(key: ByteArray, value: ByteArray) {
        checkOpenForWrite()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_put(
                    native,
                    defaultWriteOptions.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        checkOpenForWrite()
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb_put(
                    native,
                    defaultWriteOptions.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    byteArrayToCPointer(value, vOffset, vLen),
                    vLen.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
        checkOpenForWrite()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_put_cf(
                    native,
                    defaultWriteOptions.native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        put(columnFamilyHandle, defaultWriteOptions, key, offset, len, value, vOffset, vLen)
    }

    actual fun put(writeOpts: WriteOptions, key: ByteArray, value: ByteArray) {
        checkOpenForWrite(writeOpts)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_put(
                    native,
                    writeOpts.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        checkOpenForWrite(writeOpts)
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb_put(
                    native,
                    writeOpts.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    byteArrayToCPointer(value, vOffset, vLen),
                    vLen.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        value: ByteArray
    ) {
        checkOpenForWrite(writeOpts)
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_put_cf(
                    native,
                    writeOpts.native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        checkOpenForWrite(writeOpts)
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb_put_cf(
                    native,
                    writeOpts.native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    byteArrayToCPointer(value, vOffset, vLen),
                    vLen.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun delete(key: ByteArray) {
        delete(defaultWriteOptions, key)
    }

    actual fun delete(key: ByteArray, offset: Int, len: Int) {
        delete(defaultWriteOptions, key, offset, len)
    }

    actual fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        delete(columnFamilyHandle, defaultWriteOptions, key)
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
        delete(columnFamilyHandle, defaultWriteOptions, key, offset, len)
    }

    actual fun delete(writeOpt: WriteOptions, key: ByteArray) {
        checkOpenForWrite(writeOpt)
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb_delete(
                    native,
                    writeOpt.native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun delete(
        writeOpt: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
        checkOpenForWrite(writeOpt)
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb_delete(
                    native,
                    writeOpt.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        key: ByteArray
    ) {
        checkOpenForWrite(writeOpt)
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb_delete_cf(
                    native,
                    writeOpt.native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
        checkOpenForWrite(writeOpt)
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb_delete_cf(
                    native,
                    writeOpt.native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        deleteRange(defaultWriteOptions, beginKey, endKey)
    }

    actual fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        beginKey: ByteArray,
        endKey: ByteArray
    ) {
        deleteRange(columnFamilyHandle, defaultWriteOptions, beginKey, endKey)
    }

    actual fun deleteRange(writeOpt: WriteOptions, beginKey: ByteArray, endKey: ByteArray) {
        checkOpenForWrite(writeOpt)
        wrapWithErrorThrower { error ->
            val default = rocksdb_get_default_column_family_handle(native)
            try {
                usePointers(beginKey, endKey) { beginPointer, endPointer ->
                    rocksdb_delete_range_cf(
                        db = native,
                        options = writeOpt.native,
                        column_family = default,
                        start_key = beginPointer,
                        start_key_len = beginKey.size.asSizeT(),
                        end_key = endPointer,
                        end_key_len = endKey.size.asSizeT(),
                        errptr = error,
                    )
                }
            } finally {
                rocksdb.rocksdb_column_family_handle_destroy(default)
            }
        }
    }

    actual fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        beginKey: ByteArray,
        endKey: ByteArray
    ) {
        checkOpenForWrite(writeOpt)
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(beginKey, endKey) { beginPointer, endPointer ->
                rocksdb_delete_range_cf(
                    db = native,
                    options = writeOpt.native,
                    column_family = columnFamilyHandle.native,
                    start_key = beginPointer,
                    start_key_len = beginKey.size.asSizeT(),
                    end_key = endPointer,
                    end_key_len = endKey.size.asSizeT(),
                    errptr = error,
                )
            }
        }
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
        merge(defaultWriteOptions, key, value)
    }

    actual fun merge(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        merge(defaultWriteOptions, key, offset, len, value, vOffset, vLen)
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
        merge(columnFamilyHandle, defaultWriteOptions, key, value)
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        merge(columnFamilyHandle, defaultWriteOptions, key, offset, len, value, vOffset, vLen)
    }

    actual fun merge(writeOpts: WriteOptions, key: ByteArray, value: ByteArray) {
        checkOpenForWrite(writeOpts)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_merge(
                    native,
                    writeOpts.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error,
                )
            }
        }
    }

    actual fun merge(
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        checkOpenForWrite(writeOpts)
        memScoped {
            wrapWithErrorThrower { error ->
                rocksdb_merge(
                    native,
                    writeOpts.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    byteArrayToCPointer(value, vOffset, vLen),
                    vLen.asSizeT(),
                    error,
                )
            }
        }
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        value: ByteArray
    ) {
        checkOpenForWrite(writeOpts)
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_merge_cf(
                    native,
                    writeOpts.native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error,
                )
            }
        }
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        checkOpenForWrite(writeOpts)
        checkOpenColumnFamily(columnFamilyHandle)
        memScoped {
            wrapWithErrorThrower { error ->
                rocksdb_merge_cf(
                    native,
                    writeOpts.native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    byteArrayToCPointer(value, vOffset, vLen),
                    vLen.asSizeT(),
                    error,
                )
            }
        }
    }

    actual fun write(writeOpts: WriteOptions, updates: WriteBatch) {
        checkOpenForWrite(writeOpts)
        updates.checkOpenHandle()
        wrapWithErrorThrower { error ->
            rocksdb_write(native, writeOpts.native, updates.native, error)
        }
    }

    actual fun get(key: ByteArray, value: ByteArray): Int = wrapWithNullErrorThrower { error ->
        checkOpenForRead()
        val result = key.usePointer { keyPointer ->
            rocksdb_get_pinned(native, defaultReadOptions.native, keyPointer, key.size.asSizeT(), error)
        }
        result?.usePinnedSlice { it.copyTo(value) }
    } ?: rocksDBNotFound

    actual fun get(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int {
        checkOpenForRead()
        memScoped {
            return wrapWithNullErrorThrower { error ->
                rocksdb_get_pinned(
                    native,
                    defaultReadOptions.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    error
                )?.usePinnedSlice { it.copyTo(value, vOffset, vLen) }
            } ?: rocksDBNotFound
        }
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ): Int =
        get(columnFamilyHandle, defaultReadOptions, key, value)

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int =
        get(columnFamilyHandle, defaultReadOptions, key, offset, len, value, vOffset, vLen)

    actual fun get(opt: ReadOptions, key: ByteArray, value: ByteArray): Int = wrapWithNullErrorThrower { error ->
        checkOpenForRead(opt)
        key.usePointer { keyPointer ->
            rocksdb_get_pinned(native, opt.native, keyPointer, key.size.asSizeT(), error)
                ?.usePinnedSlice { it.copyTo(value) }
        }
    } ?: rocksDBNotFound

    actual fun get(
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int {
        checkOpenForRead(opt)
        memScoped {
            return wrapWithNullErrorThrower { error ->
                rocksdb_get_pinned(
                    native,
                    opt.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    error
                )?.usePinnedSlice { it.copyTo(value, vOffset, vLen) }
            } ?: rocksDBNotFound
        }
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        value: ByteArray
    ): Int {
        checkOpenForRead(opt)
        checkOpenColumnFamily(columnFamilyHandle)
        return wrapWithNullErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb_get_pinned_cf(
                    native,
                    opt.native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )?.usePinnedSlice { it.copyTo(value) }
            }
        } ?: rocksDBNotFound
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int {
        checkOpenForRead(opt)
        checkOpenColumnFamily(columnFamilyHandle)
        memScoped {
            return wrapWithNullErrorThrower { error ->
                rocksdb_get_pinned_cf(
                    native,
                    opt.native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    error
                )?.usePinnedSlice { it.copyTo(value, vOffset, vLen) }
            } ?: rocksDBNotFound
        }
    }

    actual operator fun get(key: ByteArray): ByteArray? =
        get(defaultReadOptions, key)

    actual fun get(key: ByteArray, offset: Int, len: Int): ByteArray? =
        get(defaultReadOptions, key, offset, len)

    actual fun get(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray): ByteArray? =
        get(columnFamilyHandle, defaultReadOptions, key)

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? = get(columnFamilyHandle, defaultReadOptions, key, offset, len)

    actual fun get(opt: ReadOptions, key: ByteArray): ByteArray? =
        wrapWithNullErrorThrower { error ->
            checkOpenForRead(opt)
            val result = key.usePointer { keyPointer ->
                rocksdb_get_pinned(native, opt.native, keyPointer, key.size.asSizeT(), error)
            }
            result?.usePinnedSlice { it.toByteArray() }
        }

    actual fun get(
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? = wrapWithNullErrorThrower { error ->
        checkOpenForRead(opt)
        memScoped {
            val result = rocksdb_get_pinned(
                native,
                opt.native,
                byteArrayToCPointer(key, offset, len),
                len.asSizeT(),
                error
            )

            result?.usePinnedSlice { it.toByteArray() }
        }
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray
    ): ByteArray? = wrapWithNullErrorThrower { error ->
        checkOpenForRead(opt)
        checkOpenColumnFamily(columnFamilyHandle)
        val result = key.usePointer { keyPointer ->
            rocksdb_get_pinned_cf(
                native,
                opt.native,
                columnFamilyHandle.native,
                keyPointer,
                key.size.asSizeT(),
                error
            )
        }
        result?.usePinnedSlice { it.toByteArray() }
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? {
        checkOpenForRead(opt)
        checkOpenColumnFamily(columnFamilyHandle)
        return wrapWithNullErrorThrower { error ->
            memScoped {
                val result = rocksdb_get_pinned_cf(
                    native,
                    opt.native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, offset, len),
                    len.asSizeT(),
                    error
                )

                result?.usePinnedSlice { it.toByteArray() }
            }
        }
    }

    actual fun multiGetAsList(keys: List<ByteArray>): List<ByteArray?> =
        multiGetAsList(defaultReadOptions, keys)

    actual fun multiGetAsList(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> = multiGetAsList(defaultReadOptions, columnFamilyHandleList, keys)

    actual fun multiGetAsList(
        opt: ReadOptions,
        keys: List<ByteArray>
    ): List<ByteArray?> {
        checkOpenForRead(opt)
        if (keys.isEmpty()) return emptyList()

        return wrapWithMultiErrorThrower(keys.size) { error ->
            memScoped {
                val keyList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val keyListSizes = allocArray<size_tVar>(keys.size)
                val emptyKeyPointer = allocArray<ByteVar>(1)

                val valueList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val valueListSizes = allocArray<size_tVar>(keys.size)
                for (index in keys.indices) {
                    valueList[index] = null
                    valueListSizes[index] = 0u
                }

                usePinnedKeys(keys, emptyKeyPointer, keyList, keyListSizes) {
                    rocksdb_multi_get(
                        db = native,
                        options = opt.native,
                        num_keys = keys.size.asSizeT(),
                        keys_list = keyList,
                        keys_list_sizes = keyListSizes,
                        values_list = valueList,
                        values_list_sizes = valueListSizes,
                        errs = error,
                    )
                }

                try {
                    List(keys.size) { index ->
                        valueList[index]?.toByteArray(valueListSizes[index]) ?: get(opt, keys[index])
                    }
                } finally {
                    for (index in keys.indices) {
                        valueList[index]?.let { rocksdb_free(it) }
                    }
                }
            }
        } ?: emptyList()
    }

    @OptIn(ExperimentalStdlibApi::class)
    actual fun multiGetAsList(
        opt: ReadOptions,
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> {
        checkOpenForRead(opt)
        columnFamilyHandleList.forEach(::checkOpenColumnFamily)
        if (keys.isEmpty()) return emptyList()
        if (columnFamilyHandleList.size != keys.size) {
            throw IllegalArgumentException("For each key there must be a related column family handle.")
        }

        return wrapWithMultiErrorThrower(keys.size) { error ->
            memScoped {
                val columnFamilies = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyHandleList.size)
                columnFamilyHandleList.forEachIndexed { i, handle ->
                    columnFamilies[i] = handle.native
                }

                val keyList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val keyListSizes = allocArray<size_tVar>(keys.size)
                val emptyKeyPointer = allocArray<ByteVar>(1)

                val valueList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val valueListSizes = allocArray<size_tVar>(keys.size)
                for (index in keys.indices) {
                    valueList[index] = null
                    valueListSizes[index] = 0u
                }

                usePinnedKeys(keys, emptyKeyPointer, keyList, keyListSizes) {
                    rocksdb_multi_get_cf(
                        db = native,
                        options = opt.native,
                        column_families = columnFamilies,
                        num_keys = keys.size.asSizeT(),
                        keys_list = keyList,
                        keys_list_sizes = keyListSizes,
                        values_list = valueList,
                        values_list_sizes = valueListSizes,
                        errs = error,
                    )
                }

                try {
                    List(keys.size) { index ->
                        valueList[index]?.toByteArray(valueListSizes[index])
                            ?: get(columnFamilyHandleList[index], opt, keys[index])
                    }
                } finally {
                    for (index in keys.indices) {
                        valueList[index]?.let { rocksdb_free(it) }
                    }
                }
            }
        } ?: emptyList()
    }

    actual fun keyMayExist(key: ByteArray, valueHolder: Holder<ByteArray>?): Boolean =
        keyMayExist(defaultReadOptions, key, valueHolder)

    actual fun keyMayExist(
        key: ByteArray,
        offset: Int,
        len: Int,
        valueHolder: Holder<ByteArray>?
    ): Boolean =
        keyMayExist(defaultReadOptions, key, offset, len, valueHolder)

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        valueHolder: Holder<ByteArray>?
    ): Boolean =
        keyMayExist(columnFamilyHandle, defaultReadOptions, key, valueHolder)

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        valueHolder: Holder<ByteArray>?
    ): Boolean =
        keyMayExist(columnFamilyHandle, defaultReadOptions, key, offset, len, valueHolder)

    actual fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray,
        valueHolder: Holder<ByteArray>?
    ): Boolean {
        checkOpenForRead(readOptions)
        memScoped {
            val value = alloc<CPointerVar<ByteVar>>()
            val valueLength = alloc<size_tVar>()
            val valueFound = alloc<UByteVar>()
            value.value = null
            val mayExist = key.usePointer { keyPointer ->
                rocksdb_key_may_exist(
                    native,
                    readOptions.native,
                    keyPointer,
                    key.size.asSizeT(),
                    value.ptr,
                    valueLength.ptr,
                    null,
                    0.asSizeT(),
                    valueFound.ptr,
                )
            }
            try {
                valueHolder.setKeyMayExistValue(valueFound.value, value.value, valueLength.value)
                return mayExist.toBoolean()
            } finally {
                value.value?.let { rocksdb_free(it) }
            }
        }
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        valueHolder: Holder<ByteArray>?
    ): Boolean {
        checkOpenForRead(readOptions)
        memScoped {
            val value = alloc<CPointerVar<ByteVar>>()
            val valueLength = alloc<size_tVar>()
            val valueFound = alloc<UByteVar>()
            value.value = null
            val mayExist = rocksdb_key_may_exist(
                native,
                readOptions.native,
                byteArrayToCPointer(key, offset, len),
                len.asSizeT(),
                value.ptr,
                valueLength.ptr,
                null,
                0.asSizeT(),
                valueFound.ptr,
            )
            try {
                valueHolder.setKeyMayExistValue(valueFound.value, value.value, valueLength.value)
                return mayExist.toBoolean()
            } finally {
                value.value?.let { rocksdb_free(it) }
            }
        }
    }

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions,
        key: ByteArray,
        valueHolder: Holder<ByteArray>?
    ): Boolean {
        checkOpenForRead(readOptions)
        checkOpenColumnFamily(columnFamilyHandle)
        memScoped {
            val value = alloc<CPointerVar<ByteVar>>()
            val valueLength = alloc<size_tVar>()
            val valueFound = alloc<UByteVar>()
            value.value = null
            val mayExist = key.usePointer { keyPointer ->
                rocksdb_key_may_exist_cf(
                    native,
                    readOptions.native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    value.ptr,
                    valueLength.ptr,
                    null,
                    0.asSizeT(),
                    valueFound.ptr,
                )
            }
            try {
                valueHolder.setKeyMayExistValue(valueFound.value, value.value, valueLength.value)
                return mayExist > 0uL
            } finally {
                value.value?.let { rocksdb_free(it) }
            }
        }
    }

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        valueHolder: Holder<ByteArray>?
    ): Boolean {
        checkOpenForRead(readOptions)
        checkOpenColumnFamily(columnFamilyHandle)
        memScoped {
            val value = alloc<CPointerVar<ByteVar>>()
            val valueLength = alloc<size_tVar>()
            val valueFound = alloc<UByteVar>()
            value.value = null
            val mayExist = rocksdb_key_may_exist_cf(
                native,
                readOptions.native,
                columnFamilyHandle.native,
                byteArrayToCPointer(key, offset, len),
                len.asSizeT(),
                value.ptr,
                valueLength.ptr,
                null,
                0.asSizeT(),
                valueFound.ptr,
            )
            try {
                valueHolder.setKeyMayExistValue(valueFound.value, value.value, valueLength.value)
                return mayExist > 0uL
            } finally {
                value.value?.let { rocksdb_free(it) }
            }
        }
    }

    actual fun newIterator(): RocksIterator = newIterator(defaultReadOptions)

    actual fun newIterator(readOptions: ReadOptions): RocksIterator =
        run {
            checkOpenForRead(readOptions)
            borrowIterator(RocksIterator(
                requireNotNull(rocksdb_create_iterator(native, readOptions.native)) {
                    "RocksDB returned null iterator"
                },
                dbOwner = this,
            ))
        }

    actual fun newIterator(columnFamilyHandle: ColumnFamilyHandle): RocksIterator =
        newIterator(columnFamilyHandle, defaultReadOptions)

    actual fun newIterator(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions
    ): RocksIterator = run {
        checkOpenForRead(readOptions)
        checkOpenColumnFamily(columnFamilyHandle)
        borrowIterator(RocksIterator(
            requireNotNull(rocksdb_create_iterator_cf(native, readOptions.native, columnFamilyHandle.native)) {
                "RocksDB returned null column-family iterator"
            },
            dbOwner = this,
        ))
    }

    actual fun newIterators(columnFamilyHandleList: List<ColumnFamilyHandle>): List<RocksIterator> =
        newIterators(columnFamilyHandleList, defaultReadOptions)

    actual fun newIterators(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        readOptions: ReadOptions
    ): List<RocksIterator> = memScoped {
        checkOpenForRead(readOptions)
        if (columnFamilyHandleList.isEmpty()) return@memScoped emptyList()

        val columnFamilies = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyHandleList.size)
        val nativeIterators = allocArray<CPointerVar<rocksdb_iterator_t>>(columnFamilyHandleList.size)
        columnFamilyHandleList.forEachIndexed { index, handle ->
            checkOpenColumnFamily(handle)
            columnFamilies[index] = handle.native
            nativeIterators[index] = null
        }

        Unit.wrapWithErrorThrower { error ->
            rocksdb_create_iterators(
                db = native,
                opts = readOptions.native,
                column_families = columnFamilies,
                iterators = nativeIterators,
                size = columnFamilyHandleList.size.asSizeT(),
                errptr = error,
            )
        }

        val nativePointers = ArrayList<CPointer<rocksdb_iterator_t>>(columnFamilyHandleList.size)
        for (index in columnFamilyHandleList.indices) {
            val nativeIterator = nativeIterators[index]
            if (nativeIterator == null) {
                for (cleanupIndex in columnFamilyHandleList.indices) {
                    nativeIterators[cleanupIndex]?.let { rocksdb_iter_destroy(it) }
                }
                error("RocksDB returned null iterator at index $index")
            }
            nativePointers += nativeIterator
        }

        val wrappedIterators = ArrayList<RocksIterator>(nativePointers.size)
        var nextToWrap = 0
        try {
            for (nativeIterator in nativePointers) {
                nextToWrap++
                wrappedIterators += borrowIterator(RocksIterator(nativeIterator, dbOwner = this@RocksDB))
            }
            wrappedIterators
        } catch (throwable: Throwable) {
            wrappedIterators.forEach { it.close() }
            for (index in nextToWrap until nativePointers.size) {
                rocksdb_iter_destroy(nativePointers[index])
            }
            throw throwable
        }
    }

    actual fun getSnapshot(): Snapshot? {
        checkOwningHandle()
        return Snapshot(
            requireNotNull(rocksdb.rocksdb_create_snapshot(native)) {
                "RocksDB returned null snapshot"
            },
            this,
        )
    }

    actual fun releaseSnapshot(snapshot: Snapshot) {
        checkOwningHandle()
        snapshot.releaseFrom(this)
    }

    actual fun getProperty(
        columnFamilyHandle: ColumnFamilyHandle,
        property: String
    ): String? {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        return rocksdb.rocksdb_property_value_cf(native, columnFamilyHandle.native, property)?.let { value ->
            try {
                value.toKString()
            } finally {
                rocksdb_free(value)
            }
        }
    }

    actual fun getProperty(property: String): String? {
        checkOwningHandle()
        return rocksdb.rocksdb_property_value(native, property)?.let { value ->
            try {
                value.toKString()
            } finally {
                rocksdb_free(value)
            }
        }
    }

    actual fun getMapProperty(property: String): Map<String, String> {
        checkOwningHandle()
        memScoped {
            val numEntriesPtr = alloc<size_tVar>()
            numEntriesPtr.value = 0.asSizeT()
            val entrySizesPtr = alloc<CPointerVar<size_tVar>>()
            entrySizesPtr.value = null

            val mapInArray = rocksdb.rocksdb_property_map_value(
                native,
                property,
                numEntriesPtr.ptr,
                entrySizesPtr.ptr,
            ) ?: return emptyMap()

            val entrySizes = entrySizesPtr.value ?: run {
                releaseInvalidPropertyMapEntries(mapInArray, numEntriesPtr.value)
                throw RocksDBException("RocksDB returned property map entries without entry sizes for $property")
            }

            var pointerCount = 0
            try {
                pointerCount = propertyMapPointerCount(numEntriesPtr.value)
                val numEntries = pointerCount / 2
                return buildMap {
                    repeat(numEntries) { i ->
                        val keyIndex = i * 2
                        val valueIndex = keyIndex + 1
                        val key = requireNotNull(mapInArray[keyIndex]) {
                            "RocksDB returned null property map key at index $i"
                        }
                            .readBytes(sizeTToInt(entrySizes[keyIndex], "property map key"))
                            .decodeToString()
                        val value = requireNotNull(mapInArray[valueIndex]) {
                            "RocksDB returned null property map value at index $i"
                        }
                            .readBytes(sizeTToInt(entrySizes[valueIndex], "property map value"))
                            .decodeToString()
                        put(key, value)
                    }
                }
            } finally {
                freePropertyMapEntries(mapInArray, pointerCount)
                rocksdb_free(entrySizes)
            }
        }
    }

    actual fun getMapProperty(
        columnFamilyHandle: ColumnFamilyHandle,
        property: String
    ): Map<String, String> {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        memScoped {
            val numEntriesPtr = alloc<size_tVar>()
            numEntriesPtr.value = 0.asSizeT()
            val entrySizesPtr = alloc<CPointerVar<size_tVar>>()
            entrySizesPtr.value = null

            val entriesPtr = rocksdb.rocksdb_property_map_value_cf(
                native,
                columnFamilyHandle.native,
                property,
                numEntriesPtr.ptr,
                entrySizesPtr.ptr,
            ) ?: return emptyMap()

            val entrySizes = entrySizesPtr.value ?: run {
                releaseInvalidPropertyMapEntries(entriesPtr, numEntriesPtr.value)
                throw RocksDBException("RocksDB returned property map entries without entry sizes for $property")
            }

            var pointerCount = 0
            try {
                pointerCount = propertyMapPointerCount(numEntriesPtr.value)
                return buildMap {
                    // Iterate through pairs (even indices are keys, odd indices are values)
                    for (i in 0 until pointerCount step 2) {
                        val key = requireNotNull(entriesPtr[i]) {
                            "RocksDB returned null property map key at index ${i / 2}"
                        }
                            .readBytes(sizeTToInt(entrySizes[i], "property map key"))
                            .decodeToString()
                        val value = requireNotNull(entriesPtr[i + 1]) {
                            "RocksDB returned null property map value at index ${i / 2}"
                        }
                            .readBytes(sizeTToInt(entrySizes[i + 1], "property map value"))
                            .decodeToString()
                        put(key, value)
                    }
                }
            } finally {
                freePropertyMapEntries(entriesPtr, pointerCount)
                rocksdb_free(entrySizes)
            }
        }
    }

    actual fun getLongProperty(property: String): Long {
        checkOwningHandle()
        memScoped {
            val outValue = alloc<uint64_tVar>()
            val result = rocksdb_property_int(
                native,
                property,
                outValue.ptr,
            )
            if (result != 0) {
                throw RocksDBException("Unknown RocksDB property: $property")
            }
            return outValue.value.toCheckedLong("RocksDB property $property")
        }
    }

    actual fun getLongProperty(
        columnFamilyHandle: ColumnFamilyHandle,
        property: String
    ): Long {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        memScoped {
            val outValue = alloc<uint64_tVar>()
            val result = rocksdb_property_int_cf(
                native,
                columnFamilyHandle.native,
                property,
                outValue.ptr,
            )
            if (result != 0) {
                throw RocksDBException("Unknown RocksDB property: $property")
            }
            return outValue.value.toCheckedLong("RocksDB column-family property $property")
        }
    }

    actual fun resetStats() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_reset_stats(native, error)
        }
    }

    actual fun compactRange() {
        checkOwningHandle()
        rocksdb_compact_range(
            native,
            null,
            0u,
            null,
            0u,
        )
    }

    actual fun compactRange(columnFamilyHandle: ColumnFamilyHandle) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        rocksdb_compact_range_cf(
            native,
            columnFamilyHandle.native,
            null,
            0u,
            null,
            0u,
        )
    }

    actual fun compactRange(begin: ByteArray, end: ByteArray) {
        checkOwningHandle()
        usePointers(begin, end) { beginPointer, endPointer ->
            rocksdb_compact_range(
                native,
                beginPointer,
                begin.size.asSizeT(),
                endPointer,
                end.size.asSizeT(),
            )
        }
    }

    actual fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle,
        begin: ByteArray,
        end: ByteArray
    ) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        usePointers(begin, end) { beginPointer, endPointer ->
            rocksdb_compact_range_cf(
                native,
                columnFamilyHandle.native,
                beginPointer,
                begin.size.asSizeT(),
                endPointer,
                end.size.asSizeT(),
            )
        }
    }

    actual fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle,
        begin: ByteArray,
        end: ByteArray,
        compactRangeOptions: CompactRangeOptions
    ) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        compactRangeOptions.checkOwningHandle()
        usePointers(begin, end) { beginPointer, endPointer ->
            rocksdb_compact_range_cf_opt(
                native,
                columnFamilyHandle.native,
                compactRangeOptions.native,
                beginPointer,
                begin.size.asSizeT(),
                endPointer,
                end.size.asSizeT(),
            )
        }
    }

    actual fun pauseBackgroundWork() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_pause_background_work(native)
        }
    }

    actual fun continueBackgroundWork() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_continue_background_work(native)
        }
    }

    actual fun cancelAllBackgroundWork(waitForExit: Boolean) {
        checkOwningHandle()
        rocksdb_cancel_all_background_work(native, waitForExit.toUByte())
    }

    actual fun enableAutoCompaction(columnFamilyHandles: List<ColumnFamilyHandle>) {
        if (columnFamilyHandles.isEmpty()) return
        checkOwningHandle()
        columnFamilyHandles.forEach(::checkOpenColumnFamily)

        wrapWithErrorThrower { error ->
            memScoped {
                val columnFamilyHandlePointers = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyHandles.size)
                columnFamilyHandles.forEachIndexed { index, handle ->
                    columnFamilyHandlePointers[index] = handle.native
                }

                // Call the native RocksDB function
                rocksdb.rocksdb_enable_auto_compaction(
                    db = native,
                    column_family_handles = columnFamilyHandlePointers,
                    num_handles = columnFamilyHandles.size.asSizeT(),
                    errptr = error
                )
            }
        }
    }

    actual fun numberLevels(): Int {
        checkOwningHandle()
        return rocksdb.rocksdb_number_levels(native)
    }

    actual fun numberLevels(columnFamilyHandle: ColumnFamilyHandle): Int {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        return rocksdb.rocksdb_number_levels_cf(native, columnFamilyHandle.native)
    }

    actual fun maxMemCompactionLevel(): Int {
        checkOwningHandle()
        return rocksdb.rocksdb_max_mem_compaction_level(native)
    }

    actual fun maxMemCompactionLevel(columnFamilyHandle: ColumnFamilyHandle): Int {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        return rocksdb.rocksdb_max_mem_compaction_level_cf(native, columnFamilyHandle.native)
    }

    actual fun level0StopWriteTrigger(): Int {
        checkOwningHandle()
        return rocksdb.rocksdb_level0_stop_write_trigger(native)
    }

    actual fun level0StopWriteTrigger(columnFamilyHandle: ColumnFamilyHandle): Int {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        return rocksdb.rocksdb_level0_stop_write_trigger_cf(native, columnFamilyHandle.native)
    }

    actual fun getEnv(): Env {
        return getDefaultEnv()
    }

    actual fun setPerfLevel(perfLevel: PerfLevel) {
        rocksdb_set_perf_level(perfLevel.value.toInt())
        this.perfLevel = perfLevel
    }

    actual fun getPerfLevel(): PerfLevel = perfLevel

    actual fun getPerfContext(): PerfContext = PerfContext()

    actual fun getLiveFilesMetaData(): List<LiveFileMetaData> = memScoped {
        checkOwningHandle()
        val liveFiles = rocksdb_livefiles(native) ?: return@memScoped emptyList()
        try {
            val count = rocksdb_livefiles_count(liveFiles)
            buildList {
                repeat(count) { index ->
                    val cfNamePtr = requireNotNull(rocksdb_livefiles_column_family_name(liveFiles, index)) {
                        "RocksDB returned null live-file column family name at index $index"
                    }
                    val cfName = cfNamePtr.toKString().encodeToByteArray()

                    val namePtr = requireNotNull(rocksdb_livefiles_name(liveFiles, index)) {
                        "RocksDB returned null live-file name at index $index"
                    }
                    val name = namePtr.toKString()
                    val pathPtr = requireNotNull(rocksdb_livefiles_path(liveFiles, index)) {
                        "RocksDB returned null live-file path at index $index"
                    }
                    val path = pathPtr.toKString()

                    val smallestLen = alloc<size_tVar>()
                    val largestLen = alloc<size_tVar>()
                    val smallestPtr = rocksdb_livefiles_smallestkey(liveFiles, index, smallestLen.ptr)
                    val largestPtr = rocksdb_livefiles_largestkey(liveFiles, index, largestLen.ptr)

                    val smallestKey = if (smallestLen.value == 0.asSizeT()) {
                        ByteArray(0)
                    } else {
                        requireNotNull(smallestPtr) {
                            "RocksDB returned null live-file smallest key at index $index"
                        }.toByteArray(smallestLen.value)
                    }
                    val largestKey = if (largestLen.value == 0.asSizeT()) {
                        ByteArray(0)
                    } else {
                        requireNotNull(largestPtr) {
                            "RocksDB returned null live-file largest key at index $index"
                        }.toByteArray(largestLen.value)
                    }

                    add(
                        LiveFileMetaData(
                            columnFamilyNameValue = cfName,
                            levelValue = rocksdb_livefiles_level(liveFiles, index),
                            fileName = name,
                            path = path,
                            size = rocksdb_livefiles_size(liveFiles, index).convert(),
                            smallestKey = smallestKey,
                            largestKey = largestKey,
                        )
                    )
                }
            }
        } finally {
            rocksdb_livefiles_destroy(liveFiles)
        }
    }

    actual fun getUpdatesSince(sequenceNumber: Long): TransactionLogIterator =
        wrapWithErrorThrower { error ->
            checkOwningHandle()
            borrowTransactionLogIterator(TransactionLogIterator(
                requireNotNull(rocksdb_get_updates_since(native, sequenceNumber.asUInt64(), null, error)) {
                    "RocksDB returned null WAL iterator without an error"
                },
                this,
            ))
        }

    actual fun flush(flushOptions: FlushOptions) {
        checkOwningHandle()
        flushOptions.checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_flush(native, flushOptions.native, error)
        }
    }

    actual fun flush(flushOptions: FlushOptions, columnFamilyHandle: ColumnFamilyHandle) {
        checkOwningHandle()
        flushOptions.checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            rocksdb_flush_cf(native, flushOptions.native, columnFamilyHandle.native, error)
        }
    }

    actual fun flush(flushOptions: FlushOptions, columnFamilyHandles: List<ColumnFamilyHandle>) {
        if (columnFamilyHandles.isEmpty()) return
        checkOwningHandle()
        flushOptions.checkOwningHandle()
        columnFamilyHandles.forEach(::checkOpenColumnFamily)
        wrapWithErrorThrower { error ->
            memScoped {
                val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyHandles.size)
                columnFamilyHandles.forEachIndexed { index, handle ->
                    handles[index] = handle.native
                }
                rocksdb_flush_cfs(native, flushOptions.native, handles, columnFamilyHandles.size, error)
            }
        }
    }

    actual fun flushWal(sync: Boolean) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_flush_wal(native, sync.toUByte(), error)
        }
    }

    actual fun syncWal() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_sync_wal(native, error)
        }
    }

    actual fun getLatestSequenceNumber(): Long =
        run {
            checkOwningHandle()
            rocksdb_get_latest_sequence_number(native).toCheckedLong("latest sequence number")
        }

    actual fun disableFileDeletions() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_disable_file_deletions(native, error)
        }
    }

    actual fun enableFileDeletions() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_enable_file_deletions(native, error)
        }
    }

    internal fun processColumnFamilyMetaData(metaData: CPointer<rocksdb_column_family_metadata_t>?): ColumnFamilyMetaData {
        requireNotNull(metaData) { "RocksDB returned null column family metadata" }
        try {
            val levelCount = sizeTToInt(rocksdb_column_family_metadata_get_level_count(metaData), "column family metadata level count")

            val levels = memScoped {
                buildList {
                    for (i in 0 until levelCount) {
                        val levelData = requireNotNull(rocksdb_column_family_metadata_get_level_metadata(metaData, i.asSizeT())) {
                            "RocksDB returned null level metadata for level index $i"
                        }
                        try {
                            val count = sizeTToInt(rocksdb_level_metadata_get_file_count(levelData), "level metadata file count")

                            val files =
                                buildList {
                                    for (i in 0 until count) {
                                        val sstMetaData = requireNotNull(rocksdb_level_metadata_get_sst_file_metadata(levelData, i.asSizeT())) {
                                            "RocksDB returned null SST file metadata for file index $i"
                                        }
                                        var fileName: CPointer<ByteVar>? = null
                                        var directory: CPointer<ByteVar>? = null
                                        var smallestKey: CPointer<ByteVar>? = null
                                        var largestKey: CPointer<ByteVar>? = null
                                        try {
                                            val smallestKeyLength = this@memScoped.alloc<size_tVar>()
                                            val largestKeyLength = this@memScoped.alloc<size_tVar>()

                                            fileName = rocksdb_sst_file_metadata_get_relative_filename(sstMetaData)
                                            directory = rocksdb_sst_file_metadata_get_directory(sstMetaData)
                                            smallestKey = rocksdb_sst_file_metadata_get_smallestkey(sstMetaData, smallestKeyLength.ptr)
                                            largestKey = rocksdb.rocksdb_sst_file_metadata_get_largestkey(sstMetaData, largestKeyLength.ptr)
                                            val smallestKeyBytes = if (smallestKeyLength.value == 0.asSizeT()) {
                                                ByteArray(0)
                                            } else {
                                                requireNotNull(smallestKey) {
                                                    "RocksDB returned null SST smallest key"
                                                }.toByteArray(smallestKeyLength.value)
                                            }
                                            val largestKeyBytes = if (largestKeyLength.value == 0.asSizeT()) {
                                                ByteArray(0)
                                            } else {
                                                requireNotNull(largestKey) {
                                                    "RocksDB returned null SST largest key"
                                                }.toByteArray(largestKeyLength.value)
                                            }
                                            add(
                                                SstFileMetaData(
                                                    fileName = requireNotNull(fileName) {
                                                        "RocksDB returned null SST file name"
                                                    }.toKString(),
                                                    path = requireNotNull(directory) {
                                                        "RocksDB returned null SST directory"
                                                    }.toKString(),
                                                    size = rocksdb_sst_file_metadata_get_size(sstMetaData),
                                                    smallestKey = smallestKeyBytes,
                                                    largestKey = largestKeyBytes,
                                                )
                                            )
                                        } finally {
                                            fileName?.let { rocksdb_free(it) }
                                            directory?.let { rocksdb_free(it) }
                                            smallestKey?.let { rocksdb_free(it) }
                                            largestKey?.let { rocksdb_free(it) }
                                            rocksdb_sst_file_metadata_destroy(sstMetaData)
                                        }
                                    }
                                }
                            add(
                                LevelMetaData(
                                    level = rocksdb_level_metadata_get_level(levelData),
                                    size = rocksdb_level_metadata_get_size(levelData),
                                    files = files,
                                )
                            )
                        } finally {
                            rocksdb_level_metadata_destroy(levelData)
                        }
                    }
                }
            }

            val name = rocksdb.rocksdb_column_family_metadata_get_name(metaData)
            try {
                return ColumnFamilyMetaData(
                    size = rocksdb.rocksdb_column_family_metadata_get_size(metaData),
                    fileCount = rocksdb.rocksdb_column_family_metadata_get_file_count(metaData).convert(),
                    name = requireNotNull(name) {
                        "RocksDB returned null column family metadata name"
                    }.toKString(),
                    levels = levels
                )
            } finally {
                name?.let { rocksdb_free(it) }
            }
        } finally {
            rocksdb.rocksdb_column_family_metadata_destroy(metaData)
        }
    }

    actual fun getColumnFamilyMetaData(columnFamilyHandle: ColumnFamilyHandle): ColumnFamilyMetaData {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        val metaData = rocksdb.rocksdb_get_column_family_metadata_cf(native, columnFamilyHandle.native)

        return processColumnFamilyMetaData(metaData)
    }

    actual fun getColumnFamilyMetaData(): ColumnFamilyMetaData {
        checkOwningHandle()
        val metaData = rocksdb_get_column_family_metadata(native)

        return processColumnFamilyMetaData(metaData)
    }

    private inline fun ingestFiles(
        filePaths: List<String>,
        ingestOptions: IngestExternalFileOptions,
        crossinline action: (CPointer<CPointerVar<ByteVar>>, size_t, CValuesRef<CPointerVar<ByteVar>>) -> Unit
    ) {
        if (filePaths.isEmpty()) {
            return
        }
        memScoped {
            val names = allocArray<CPointerVar<ByteVar>>(filePaths.size)
            filePaths.forEachIndexed { index, path ->
                names[index] = path.cstr.ptr
            }
            wrapWithErrorThrower { error ->
                action(names, filePaths.size.asSizeT(), error)
            }
        }
    }

    actual fun ingestExternalFile(
        filePaths: List<String>,
        ingestOptions: IngestExternalFileOptions
    ) {
        checkOwningHandle()
        ingestOptions.checkOwningHandle()
        ingestFiles(filePaths, ingestOptions) { files, count, error ->
            rocksdb_ingest_external_file(native, files, count, ingestOptions.native, error)
        }
    }

    actual fun ingestExternalFile(
        columnFamilyHandle: ColumnFamilyHandle,
        filePaths: List<String>,
        ingestOptions: IngestExternalFileOptions
    ) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        ingestOptions.checkOwningHandle()
        ingestFiles(filePaths, ingestOptions) { files, count, error ->
            rocksdb_ingest_external_file_cf(
                native,
                columnFamilyHandle.native,
                files,
                count,
                ingestOptions.native,
                error
            )
        }
    }

    actual fun verifyChecksum() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_verify_checksum(native, error)
        }
    }

    actual fun tryCatchUpWithPrimary() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_try_catch_up_with_primary(native, error)
        }
    }

    actual fun getDefaultColumnFamily(): ColumnFamilyHandle {
        checkOwningHandle()
        return registerColumnFamilyHandle(ColumnFamilyHandle(
            requireNotNull(rocksdb_get_default_column_family_handle(native)) {
                "RocksDB returned null default column family handle"
            }
        ))
    }

    actual fun promoteL0(columnFamilyHandle: ColumnFamilyHandle, targetLevel: Int) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_promote_l0(native, columnFamilyHandle.native, targetLevel, error)
        }
    }

    actual fun promoteL0(targetLevel: Int) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            val defaultColumnFamily = getDefaultColumnFamily()
            try {
                rocksdb.rocksdb_promote_l0(native, defaultColumnFamily.native, targetLevel, error)
            } finally {
                defaultColumnFamily.close()
            }
        }
    }

    actual fun deleteFilesInRanges(
        columnFamilyHandle: ColumnFamilyHandle,
        ranges: List<ByteArray>,
        includeEnd: Boolean
    ) {
        if (ranges.isEmpty()) return
        require(ranges.size % 2 == 0) { "ranges must be pairs of [from, to]" }
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)

        var i = 0
        while (i < ranges.size) {
            val begin = ranges[i]
            val end = ranges[i + 1]

            // Emulate inclusive end by using an exclusive upper bound of end + 0x00
            val limit = if (includeEnd) {
                ByteArray(end.size + 1).also { dst ->
                    end.copyInto(dst)
                    dst[end.size] = 0
                }
            } else end

            wrapWithErrorThrower { error ->
                usePointers(begin, limit) { beginPointer, limitPointer ->
                    rocksdb_delete_file_in_range_cf(
                        db = native,
                        column_family = columnFamilyHandle.native,
                        start_key = beginPointer,
                        start_key_len = begin.size.asSizeT(),
                        limit_key = limitPointer,
                        limit_key_len = limit.size.asSizeT(),
                        errptr = error,
                    )
                }
            }

            i += 2
        }
    }
}

actual fun destroyRocksDB(path: String, options: Options) {
    options.checkOwningHandle()
    Unit.wrapWithErrorThrower { error ->
        rocksdb_destroy_db(options.native, path, error)
    }
}

actual fun listColumnFamilies(
    options: Options,
    path: String
): List<ByteArray> {
    options.checkOwningHandle()
    return Unit.wrapWithErrorThrower { error ->
        memScoped {
            val cfCount = alloc<size_tVar>()
            val values = rocksdb_list_column_families(options.native, path, cfCount.ptr, error)

            try {
                val count = sizeTToInt(cfCount.value, "column family count")
                if (count == 0) return@wrapWithErrorThrower emptyList()
                val columnFamilies = requireNotNull(values) {
                    "RocksDB returned null column family list for $count column families"
                }
                buildList {
                    for (i in 0 until count) {
                        columnFamilies[i]?.toKString()?.let {
                            add(it.encodeToByteArray())
                        } ?: throw RocksDBException("Missing column family name for index $i")
                    }
                }
            } finally {
                values?.let {
                    rocksdb_list_column_families_destroy(it, cfCount.value)
                }
            }
        }
    }
}
