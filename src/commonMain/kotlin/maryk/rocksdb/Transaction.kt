package maryk.rocksdb

/**
 * Provides BEGIN/COMMIT/ROLLBACK transactions.
 *
 * To use transactions, you must first create either an
 * {@link OptimisticTransactionDB} or a {@link TransactionDB}
 *
 * To create a transaction, use
 * {@link OptimisticTransactionDB#beginTransaction(WriteOptions)} or
 * {@link TransactionDB#beginTransaction(oWriteOptions)}
 *
 * It is up to the caller to synchronize access to this object.
 */
expect class Transaction : RocksObject {
    /**
     * If a transaction has a snapshot set, the transaction will ensure that
     * any keys successfully written(or fetched via [.getForUpdate]) have
     * not been modified outside of this transaction since the time the snapshot
     * was set.
     *
     * If a snapshot has not been set, the transaction guarantees that keys have
     * not been modified since the time each key was first written (or fetched via
     * [.getForUpdate]).
     *
     * Using [.setSnapshot] will provide stricter isolation guarantees
     * at the expense of potentially more transaction failures due to conflicts
     * with other writes.
     *
     * Calling [.setSnapshot] has no effect on keys written before this
     * function has been called.
     *
     * [.setSnapshot] may be called multiple times if you would like to
     * change the snapshot used for different operations in this transaction.
     *
     * Calling [.setSnapshot] will not affect the version of Data returned
     * by get(...) methods. See [.get] for more details.
     */
    fun setSnapshot()

    /**
     * Similar to [.setSnapshot], but will not change the current snapshot
     * until put/merge/delete/getForUpdate/multiGetForUpdate is called.
     * By calling this function, the transaction will essentially call
     * [.setSnapshot] for you right before performing the next
     * write/getForUpdate.
     *
     * Calling [.setSnapshotOnNextOperation] will not affect what
     * snapshot is returned by [.getSnapshot] until the next
     * write/getForUpdate is executed.
     *
     * When the snapshot is created the notifier's snapshotCreated method will
     * be called so that the caller can get access to the snapshot.
     *
     * This is an optimization to reduce the likelihood of conflicts that
     * could occur in between the time [.setSnapshot] is called and the
     * first write/getForUpdate operation. i.e. this prevents the following
     * race-condition:
     *
     * txn1->setSnapshot();
     * txn2->put("A", ...);
     * txn2->commit();
     * txn1->getForUpdate(opts, "A", ...);  * FAIL!
     */
    fun setSnapshotOnNextOperation()

    /**
     * Similar to [.setSnapshot], but will not change the current snapshot
     * until put/merge/delete/getForUpdate/multiGetForUpdate is called.
     * By calling this function, the transaction will essentially call
     * [.setSnapshot] for you right before performing the next
     * write/getForUpdate.
     *
     * Calling [.setSnapshotOnNextOperation] will not affect what
     * snapshot is returned by [.getSnapshot] until the next
     * write/getForUpdate is executed.
     *
     * When the snapshot is created the
     * [AbstractTransactionNotifier.snapshotCreated] method will
     * be called so that the caller can get access to the snapshot.
     *
     * This is an optimization to reduce the likelihood of conflicts that
     * could occur in between the time [.setSnapshot] is called and the
     * first write/getForUpdate operation. i.e. this prevents the following
     * race-condition:
     *
     * txn1->setSnapshot();
     * txn2->put("A", ...);
     * txn2->commit();
     * txn1->getForUpdate(opts, "A", ...);  * FAIL!
     *
     * @param transactionNotifier A handler for receiving snapshot notifications
     * for the transaction
     */
    fun setSnapshotOnNextOperation(
        transactionNotifier: AbstractTransactionNotifier
    )

    /**
     * Returns the Snapshot created by the last call to [.setSnapshot].
     *
     * REQUIRED: The returned Snapshot is only valid up until the next time
     * [.setSnapshot]/[.setSnapshotOnNextOperation] is called,
     * [.clearSnapshot] is called, or the Transaction is deleted.
     *
     * @return The snapshot or null if there is no snapshot
     */
    fun getSnapshot(): Snapshot?

    /**
     * Clears the current snapshot (i.e. no snapshot will be 'set')
     *
     * This removes any snapshot that currently exists or is set to be created
     * on the next update operation ([.setSnapshotOnNextOperation]).
     *
     * Calling [.clearSnapshot] has no effect on keys written before this
     * function has been called.
     *
     * If a reference to a snapshot was retrieved via [.getSnapshot], it
     * will no longer be valid and should be discarded after a call to
     * [.clearSnapshot].
     */
    fun clearSnapshot()

    /**
     * Write all batched keys to the db atomically.
     *
     * Returns OK on success.
     *
     * May return any error status that could be returned by DB:Write().
     *
     * If this transaction was created by an [OptimisticTransactionDB]
     * Status::Busy() may be returned if the transaction could not guarantee
     * that there are no write conflicts. Status::TryAgain() may be returned
     * if the memtable history size is not large enough
     * (See max_write_buffer_number_to_maintain).
     *
     * If this transaction was created by a [TransactionDB],
     * Status::Expired() may be returned if this transaction has lived for
     * longer than [TransactionOptions.getExpiration].
     *
     * @throws RocksDBException if an error occurs when committing the transaction
     */
    fun commit()

    /**
     * Discard all batched writes in this transaction.
     * @throws RocksDBException if an error occurs when rolling back the transaction
     */
    fun rollback()

    /**
     * Records the state of the transaction for future calls to
     * [.rollbackToSavePoint].
     *
     * May be called multiple times to set multiple save points.
     *
     * @throws RocksDBException if an error occurs whilst setting a save point
     */
    fun setSavePoint()

    /**
     * Undo all operations in this transaction (put, merge, delete, putLogData)
     * since the most recent call to [.setSavePoint] and removes the most
     * recent [.setSavePoint].
     *
     * If there is no previous call to [.setSavePoint],
     * returns Status::NotFound()
     *
     * @throws RocksDBException if an error occurs when rolling back to a save point
     */
    fun rollbackToSavePoint()

    /**
     * This function is similar to
     * [RocksDB.get] except it will
     * also read pending changes in this transaction.
     * Currently, this function will return Status::MergeInProgress if the most
     * recent write to the queried key in this batch is a Merge.
     *
     * If [ReadOptions.snapshot] is not set, the current version of the
     * key will be read. Calling [.setSnapshot] does not affect the
     * version of the data returned.
     *
     * Note that setting [ReadOptions.setSnapshot] will affect
     * what is read from the DB but will NOT change which keys are read from this
     * transaction (the keys in this transaction do not yet belong to any snapshot
     * and will be fetched regardless).
     *
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle] instance
     * @param readOptions Read options.
     * @param key the key to retrieve the value for.
     *
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying native
     * library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions, key: ByteArray
    ): ByteArray?

    /**
     * This function is similar to
     * [RocksDB.get] except it will
     * also read pending changes in this transaction.
     * Currently, this function will return Status::MergeInProgress if the most
     * recent write to the queried key in this batch is a Merge.
     *
     * If [ReadOptions.snapshot] is not set, the current version of the
     * key will be read. Calling [.setSnapshot] does not affect the
     * version of the data returned.
     *
     * Note that setting [ReadOptions.setSnapshot] will affect
     * what is read from the DB but will NOT change which keys are read from this
     * transaction (the keys in this transaction do not yet belong to any snapshot
     * and will be fetched regardless).
     *
     * @param readOptions Read options.
     * @param key the key to retrieve the value for.
     *
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying native
     * library.
     */
    fun get(readOptions: ReadOptions, key: ByteArray): ByteArray?

    /**
     * This function is similar to
     * [RocksDB.multiGet] except it will
     * also read pending changes in this transaction.
     * Currently, this function will return Status::MergeInProgress if the most
     * recent write to the queried key in this batch is a Merge.
     *
     * If [ReadOptions.snapshot] is not set, the current version of the
     * key will be read. Calling [.setSnapshot] does not affect the
     * version of the data returned.
     *
     * Note that setting [ReadOptions.setSnapshot] will affect
     * what is read from the DB but will NOT change which keys are read from this
     * transaction (the keys in this transaction do not yet belong to any snapshot
     * and will be fetched regardless).
     *
     * @param readOptions Read options.
     * @param columnFamilyHandles [java.util.List] containing
     * [org.rocksdb.ColumnFamilyHandle] instances.
     * @param keys of keys for which values need to be retrieved.
     *
     * @return Array of values, one for each key
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IllegalArgumentException thrown if the size of passed keys is not
     * equal to the amount of passed column family handles.
     */
    fun multiGet(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: Array<ByteArray>
    ): Array<ByteArray?>

    /**
     * This function is similar to
     * [RocksDB.multiGet] except it will
     * also read pending changes in this transaction.
     * Currently, this function will return Status::MergeInProgress if the most
     * recent write to the queried key in this batch is a Merge.
     *
     * If [ReadOptions.snapshot] is not set, the current version of the
     * key will be read. Calling [.setSnapshot] does not affect the
     * version of the data returned.
     *
     * Note that setting [ReadOptions.setSnapshot] will affect
     * what is read from the DB but will NOT change which keys are read from this
     * transaction (the keys in this transaction do not yet belong to any snapshot
     * and will be fetched regardless).
     *
     * @param readOptions Read options.= [ColumnFamilyHandle] instances.
     * @param keys of keys for which values need to be retrieved.
     *
     * @return Array of values, one for each key
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun multiGet(
        readOptions: ReadOptions,
        keys: Array<ByteArray>
    ): Array<ByteArray?>

    /**
     * Read this key and ensure that this transaction will only
     * be able to be committed if this key is not written outside this
     * transaction after it has first been read (or after the snapshot if a
     * snapshot is set in this transaction). The transaction behavior is the
     * same regardless of whether the key exists or not.
     *
     * Note: Currently, this function will return Status::MergeInProgress
     * if the most recent write to the queried key in this batch is a Merge.
     *
     * The values returned by this function are similar to
     * [RocksDB.get].
     * If value==nullptr, then this function will not read any data, but will
     * still ensure that this key cannot be written to by outside of this
     * transaction.
     *
     * If this transaction was created by an [OptimisticTransactionDB],
     * [.getForUpdate]
     * could cause [.commit] to fail. Otherwise, it could return any error
     * that could be returned by
     * [RocksDB.get].
     *
     * If this transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     * [Status.Code.MergeInProgress] if merge operations cannot be
     * resolved.
     *
     * @param readOptions Read options.
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle]
     * instance
     * @param key the key to retrieve the value for.
     * @param exclusive true if the transaction should have exclusive access to
     * the key, otherwise false for shared access.
     * @param do_validate true if it should validate the snapshot before doing the read
     *
     * @return a byte array storing the value associated with the input key if
     * any.  null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, exclusive: Boolean,
        do_validate: Boolean
    ): ByteArray?

    /**
     * Same as
     * [.getForUpdate]
     * with do_validate=true.
     *
     * @param readOptions Read options.
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle]
     * instance
     * @param key the key to retrieve the value for.
     * @param exclusive true if the transaction should have exclusive access to
     * the key, otherwise false for shared access.
     *
     * @return a byte array storing the value associated with the input key if
     * any.  null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        exclusive: Boolean
    ): ByteArray?

    /**
     * Read this key and ensure that this transaction will only
     * be able to be committed if this key is not written outside this
     * transaction after it has first been read (or after the snapshot if a
     * snapshot is set in this transaction). The transaction behavior is the
     * same regardless of whether the key exists or not.
     *
     * Note: Currently, this function will return Status::MergeInProgress
     * if the most recent write to the queried key in this batch is a Merge.
     *
     * The values returned by this function are similar to
     * [RocksDB.get].
     * If value==nullptr, then this function will not read any data, but will
     * still ensure that this key cannot be written to by outside of this
     * transaction.
     *
     * If this transaction was created on an [OptimisticTransactionDB],
     * [.getForUpdate]
     * could cause [.commit] to fail. Otherwise, it could return any error
     * that could be returned by
     * [RocksDB.get].
     *
     * If this transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     * [Status.Code.MergeInProgress] if merge operations cannot be
     * resolved.
     *
     * @param readOptions Read options.
     * @param key the key to retrieve the value for.
     * @param exclusive true if the transaction should have exclusive access to
     * the key, otherwise false for shared access.
     *
     * @return a byte array storing the value associated with the input key if
     * any.  null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun getForUpdate(
        readOptions: ReadOptions, key: ByteArray,
        exclusive: Boolean
    ): ByteArray?

    /**
     * A multi-key version of
     * [.getForUpdate].
     *
     * @param readOptions Read options.
     * @param columnFamilyHandles [org.rocksdb.ColumnFamilyHandle]
     * instances
     * @param keys the keys to retrieve the values for.
     *
     * @return Array of values, one for each key
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun multiGetForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: Array<ByteArray>
    ): Array<ByteArray?>

    /**
     * A multi-key version of [.getForUpdate].
     *
     * @param readOptions Read options.
     * @param keys the keys to retrieve the values for.
     *
     * @return Array of values, one for each key
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun multiGetForUpdate(
        readOptions: ReadOptions,
        keys: Array<ByteArray>
    ): Array<ByteArray?>

    /**
     * Returns an iterator that will iterate on all keys in the default
     * column family including both keys in the DB and uncommitted keys in this
     * transaction.
     *
     * Setting [ReadOptions.setSnapshot] will affect what is read
     * from the DB but will NOT change which keys are read from this transaction
     * (the keys in this transaction do not yet belong to any snapshot and will be
     * fetched regardless).
     *
     * Caller is responsible for deleting the returned Iterator.
     *
     * The returned iterator is only valid until [.commit],
     * [.rollback], or [.rollbackToSavePoint] is called.
     *
     * @param readOptions Read options.
     *
     * @return instance of iterator object.
     */
    fun getIterator(readOptions: ReadOptions): RocksIterator

    /**
     * Returns an iterator that will iterate on all keys in the default
     * column family including both keys in the DB and uncommitted keys in this
     * transaction.
     *
     * Setting [ReadOptions.setSnapshot] will affect what is read
     * from the DB but will NOT change which keys are read from this transaction
     * (the keys in this transaction do not yet belong to any snapshot and will be
     * fetched regardless).
     *
     * Caller is responsible for calling [RocksIterator.close] on
     * the returned Iterator.
     *
     * The returned iterator is only valid until [.commit],
     * [.rollback], or [.rollbackToSavePoint] is called.
     *
     * @param readOptions Read options.
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle]
     * instance
     *
     * @return instance of iterator object.
     */
    fun getIterator(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle
    ): RocksIterator

    /**
     * Similar to [RocksDB.put], but
     * will also perform conflict checking on the keys be written.
     *
     * If this Transaction was created on an [OptimisticTransactionDB],
     * these functions should always succeed.
     *
     * If this Transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     *
     * @param columnFamilyHandle The column family to put the key/value into
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray,
        assume_tracked: Boolean
    )

    /**
     * Same as
     * {@link #put(ColumnFamilyHandle, byte[], byte[], boolean)}
     * with assume_tracked=false.
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        value: ByteArray
    )

    /**
     * Similar to [RocksDB.put], but
     * will also perform conflict checking on the keys be written.
     *
     * If this Transaction was created on an [OptimisticTransactionDB],
     * these functions should always succeed.
     *
     * If this Transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     *
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun put(key: ByteArray, value: ByteArray)

    /**
     * Similar to [.put] but allows
     * you to specify the key and value in several parts that will be
     * concatenated together.
     *
     * @param columnFamilyHandle The column family to put the key/value into
     * @param keyParts the specified key to be inserted.
     * @param valueParts the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle, keyParts: Array<ByteArray>,
        valueParts: Array<ByteArray>, assume_tracked: Boolean
    )

    /**
     * Same as
     * {@link #put(ColumnFamilyHandle, byte[][], byte[][], boolean)}
     * with assume_tracked=false.
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>, valueParts: Array<ByteArray>
    )

    /**
     * Similar to [.put] but allows
     * you to specify the key and value in several parts that will be
     * concatenated together
     *
     * @param keyParts the specified key to be inserted.
     * @param valueParts the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun put(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>)

    /**
     * Similar to [RocksDB.merge], but
     * will also perform conflict checking on the keys be written.
     *
     * If this Transaction was created on an [OptimisticTransactionDB],
     * these functions should always succeed.
     *
     * If this Transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     *
     * @param columnFamilyHandle The column family to merge the key/value into
     * @param key the specified key to be merged.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun merge(
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        value: ByteArray, assume_tracked: Boolean
    )

    /**
     * Same as
     * {@link #merge(ColumnFamilyHandle, byte[], byte[], boolean)}
     * with assume_tracked=false.
     */
    fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    /**
     * Similar to [RocksDB.merge], but
     * will also perform conflict checking on the keys be written.
     *
     * If this Transaction was created on an [OptimisticTransactionDB],
     * these functions should always succeed.
     *
     * If this Transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     *
     * @param key the specified key to be merged.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun merge(key: ByteArray, value: ByteArray)

    /**
     * Similar to [RocksDB.delete], but
     * will also perform conflict checking on the keys be written.
     *
     * If this Transaction was created on an [OptimisticTransactionDB],
     * these functions should always succeed.
     *
     * If this Transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     *
     * @param columnFamilyHandle The column family to delete the key/value from
     * @param key the specified key to be deleted.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        assume_tracked: Boolean
    )

    /**
     * Same as
     * {@link #delete(ColumnFamilyHandle, byte[], boolean)}
     * with assume_tracked=false.
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray
    )

    /**
     * Similar to [RocksDB.delete], but
     * will also perform conflict checking on the keys be written.
     *
     * If this Transaction was created on an [OptimisticTransactionDB],
     * these functions should always succeed.
     *
     * If this Transaction was created on a [TransactionDB], an
     * [RocksDBException] may be thrown with an accompanying [Status]
     * when:
     * [Status.Code.Busy] if there is a write conflict,
     * [Status.Code.TimedOut] if a lock could not be acquired,
     * [Status.Code.TryAgain] if the memtable history size is not large
     * enough. See
     * [ColumnFamilyOptions.maxWriteBufferNumberToMaintain]
     *
     * @param key the specified key to be deleted.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun delete(key: ByteArray)

    /**
     * Similar to [.delete] but allows
     * you to specify the key in several parts that will be
     * concatenated together.
     *
     * @param columnFamilyHandle The column family to delete the key/value from
     * @param keyParts the specified key to be deleted.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle, keyParts: Array<ByteArray>,
        assume_tracked: Boolean
    )

    /**
     * Same as
     * {@link #delete(ColumnFamilyHandle, byte[][], boolean)}
     * with assume_tracked=false.
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>
    )

    /**
     * Similar to [.delete] but allows
     * you to specify key the in several parts that will be
     * concatenated together.
     *
     * @param keyParts the specified key to be deleted
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun delete(keyParts: Array<ByteArray>)

    /**
     * Similar to [RocksDB.put],
     * but operates on the transactions write batch. This write will only happen
     * if this transaction gets committed successfully.
     *
     * Unlike [.put] no conflict
     * checking will be performed for this key.
     *
     * If this Transaction was created on a [TransactionDB], this function
     * will still acquire locks necessary to make sure this write doesn't cause
     * conflicts in other transactions; This may cause a [RocksDBException]
     * with associated [Status.Code.Busy].
     *
     * @param columnFamilyHandle The column family to put the key/value into
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun putUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    /**
     * Similar to [RocksDB.put],
     * but operates on the transactions write batch. This write will only happen
     * if this transaction gets committed successfully.
     *
     * Unlike [.put] no conflict
     * checking will be performed for this key.
     *
     * If this Transaction was created on a [TransactionDB], this function
     * will still acquire locks necessary to make sure this write doesn't cause
     * conflicts in other transactions; This may cause a [RocksDBException]
     * with associated [Status.Code.Busy].
     *
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun putUntracked(key: ByteArray, value: ByteArray)

    /**
     * Similar to [.putUntracked] but
     * allows you to specify the key and value in several parts that will be
     * concatenated together.
     *
     * @param columnFamilyHandle The column family to put the key/value into
     * @param keyParts the specified key to be inserted.
     * @param valueParts the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun putUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>, valueParts: Array<ByteArray>
    )

    /**
     * Similar to [.putUntracked] but
     * allows you to specify the key and value in several parts that will be
     * concatenated together.
     *
     * @param keyParts the specified key to be inserted.
     * @param valueParts the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun putUntracked(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>)

    /**
     * Similar to [RocksDB.merge],
     * but operates on the transactions write batch. This write will only happen
     * if this transaction gets committed successfully.
     *
     * Unlike [.merge] no conflict
     * checking will be performed for this key.
     *
     * If this Transaction was created on a [TransactionDB], this function
     * will still acquire locks necessary to make sure this write doesn't cause
     * conflicts in other transactions; This may cause a [RocksDBException]
     * with associated [Status.Code.Busy].
     *
     * @param columnFamilyHandle The column family to merge the key/value into
     * @param key the specified key to be merged.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun mergeUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    /**
     * Similar to [RocksDB.merge],
     * but operates on the transactions write batch. This write will only happen
     * if this transaction gets committed successfully.
     *
     * Unlike [.merge] no conflict
     * checking will be performed for this key.
     *
     * If this Transaction was created on a [TransactionDB], this function
     * will still acquire locks necessary to make sure this write doesn't cause
     * conflicts in other transactions; This may cause a [RocksDBException]
     * with associated [Status.Code.Busy].
     *
     * @param key the specified key to be merged.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun mergeUntracked(key: ByteArray, value: ByteArray)

    /**
     * Similar to [RocksDB.delete],
     * but operates on the transactions write batch. This write will only happen
     * if this transaction gets committed successfully.
     *
     * Unlike [.delete] no conflict
     * checking will be performed for this key.
     *
     * If this Transaction was created on a [TransactionDB], this function
     * will still acquire locks necessary to make sure this write doesn't cause
     * conflicts in other transactions; This may cause a [RocksDBException]
     * with associated [Status.Code.Busy].
     *
     * @param columnFamilyHandle The column family to delete the key/value from
     * @param key the specified key to be deleted.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun deleteUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray
    )

    /**
     * Similar to [RocksDB.delete],
     * but operates on the transactions write batch. This write will only happen
     * if this transaction gets committed successfully.
     *
     * Unlike [.delete] no conflict
     * checking will be performed for this key.
     *
     * If this Transaction was created on a [TransactionDB], this function
     * will still acquire locks necessary to make sure this write doesn't cause
     * conflicts in other transactions; This may cause a [RocksDBException]
     * with associated [Status.Code.Busy].
     *
     * @param key the specified key to be deleted.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun deleteUntracked(key: ByteArray)

    /**
     * Similar to [.deleteUntracked] but allows
     * you to specify the key in several parts that will be
     * concatenated together.
     *
     * @param columnFamilyHandle The column family to delete the key/value from
     * @param keyParts the specified key to be deleted.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun deleteUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>
    )

    /**
     * Similar to [.deleteUntracked] but allows
     * you to specify the key in several parts that will be
     * concatenated together.
     *
     * @param keyParts the specified key to be deleted.
     *
     * @throws RocksDBException when one of the TransactionalDB conditions
     * described above occurs, or in the case of an unexpected error
     */
    fun deleteUntracked(keyParts: Array<ByteArray>)

    /**
     * Similar to [WriteBatch.putLogData]
     * @param blob binary object to be inserted
     */
    fun putLogData(blob: ByteArray)

    /**
     * By default, all put/merge/delete operations will be indexed in the
     * transaction so that get/getForUpdate/getIterator can search for these
     * keys.
     *
     * If the caller does not want to fetch the keys about to be written,
     * they may want to avoid indexing as a performance optimization.
     * Calling [.disableIndexing] will turn off indexing for all future
     * put/merge/delete operations until [.enableIndexing] is called.
     *
     * If a key is put/merge/deleted after [.disableIndexing] is called
     * and then is fetched via get/getForUpdate/getIterator, the result of the
     * fetch is undefined.
     */
    fun disableIndexing()

    /** Re-enables indexing after a previous call to [.disableIndexing] */
    fun enableIndexing()

    /**
     * Returns the number of distinct Keys being tracked by this transaction.
     * If this transaction was created by a [TransactionDB], this is the
     * number of keys that are currently locked by this transaction.
     * If this transaction was created by an [OptimisticTransactionDB],
     * this is the number of keys that need to be checked for conflicts at commit
     * time.
     *
     * @return the number of distinct Keys being tracked by this transaction
     */
    fun getNumKeys(): Long

    /**
     * Returns the number of puts that have been applied to this
     * transaction so far.
     *
     * @return the number of puts that have been applied to this transaction
     */
    fun getNumPuts(): Long

    /**
     * Returns the number of deletes that have been applied to this
     * transaction so far.
     *
     * @return the number of deletes that have been applied to this transaction
     */
    fun getNumDeletes(): Long

    /**
     * Returns the number of merges that have been applied to this
     * transaction so far.
     *
     * @return the number of merges that have been applied to this transaction
     */
    fun getNumMerges(): Long

    /**
     * Returns the elapsed time in milliseconds since this Transaction began.
     *
     * @return the elapsed time in milliseconds since this transaction began.
     */
    fun getElapsedTime(): Long

    /**
     * Fetch the underlying write batch that contains all pending changes to be
     * committed.
     *
     * Note: You should not write or delete anything from the batch directly and
     * should only use the functions in the [Transaction] class to
     * write to this transaction.
     *
     * @return The write batch
     */
    fun getWriteBatch(): WriteBatchWithIndex

    /**
     * Change the value of [TransactionOptions.getLockTimeout]
     * (in milliseconds) for this transaction.
     *
     * Has no effect on OptimisticTransactions.
     *
     * @param lockTimeout the timeout (in milliseconds) for locks used by this
     * transaction.
     */
    fun setLockTimeout(lockTimeout: Long)

    /**
     * Return the WriteOptions that will be used during [.commit].
     *
     * @return the WriteOptions that will be used
     */
    fun getWriteOptions(): WriteOptions

    /**
     * Reset the WriteOptions that will be used during [.commit].
     *
     * @param writeOptions The new WriteOptions
     */
    fun setWriteOptions(writeOptions: WriteOptions)

    /**
     * If this key was previously fetched in this transaction using
     * [.getForUpdate]/
     * [.multiGetForUpdate], calling
     * [.undoGetForUpdate] will tell
     * the transaction that it no longer needs to do any conflict checking
     * for this key.
     *
     * If a key has been fetched N times via
     * [.getForUpdate]/
     * [.multiGetForUpdate], then
     * [.undoGetForUpdate]  will only have an
     * effect if it is also called N times. If this key has been written to in
     * this transaction, [.undoGetForUpdate]
     * will have no effect.
     *
     * If [.setSavePoint] has been called after the
     * [.getForUpdate],
     * [.undoGetForUpdate] will not have any
     * effect.
     *
     * If this Transaction was created by an [OptimisticTransactionDB],
     * calling [.undoGetForUpdate] can affect
     * whether this key is conflict checked at commit time.
     * If this Transaction was created by a [TransactionDB],
     * calling [.undoGetForUpdate] may release
     * any held locks for this key.
     *
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle]
     * instance
     * @param key the key to retrieve the value for.
     */
    fun undoGetForUpdate(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray
    )

    /**
     * If this key was previously fetched in this transaction using
     * [.getForUpdate]/
     * [.multiGetForUpdate], calling
     * [.undoGetForUpdate] will tell
     * the transaction that it no longer needs to do any conflict checking
     * for this key.
     *
     * If a key has been fetched N times via
     * [.getForUpdate]/
     * [.multiGetForUpdate], then
     * [.undoGetForUpdate]  will only have an
     * effect if it is also called N times. If this key has been written to in
     * this transaction, [.undoGetForUpdate]
     * will have no effect.
     *
     * If [.setSavePoint] has been called after the
     * [.getForUpdate],
     * [.undoGetForUpdate] will not have any
     * effect.
     *
     * If this Transaction was created by an [OptimisticTransactionDB],
     * calling [.undoGetForUpdate] can affect
     * whether this key is conflict checked at commit time.
     * If this Transaction was created by a [TransactionDB],
     * calling [.undoGetForUpdate] may release
     * any held locks for this key.
     *
     * @param key the key to retrieve the value for.
     */
    fun undoGetForUpdate(key: ByteArray)

    /**
     * Adds the keys from the WriteBatch to the transaction
     *
     * @param writeBatch The write batch to read from
     *
     * @throws RocksDBException if an error occurs whilst rebuilding from the
     * write batch.
     */
    fun rebuildFromWriteBatch(writeBatch: WriteBatch)

    /**
     * Get the Commit time Write Batch.
     *
     * @return the commit time write batch.
     */
    fun getCommitTimeWriteBatch(): WriteBatch

    /**
     * Set the log number.
     *
     * @param logNumber the log number
     */
    fun setLogNumber(logNumber: Long)

    /**
     * Get the log number.
     *
     * @return the log number
     */
    fun getLogNumber(): Long

    /**
     * Set the name of the transaction.
     *
     * @param transactionName the name of the transaction
     *
     * @throws RocksDBException if an error occurs when setting the transaction
     * name.
     */
    fun setName(transactionName: String)

    /**
     * Get the name of the transaction.
     *
     * @return the name of the transaction
     */
    fun getName(): String

    /**
     * Get the ID of the transaction.
     *
     * @return the ID of the transaction.
     */
    fun getID(): Long

    /**
     * Determine if a deadlock has been detected.
     *
     * @return true if a deadlock has been detected.
     */
    fun isDeadlockDetect(): Boolean

    /** Get the list of waiting transactions. */
    fun getWaitingTxns(): WaitingTransactions

    /**
     * Get the execution status of the transaction.
     *
     * NOTE: The execution status of an Optimistic Transaction
     * never changes. This is only useful for non-optimistic transactions!
     *
     * @return The execution status of the transaction
     */
    fun getState(): TransactionState

    /**
     * The globally unique id with which the transaction is identified. This id
     * might or might not be set depending on the implementation. Similarly the
     * implementation decides the point in lifetime of a transaction at which it
     * assigns the id. Although currently it is the case, the id is not guaranteed
     * to remain the same across restarts.
     *
     * @return the transaction id.
     */
    fun getId(): Long
}
