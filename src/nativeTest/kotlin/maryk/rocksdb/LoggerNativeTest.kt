package maryk.rocksdb

import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggerNativeTest {
    @Test
    fun existingNoArgumentNativeLoggerSubclassesRemainUsable() {
        object : Logger() {}.use { logger ->
            Options().setCreateIfMissing(true).use { options ->
                options.setLogger(logger)
                openRocksDB(options, createTestDBFolder("LoggerNativeTest_noArgumentSubclass")).use { db ->
                    db.put(byteArrayOf(1), byteArrayOf(2))
                }
            }
        }
    }

    @Test
    fun closedLoggerCallbacksCannotReachAnotherLogger() {
        val closedMessages = ThreadSafeCounter()
        val otherMessages = ThreadSafeCounter()
        val closedLogger = countingLogger(closedMessages)
        Options().setCreateIfMissing(true).use { options ->
            options.setLogger(closedLogger)
            closedLogger.close()
            // A new logger must not inherit callbacks from a freed callback state.
            countingLogger(otherMessages).use { other ->
                Options().setCreateIfMissing(true).use { otherOptions ->
                    otherOptions.setLogger(other)
                    openRocksDB(otherOptions, createTestDBFolder("LoggerNativeTest_otherLogger")).use { db ->
                        db.put(byteArrayOf(1), byteArrayOf(2))
                    }
                }
                val beforeOldCallbacks = otherMessages.value()
                assertTrue(beforeOldCallbacks > 0)
                openRocksDB(options, createTestDBFolder("LoggerNativeTest_closedLogger")).use { db ->
                    db.put(byteArrayOf(1), byteArrayOf(2))
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                }
                assertEquals(0, closedMessages.value())
                assertEquals(beforeOldCallbacks, otherMessages.value())
            }
            closedLogger.close()
        }
    }

    private fun countingLogger(messages: ThreadSafeCounter): Logger = object : Logger(InfoLogLevel.INFO_LEVEL) {
        override fun log(logLevel: InfoLogLevel, logMsg: String) {
            if (logMsg.isNotEmpty()) messages.increment()
        }
    }

    @Test
    fun failedPersistentCacheCreationDoesNotConsumeTheLogger() {
        val messages = ThreadSafeCounter()
        val logger = object : Logger(InfoLogLevel.INFO_LEVEL) {
            override fun log(logLevel: InfoLogLevel, logMsg: String) {
                if (logMsg.isNotEmpty()) messages.increment()
            }
        }
        logger.use {
            assertFailsWith<RocksDBException> {
                PersistentCache(
                    getDefaultEnv(), createTestDBFolder("LoggerNativeTest_invalidCache"),
                    0, logger, false,
                ).close()
            }
            assertTrue(logger.isOwningHandle(), "Failed cache creation must leave the shared logger usable")
            val beforeOpen = messages.value()
            Options().setCreateIfMissing(true).use { options ->
                options.setLogger(logger)
                openRocksDB(options, createTestDBFolder("LoggerNativeTest_reusedLogger")).use { db ->
                    db.put(byteArrayOf(1), byteArrayOf(2))
                }
            }
            assertTrue(messages.value() > beforeOpen)
        }
    }
}
