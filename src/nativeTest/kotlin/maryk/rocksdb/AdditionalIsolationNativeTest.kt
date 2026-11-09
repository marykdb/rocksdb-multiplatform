package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import maryk.wrapWithErrorThrower
import maryk.wrapWithNullErrorThrower
import maryk.convertToStatus
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertSame

class AdditionalIsolationNativeTest {
    @Test
    fun equalBinarySlicesHaveEqualHashes() {
        Slice(byteArrayOf(97, 0, 98)).use { heap ->
            DirectSlice("a\u0000b").use { direct ->
                assertEquals<AbstractSlice<*>>(heap, direct)
                assertEquals(heap.hashCode(), direct.hashCode())
                assertTrue(hashSetOf<AbstractSlice<*>>(heap).contains(direct))
            }
        }
    }

    @Test
    fun ttlReadsRemoveTheStoredTimestamp() {
        val path = createTestDBFolder("AdditionalIsolationNativeTest_ttl")
        val key = "key".encodeToByteArray()
        val value = "value".encodeToByteArray()
        Options().setCreateIfMissing(true).use { options ->
            // The standard C API returns the DBWithTTL itself, rather than its base DB.
            val native = requireNotNull(Unit.wrapWithNullErrorThrower {
                rocksdb.rocksdb_open_with_ttl(options.native, path, 0, it)
            })
            RocksDB(native).use { db -> db.put(key, value) }
            openTtlDB(options, path).use { db ->
                assertContentEquals(value, db.get(key))
            }
        }
    }

    @Test
    fun ttlMultiIteratorsRemoveTheStoredTimestamp() {
        val path = createTestDBFolder("AdditionalIsolationNativeTest_ttl_multi")
        val key = "key".encodeToByteArray()
        val value = "value".encodeToByteArray()
        DBOptions().setCreateIfMissing(true).use { options ->
            val handles = mutableListOf<ColumnFamilyHandle>()
            ColumnFamilyOptions().use { cfOptions ->
                val descriptors = listOf(ColumnFamilyDescriptor(defaultColumnFamily, cfOptions))
                openTtlDB(options, path, descriptors, handles, listOf(0), false).use { db ->
                    try {
                        db.put(handles.single(), key, value)
                        val iterators = db.newIterators(handles)
                        try {
                            val iterator = iterators.single()
                            iterator.seekToFirst()
                            assertContentEquals(value, iterator.value())
                        } finally {
                            iterators.forEach { it.close() }
                        }
                    } finally {
                        handles.forEach { it.close() }
                    }
                }
            }
        }
    }

    @Test
    fun reopeningSstReaderInvalidatesExistingIterators() {
        val folder = createTestDBFolder("AdditionalIsolationNativeTest_reopen_sst")
        val paths = listOf("$folder-first.sst", "$folder-second.sst")
        Options().use { options ->
            EnvOptions().use { envOptions ->
                paths.forEachIndexed { index, path ->
                    SstFileWriter(envOptions, options).use { writer ->
                        writer.open(path)
                        writer.put("key".encodeToByteArray(), byteArrayOf(index.toByte()))
                        writer.finish()
                    }
                }
            }
            SstFileReader(options).use { reader ->
                ReadOptions().use { readOptions ->
                    reader.open(paths[0])
                    reader.newIterator(readOptions).use { iterator ->
                        reader.open(paths[1])
                        assertFalse(iterator.isOwningHandle())
                    }
                }
            }
        }
    }

    @Test
    fun indexedBatchReadsRejectAReleasedSnapshot() {
        val path = createTestDBFolder("AdditionalIsolationNativeTest_batch_snapshot")
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, path).use { db ->
                val snapshot = requireNotNull(db.getSnapshot())
                ReadOptions().setSnapshot(snapshot).use { readOptions ->
                    WriteBatchWithIndex().use { batch ->
                        val key = "key".encodeToByteArray()
                        batch.put(key, "value".encodeToByteArray())
                        snapshot.close()
                        // A batch hit avoids dereferencing the freed native snapshot in this probe.
                        assertFailsWith<IllegalStateException> {
                            batch.getFromBatchAndDB(db, readOptions, key)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun savePointRollbackInvalidatesReplacedSnapshotWrappers() {
        val path = createTestDBFolder("AdditionalIsolationNativeTest_rollback_snapshot")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txOptions ->
                openTransactionDB(options, txOptions, path).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { tx ->
                            tx.setSnapshot()
                            tx.setSavePoint()
                            tx.setSnapshot()
                            val replacedSnapshot = requireNotNull(tx.getSnapshot())
                            tx.rollbackToSavePoint()
                            // Check the Kotlin lifetime flag without dereferencing the freed C++ snapshot.
                            assertFalse(replacedSnapshot.isOwningHandle())
                            replacedSnapshot.close()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun notifierSnapshotsRemainReadableAfterTheCallbackReturns() = withTransaction { db, tx ->
        var captured: Snapshot? = null
        val notifier = object : AbstractTransactionNotifier() {
            override fun snapshotCreated(newSnapshot: Snapshot) { captured = newSnapshot }
        }
        notifier.use {
            tx.setSnapshotOnNextOperation(notifier)
            tx.put("key".encodeToByteArray(), "value".encodeToByteArray())
            val snapshot = requireNotNull(captured)
            assertEquals(requireNotNull(tx.getSnapshot()).use { it.getSequenceNumber() }, snapshot.getSequenceNumber())
            ReadOptions().setSnapshot(snapshot).use { readOptions ->
                assertEquals(null, db.get(readOptions, "key".encodeToByteArray()))
            }
        }
    }

    @Test
    fun savePointsRetainAndRestoreSnapshotNotifiers() = withTransaction { _, tx ->
        var firstCount = 0
        var secondCount = 0
        val first = object : AbstractTransactionNotifier() {
            override fun snapshotCreated(newSnapshot: Snapshot) { firstCount++ }
        }
        val second = object : AbstractTransactionNotifier() {
            override fun snapshotCreated(newSnapshot: Snapshot) { secondCount++ }
        }
        first.use {
            second.use {
                tx.setSnapshotOnNextOperation(first)
                tx.setSavePoint()
                tx.put("first".encodeToByteArray(), byteArrayOf(1))
                assertEquals(1, firstCount)
                tx.setSnapshotOnNextOperation(second)
                tx.rollbackToSavePoint()
                tx.put("restored".encodeToByteArray(), byteArrayOf(2))
                assertEquals(2, firstCount)
                assertEquals(0, secondCount)
            }
        }
    }

    private fun withTransaction(block: (TransactionDB, Transaction) -> Unit) {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txOptions ->
                openTransactionDB(options, txOptions, createTestDBFolder("AdditionalIsolationNativeTest_notifier")).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { tx -> block(db, tx) }
                    }
                }
            }
        }
    }

    @Test
    fun transactionWriteBatchCannotOutliveItsTransaction() = withTransaction { _, tx ->
        tx.getWriteBatch().use { batch ->
            tx.close()
            assertFailsWith<IllegalStateException> { batch.checkOpenHandle() }
        }
    }

    @Test
    fun underlyingWriteBatchCannotOutliveItsIndex() {
        val indexed = WriteBatchWithIndex()
        val batch = indexed.getWriteBatch()
        indexed.close()
        batch.use {
            assertFailsWith<IllegalStateException> { batch.checkOpenHandle() }
        }
    }

    @Test
    fun transactionCommitTimeBatchIsALiveView() = withTransaction { _, tx ->
        requireNotNull(tx.getCommitTimeWriteBatch()).use { first ->
            first.put("key".encodeToByteArray(), "value".encodeToByteArray())
            requireNotNull(tx.getCommitTimeWriteBatch()).use { second ->
                assertEquals(1, second.count())
            }
        }
    }

    @Test
    fun transactionWriteOptionsAreALiveView() = withTransaction { _, tx ->
        requireNotNull(tx.getWriteOptions()).use { first ->
            first.setSync(true)
            requireNotNull(tx.getWriteOptions()).use { second ->
                assertTrue(second.sync())
            }
        }
    }

    @Test
    fun transactionAliasCannotOutliveItsOwningWrapper() = withTransaction { db, tx ->
        tx.setName("alias")
        requireNotNull(db.getTransactionByName("alias")).use { alias ->
            tx.close()
            assertFailsWith<IllegalStateException> { alias.checkOwningHandle() }
        }
    }

    @Test
    fun transactionViewsAreInvalidatedOnClose() = withTransaction { _, tx ->
        val batch = requireNotNull(tx.getCommitTimeWriteBatch())
        val options = requireNotNull(tx.getWriteOptions())
        tx.close()
        batch.use { assertFailsWith<IllegalStateException> { batch.checkOpenHandle() } }
        options.use { assertFailsWith<IllegalStateException> { options.checkOpenHandle() } }
    }

    @Test
    fun transactionAliasesShareSnapshotsAndNotifierLifetimes() = withTransaction { db, tx ->
        tx.setName("alias-lifetime")
        tx.setSnapshot()
        val snapshot = requireNotNull(tx.getSnapshot())
        val alias = requireNotNull(db.getTransactionByName("alias-lifetime"))
        alias.clearSnapshot()
        assertFalse(snapshot.isOwningHandle())
        var count = 0
        val notifier = object : AbstractTransactionNotifier() {
            override fun snapshotCreated(newSnapshot: Snapshot) { count++ }
        }
        notifier.use {
            alias.setSnapshotOnNextOperation(notifier)
            tx.setSavePoint()
            alias.close()
            tx.put("first".encodeToByteArray(), byteArrayOf(1))
            assertEquals(1, count)
            tx.rollbackToSavePoint()
            tx.put("second".encodeToByteArray(), byteArrayOf(2))
            assertEquals(2, count)
        }
    }

    @Test
    fun transactionBatchIteratorsAreInvalidatedOnCommit() = withTransaction { _, tx ->
        tx.put("key".encodeToByteArray(), byteArrayOf(1))
        tx.getWriteBatch().use { batch ->
            batch.newIterator().use { iterator ->
                tx.commit()
                assertFalse(iterator.isOwningHandle())
            }
        }
    }

    @Test
    fun waitingTransactionReportsTheCompleteKey() = withTransaction { db, holder ->
        val key = "waiting-" + "x".repeat(300) + "\u0000tail"
        holder.put(key.encodeToByteArray(), byteArrayOf(1))
        WriteOptions().use { writeOptions ->
            TransactionOptions().setLockTimeout(0).use { options ->
                db.beginTransaction(writeOptions, options).use { waiter ->
                    assertFailsWith<RocksDBException> { waiter.put(key.encodeToByteArray(), byteArrayOf(2)) }
                    assertEquals(key, waiter.getWaitingTxns().getKey())
                }
            }
        }
    }

    @Test
    fun waitingTransactionReadRejectsAClosedTransaction() = withTransaction { _, tx ->
        tx.close()
        assertFailsWith<IllegalStateException> { tx.getWaitingTxns() }
    }

    @Test
    fun preparedCommitPersistsCommitTimeWrites() {
        Options().setCreateIfMissing(true).setAllow2pc(true).use { options ->
            TransactionDBOptions().use { txOptions ->
                openTransactionDB(options, txOptions, createTestDBFolder("AdditionalIsolationNativeTest_commit_time")).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { tx ->
                            tx.setName("commit-time")
                            tx.put("regular".encodeToByteArray(), byteArrayOf(1))
                            tx.prepare()
                            requireNotNull(tx.getCommitTimeWriteBatch()).use { batch ->
                                batch.put("commit".encodeToByteArray(), byteArrayOf(2))
                            }
                            tx.commit()
                            assertContentEquals(byteArrayOf(1), db.get("regular".encodeToByteArray()))
                            assertContentEquals(byteArrayOf(2), db.get("commit".encodeToByteArray()))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun openingADatabaseDoesNotConsumeItsComparatorOptions() {
        val path = createTestDBFolder("AdditionalIsolationNativeTest_reuse_comparator")
        Options().setCreateIfMissing(true).setComparator(BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR).use { options ->
            openRocksDB(options, path).use { db ->
                db.put("a".encodeToByteArray(), byteArrayOf(1))
                db.put("b".encodeToByteArray(), byteArrayOf(2))
            }
            openRocksDB(options, path).use { db ->
                db.newIterator().use { iterator ->
                    iterator.seekToFirst()
                    assertContentEquals("b".encodeToByteArray(), iterator.key())
                }
            }
        }
    }

    @Test
    fun sharedComparatorOutlivesOptionsAndTheFirstDatabase() {
        val options = Options().setCreateIfMissing(true).setComparator(BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR)
        val first = openRocksDB(options, createTestDBFolder("AdditionalIsolationNativeTest_comparator_first"))
        openRocksDB(options, createTestDBFolder("AdditionalIsolationNativeTest_comparator_second")).use { second ->
            options.close()
            first.close()
            second.put("a".encodeToByteArray(), byteArrayOf(1))
            second.put("b".encodeToByteArray(), byteArrayOf(2))
            second.newIterator().use { iterator ->
                iterator.seekToFirst()
                assertContentEquals("b".encodeToByteArray(), iterator.key())
            }
        }
    }

    @Test
    fun creatingAColumnFamilyDoesNotConsumeItsComparatorOptions() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("AdditionalIsolationNativeTest_comparator_cf")).use { db ->
                val cfOptions = ColumnFamilyOptions().setComparator(BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR)
                db.createColumnFamily(ColumnFamilyDescriptor("first".encodeToByteArray(), cfOptions)).use { first ->
                    db.createColumnFamily(ColumnFamilyDescriptor("second".encodeToByteArray(), cfOptions)).use { second ->
                        cfOptions.close()
                        db.put(second, "a".encodeToByteArray(), byteArrayOf(1))
                        db.put(second, "b".encodeToByteArray(), byteArrayOf(2))
                        db.newIterator(second).use { iterator ->
                            iterator.seekToFirst()
                            assertContentEquals("b".encodeToByteArray(), iterator.key())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun unknownStatusCodesDoNotMaskTheRocksDBError() {
        val status = requireNotNull(convertToStatus("[15|0] Column family dropped"))
        assertEquals(StatusCode.Undefined, status.getCode())
        assertEquals("Column family dropped", status.getState())
    }

    @Test
    fun newStatusSubcodesAreNotMisidentifiedAsTheOldSentinel() {
        val status = requireNotNull(convertToStatus("[9|16] Merge operand threshold exceeded"))
        assertEquals(StatusSubCode.Undefined, status.getSubCode())
    }

    @Test
    fun configuredDatabaseEnvironmentIsReturned() {
        getDefaultEnv().use { base ->
            MemEnv(base).use { env ->
                Options().setCreateIfMissing(true).setEnv(env).use { options ->
                    openRocksDB(options, "/" + createTestDBFolder("AdditionalIsolationNativeTest_env")).use { db ->
                        assertSame(env, db.getEnv())
                    }
                }
            }
        }
    }

    @Test
    fun defaultEnvironmentCloseIsANoOp() {
        val env = getDefaultEnv()
        env.close()
        env.getBackgroundThreads(Priority.LOW)
    }

    @Test
    fun equalDefaultEnvironmentsHaveEqualHashes() {
        getDefaultEnv().use { first ->
            getDefaultEnv().use { second ->
                assertEquals(first, second)
                assertEquals(first.hashCode(), second.hashCode())
            }
        }
    }

    @Test
    fun performanceLevelReflectsTheCurrentThreadAcrossDatabases() {
        val previous = rocksdb.rocksdb_get_perf_level()
        try {
            Options().setCreateIfMissing(true).use { options ->
                openRocksDB(options, createTestDBFolder("AdditionalIsolationNativeTest_perf_first")).use { first ->
                    openRocksDB(options, createTestDBFolder("AdditionalIsolationNativeTest_perf_second")).use { second ->
                        first.setPerfLevel(PerfLevel.ENABLE_COUNT)
                        assertEquals(PerfLevel.ENABLE_COUNT, second.getPerfLevel())
                    }
                }
            }
        } finally {
            rocksdb.rocksdb_set_perf_level(previous)
        }
    }

    @Test
    fun threadPoolQueueLengthUsesTheRequestedPriority() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("AdditionalIsolationNativeTest_queue_priority")).use { db ->
                val env = getDefaultEnv()
                val previous = env.getBackgroundThreads(Priority.HIGH)
                val previousLow = env.getBackgroundThreads(Priority.LOW)
                env.setBackgroundThreads(0, Priority.HIGH)
                env.setBackgroundThreads(0, Priority.LOW)
                try {
                    db.put("key".encodeToByteArray(), byteArrayOf(1))
                    FlushOptions().setWaitForFlush(false).use { db.flush(it) }
                    assertTrue(env.getThreadPoolQueueLen(Priority.LOW) > 0)
                    assertEquals(0, env.getThreadPoolQueueLen(Priority.BOTTOM))
                } finally {
                    env.setBackgroundThreads(previous, Priority.HIGH)
                    env.setBackgroundThreads(previousLow, Priority.LOW)
                }
            }
        }
    }

    @Test
    fun reusingATransactionRejectsADifferentDatabase() {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { txOptions ->
                openTransactionDB(options, txOptions, createTestDBFolder("AdditionalIsolationNativeTest_owner_first")).use { first ->
                    openTransactionDB(options, txOptions, createTestDBFolder("AdditionalIsolationNativeTest_owner_second")).use { second ->
                        WriteOptions().use { writeOptions ->
                            first.beginTransaction(writeOptions).use { tx ->
                                assertFailsWith<IllegalArgumentException> { second.beginTransaction(writeOptions, tx) }
                            }
                        }
                    }
                }
            }
        }
    }
}
