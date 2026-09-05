@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import cnames.structs.rocksdb_export_import_files_metadata_t
import cnames.structs.rocksdb_transactiondb_t
import kotlinx.cinterop.*
import rocksdb.maryk_rocksdb_transactiondb_create_column_family_with_import
import rocksdb.maryk_rocksdb_transactiondb_create_column_family_with_import_list
import rocksdb.maryk_rocksdb_transactiondb_create_column_family_with_length
import maryk.asSizeT
import maryk.asUInt32
import maryk.sizeTToInt
import maryk.toBoolean
import maryk.toCheckedLong
import maryk.usePointer
import platform.posix.size_tVar
import kotlin.experimental.ExperimentalNativeApi

private fun transactionBaseDb(native: CPointer<rocksdb_transactiondb_t>) =
    requireNotNull(rocksdb.rocksdb_transactiondb_get_base_db(native)) {
        "RocksDB returned null base DB for transaction DB"
    }
actual open class TransactionDB
internal constructor(
    internal val tnative: CPointer<rocksdb_transactiondb_t>,
    ownedComparators: List<AbstractComparator> = emptyList(),
    retainedReferences: List<Any> = emptyList(),
) : RocksDB(transactionBaseDb(tnative), ownedComparators, retainedReferences), TransactionOwner {
    val defaultTransactionOptions: TransactionOptions = TransactionOptions()
    private val borrowedTransactions = mutableSetOf<Transaction>()

    override fun close() {
        withLifecycleLock {
            if (tryClose()) {
                invalidateBorrowedTransactions()
                invalidateBorrowedIterators()
                invalidateBorrowedTransactionLogIterators()
                releaseBorrowedSnapshotsLocked()
                invalidateColumnFamilyHandles()
                defaultTransactionOptions.close()
                closeDefaultReferences()
                rocksdb.rocksdb_transactiondb_close_base_db(native)
                rocksdb.rocksdb_transactiondb_close(tnative)
                closeOwnedComparators()
                clearRetainedReferences()
                super.close()
            }
        }
    }

    /**
     * Column families are created and dropped through [tnative], not through the base database
     * this class hands to [RocksDB].
     *
     * PessimisticTransactionDB overrides each of these to keep its lock manager in step: creation
     * registers a lock map for the new column family, dropping releases it. Reached through the
     * base database instead, the transaction layer never learns the column family exists, and
     * every locking call on it — a transactional put, a get-for-update — fails with "Column family
     * id not found", while plain reads keep working. The JVM never had to say this out loud: its
     * handle is the transaction database itself, so C++ dispatches to the right override.
     */
    override fun nativeCreateColumnFamily(
        columnFamilyOptions: ColumnFamilyOptions,
        name: ByteArray,
        error: CValuesRef<CPointerVar<ByteVar>>
    ): CPointer<rocksdb_column_family_handle_t>? = memScoped {
        maryk_rocksdb_transactiondb_create_column_family_with_length(
            tnative,
            columnFamilyOptions.native,
            columnFamilyNameToCString(name),
            name.size.toULong(),
            error
        )
    }

    override fun nativeCreateColumnFamilyWithImport(
        columnFamilyOptions: ColumnFamilyOptions,
        name: ByteArray,
        importColumnFamilyOptions: ImportColumnFamilyOptions,
        metadata: ExportImportFilesMetaData,
        error: CValuesRef<CPointerVar<ByteVar>>
    ): CPointer<rocksdb_column_family_handle_t>? = memScoped {
        maryk_rocksdb_transactiondb_create_column_family_with_import(
            tnative,
            columnFamilyOptions.native,
            columnFamilyNameToCString(name),
            name.size.toULong(),
            importColumnFamilyOptions.native,
            metadata.native,
            error
        )
    }

    override fun nativeCreateColumnFamilyWithImportList(
        columnFamilyOptions: ColumnFamilyOptions,
        name: ByteArray,
        importColumnFamilyOptions: ImportColumnFamilyOptions,
        metadata: List<ExportImportFilesMetaData>,
        error: CValuesRef<CPointerVar<ByteVar>>
    ): CPointer<rocksdb_column_family_handle_t>? = memScoped {
        val metadataArray = allocArray<CPointerVar<rocksdb_export_import_files_metadata_t>>(metadata.size)
        metadata.forEachIndexed { index, item ->
            metadataArray[index] = item.native
        }
        maryk_rocksdb_transactiondb_create_column_family_with_import_list(
            tnative,
            columnFamilyOptions.native,
            columnFamilyNameToCString(name),
            name.size.toULong(),
            importColumnFamilyOptions.native,
            metadataArray,
            metadata.size.toULong(),
            error
        )
    }

    override fun nativeDropColumnFamily(
        columnFamilyHandle: ColumnFamilyHandle,
        error: CValuesRef<CPointerVar<ByteVar>>
    ) {
        rocksdb.rocksdb_transactiondb_drop_column_family(tnative, columnFamilyHandle.native, error)
    }

    override fun nativeDropColumnFamilies(
        columnFamilies: List<ColumnFamilyHandle>,
        error: CValuesRef<CPointerVar<ByteVar>>
    ) = memScoped {
        val cfHandles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilies.size)
        columnFamilies.forEachIndexed { index, handle ->
            cfHandles[index] = handle.native
        }
        rocksdb.rocksdb_transactiondb_drop_column_families(
            tnative,
            cfHandles,
            columnFamilies.size.asSizeT(),
            error
        )
    }

    override fun registerBorrowedTransaction(transaction: Transaction) {
        withLifecycleLock { borrowedTransactions += transaction }
    }

    override fun unregisterBorrowedTransaction(transaction: Transaction) {
        withLifecycleLock { borrowedTransactions -= transaction }
    }

    override fun closeBorrowedTransaction(transaction: Transaction) {
        withLifecycleLock { transaction.closeFromOwner(this) }
    }

    private fun invalidateBorrowedTransactions() {
        if (borrowedTransactions.isEmpty()) return
        val transactions = borrowedTransactions.toList()
        borrowedTransactions.clear()
        transactions.forEach { it.invalidateFromOwner() }
    }

    actual fun getTransactionByName(transactionName: String): Transaction? {
        return withLifecycleLock {
            checkOwningHandle()
            val nameBytes = transactionName.encodeToByteArray()
            nameBytes.usePointer { namePointer ->
                rocksdb.rocksdb_transactiondb_get_transaction_by_name(
                    tnative,
                    namePointer,
                    nameBytes.size.asSizeT(),
                )?.let {
                    Transaction(
                        it,
                        ownsNative = false,
                        freeBorrowedWrapper = true,
                    ).attachTo(this)
                }
            }
        }
    }

    actual fun beginTransaction(writeOptions: WriteOptions): Transaction {
        return withLifecycleLock {
            checkOwningHandle()
            writeOptions.checkOwningHandle()
            requireNotNull(
                rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, null)
            ) {
                "RocksDB returned null transaction"
            }.let { Transaction(it).attachTo(this) }
        }
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions
    ): Transaction {
        return withLifecycleLock {
            checkOwningHandle()
            writeOptions.checkOwningHandle()
            transactionOptions.checkOwningHandle()
            requireNotNull(
                rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, transactionOptions.native, null)
            ) {
                "RocksDB returned null transaction"
            }.let { Transaction(it).attachTo(this) }
        }
    }

    actual fun beginTransaction(writeOptions: WriteOptions, oldTransaction: Transaction): Transaction {
        return withLifecycleLock {
            checkOwningHandle()
            writeOptions.checkOwningHandle()
            oldTransaction.prepareForReuse()
            rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, oldTransaction.native)
            oldTransaction.attachTo(this)
        }
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions,
        oldTransaction: Transaction
    ): Transaction {
        return withLifecycleLock {
            checkOwningHandle()
            writeOptions.checkOwningHandle()
            transactionOptions.checkOwningHandle()
            oldTransaction.prepareForReuse()
            rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, transactionOptions.native, oldTransaction.native)
            oldTransaction.attachTo(this)
        }
    }

    actual fun getAllPreparedTransactions(): List<Transaction> =
        withLifecycleLock { getAllPreparedTransactionsLocked() }

    private fun getAllPreparedTransactionsLocked(): List<Transaction> {
        checkOwningHandle()
        memScoped {
            val count = alloc<size_tVar>()
            val transactions = rocksdb.rocksdb_transactiondb_get_prepared_transactions(tnative, count.ptr)
            var preparedTransactions: ArrayList<Transaction>? = null
            var countInt = 0

            try {
                countInt = sizeTToInt(count.value, "prepared transaction count")
                val preparedTransactionsList = ArrayList<Transaction>(countInt)
                preparedTransactions = preparedTransactionsList
                if (countInt == 0) return preparedTransactionsList
                val transactionPointers = requireNotNull(transactions) {
                    "RocksDB returned null prepared transaction array for $countInt transactions"
                }
                transactionPointers.let {
                    for (index in 0 until countInt) {
                        val rawTransaction = requireNotNull(transactionPointers[index]) {
                            "RocksDB returned null prepared transaction at index $index"
                        }
                        transactionPointers[index] = null
                        val transaction = try {
                            Transaction(
                                rawTransaction,
                                ownsNative = false,
                                freeBorrowedWrapper = true,
                            ).attachTo(this@TransactionDB)
                        } catch (throwable: Throwable) {
                            rocksdb.rocksdb_transaction_destroy_wrapper(rawTransaction)
                            throw throwable
                        }
                        try {
                            preparedTransactionsList += transaction
                        } catch (throwable: Throwable) {
                            transaction.close()
                            throw throwable
                        }
                    }
                }
                return preparedTransactionsList
            } catch (throwable: Throwable) {
                preparedTransactions?.forEach { it.close() }
                throw throwable
            } finally {
                transactions?.let {
                    for (index in 0 until countInt) {
                        transactions[index]?.let { transaction ->
                            rocksdb.rocksdb_transaction_destroy_wrapper(transaction)
                        }
                    }
                    rocksdb.rocksdb_free(it)
                }
            }
        }
    }

    actual fun getLockStatusData(): Map<Long, KeyLockInfo> = memScoped {
        checkOwningHandle()
        val countVar = alloc<size_tVar>()
        val infos = rocksdb.rocksdb_transactiondb_get_lock_status_data(tnative, countVar.ptr)
        val count = sizeTToInt(countVar.value, "lock status count")
        try {
            if (count == 0) {
                return emptyMap()
            }
            val lockInfos = requireNotNull(infos) {
                "RocksDB returned null lock status data for $count locks"
            }
            buildMap(count) {
                for (index in 0 until count) {
                    val keyLength = alloc<size_tVar>()
                    val keyPointer = rocksdb.rocksdb_key_lock_info_key(lockInfos, index.asSizeT(), keyLength.ptr)
                    val transactionCount = sizeTToInt(
                        rocksdb.rocksdb_key_lock_info_transaction_id_count(lockInfos, index.asSizeT()),
                        "lock transaction id count"
                    )
                    val transactionIds = LongArray(transactionCount) { idIndex ->
                        rocksdb.rocksdb_key_lock_info_transaction_id(lockInfos, index.asSizeT(), idIndex.asSizeT())
                            .toCheckedLong("lock transaction id")
                    }
                    val columnFamilyId = rocksdb.rocksdb_key_lock_info_column_family_id(lockInfos, index.asSizeT()).toLong()
                    put(
                        columnFamilyId,
                        KeyLockInfo(
                            key = readString(keyPointer, keyLength.value, "lock key"),
                            transactionIDs = transactionIds,
                            exclusive = rocksdb.rocksdb_key_lock_info_exclusive(lockInfos, index.asSizeT()).toBoolean()
                        )
                    )
                }
            }
        } finally {
            if (infos != null) {
                rocksdb.rocksdb_key_lock_infos_destroy(infos, countVar.value)
            }
        }
    }

    actual fun getDeadlockInfoBuffer(): Array<DeadlockPath> = memScoped {
        checkOwningHandle()
        val countVar = alloc<size_tVar>()
        val paths = rocksdb.rocksdb_transactiondb_get_deadlock_info_buffer(tnative, countVar.ptr)
        val count = sizeTToInt(countVar.value, "deadlock path count")
        try {
            if (count == 0) {
                return emptyArray()
            }
            val deadlockPaths = requireNotNull(paths) {
                "RocksDB returned null deadlock info buffer for $count paths"
            }
            Array(count) { pathIndex ->
                val infoCount = sizeTToInt(
                    rocksdb.rocksdb_deadlock_path_info_count(deadlockPaths, pathIndex.asSizeT()),
                    "deadlock info count"
                )
                val infos = Array(infoCount) { infoIndex ->
                    val keyLength = alloc<size_tVar>()
                    val keyPointer = rocksdb.rocksdb_deadlock_info_waiting_key(
                        deadlockPaths,
                        pathIndex.asSizeT(),
                        infoIndex.asSizeT(),
                        keyLength.ptr
                    )
                    DeadlockInfo(
                        transactionID = rocksdb.rocksdb_deadlock_info_transaction_id(
                            deadlockPaths,
                            pathIndex.asSizeT(),
                            infoIndex.asSizeT()
                        ).toCheckedLong("deadlock transaction id"),
                        columnFamilyId = rocksdb.rocksdb_deadlock_info_column_family_id(
                            deadlockPaths,
                            pathIndex.asSizeT(),
                            infoIndex.asSizeT()
                        ).toLong(),
                        waitingKey = readString(keyPointer, keyLength.value, "deadlock waiting key"),
                        exclusive = rocksdb.rocksdb_deadlock_info_exclusive(
                            deadlockPaths,
                            pathIndex.asSizeT(),
                            infoIndex.asSizeT()
                        ).toBoolean()
                    )
                }
                DeadlockPath(
                    path = infos,
                    limitExceeded = rocksdb.rocksdb_deadlock_path_limit_exceeded(deadlockPaths, pathIndex.asSizeT()).toBoolean(),
                    deadlockTime = rocksdb.rocksdb_deadlock_path_deadlock_time(deadlockPaths, pathIndex.asSizeT())
                )
            }
        } finally {
            if (paths != null) {
                rocksdb.rocksdb_deadlock_paths_destroy(paths, countVar.value)
            }
        }
    }

    actual fun setDeadlockInfoBufferSize(targetSize: Int) {
        checkOwningHandle()
        rocksdb.rocksdb_transactiondb_set_deadlock_info_buffer_size(tnative, targetSize.asUInt32())
    }
}

private fun readString(pointer: CPointer<ByteVar>?, length: platform.posix.size_t, label: String): String {
    val lengthInt = sizeTToInt(length, "$label length")
    if (lengthInt == 0) return ""
    return requireNotNull(pointer) { "RocksDB returned null $label for $lengthInt bytes" }
        .readBytes(lengthInt)
        .decodeToString()
}
