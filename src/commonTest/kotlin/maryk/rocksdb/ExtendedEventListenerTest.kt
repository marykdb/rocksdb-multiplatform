package maryk.rocksdb

import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ExtendedEventListenerTest {
    init { loadRocksDBLibrary() }

    @Test
    fun receivesCopiedCreationAndHandleDeletionNotification() {
        val started = ThreadSafeCounter()
        val created = ThreadSafeCounter()
        val deletedHandles = ThreadSafeCounter()
        var creation: TableFileCreationInfo? = null
        val listener = object : EventListener() {
            override fun onTableFileCreationStartedEvent(info: TableFileCreationBriefInfo) {
                if (info.columnFamilyName() == "events") started.increment()
            }
            override fun onTableFileCreatedEvent(info: TableFileCreationInfo) {
                if (info.columnFamilyName() == "events") {
                    creation = info
                    created.increment()
                }
            }
            override fun onColumnFamilyHandleDeletionStartedEvent() { deletedHandles.increment() }
        }
        try {
            Options().setCreateIfMissing(true).addEventListener(listener).use { options ->
                openRocksDB(options, createTestDBFolder("ExtendedEventListenerTest_creation")).use { db ->
                    ColumnFamilyOptions().use { cfOptions ->
                        db.createColumnFamily(ColumnFamilyDescriptor("events".encodeToByteArray(), cfOptions)).use { cf ->
                            db.put(cf, "key".encodeToByteArray(), "value".encodeToByteArray())
                            FlushOptions().setWaitForFlush(true).setAllowWriteStall(true).use { flush -> db.flush(flush, cf) }
                            assertEquals(1, started.value())
                            assertEquals(1, created.value())
                        }
                    }
                }
            }
            val info = assertNotNull(creation)
            assertEquals(TableFileCreationReason.FLUSH, info.reason())
            assertTrue(info.dbName().isNotEmpty())
            assertTrue(info.filePath().endsWith(".sst"))
            assertTrue(info.fileSize() > 0)
            assertEquals(StatusCode.Ok, info.status().getCode())
            assertEquals(1, info.tableProperties().numEntries())
            assertTrue(deletedHandles.value() >= 1)
        } finally { listener.close() }
    }

    @Test
    fun fileCallbacksAreOptInAndRecoveryCanBeVetoed() {
        val listener = object : EventListener() {
            override fun onErrorRecoveryBeginEvent(reason: BackgroundErrorReason, backgroundError: Status): Boolean = false
        }
        listener.use {
            assertFalse(it.shouldBeNotifiedOnFileIO())
            assertFalse(it.onErrorRecoveryBeginEvent(BackgroundErrorReason.FLUSH, Status(StatusCode.IOError, StatusSubCode.None, "failure")))
        }
        object : EventListener() {
            override fun shouldBeNotifiedOnFileIO(): Boolean = true
        }.use { assertTrue(it.shouldBeNotifiedOnFileIO()) }
    }

    @Test
    fun nonRangeFileEventsHaveDefinedZeroRanges() {
        val observed = ThreadSafeCounter()
        val invalid = ThreadSafeCounter()
        val listener = object : EventListener() {
            override fun shouldBeNotifiedOnFileIO(): Boolean = true
            private fun record(info: FileOperationInfo) {
                observed.increment()
                if (info.offset() != 0L || info.length() != 0L) invalid.increment()
            }
            override fun onFileFlushFinishEvent(info: FileOperationInfo) = record(info)
            override fun onFileSyncFinishEvent(info: FileOperationInfo) = record(info)
            override fun onFileTruncateFinishEvent(info: FileOperationInfo) = record(info)
            override fun onFileCloseFinishEvent(info: FileOperationInfo) = record(info)
        }
        try {
            Options().setCreateIfMissing(true).addEventListener(listener).use { options ->
                openRocksDB(options, createTestDBFolder("ExtendedEventListenerTest_fileIO")).use { db ->
                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                }
            }
            assertTrue(observed.value() > 0)
            assertEquals(0, invalid.value())
        } finally { listener.close() }
    }
}
