package maryk.rocksdb

import maryk.AtomicInteger
import maryk.createFolder
import maryk.encodeToByteArray
import maryk.rocksdb.InfoLogLevel.DEBUG_LEVEL
import maryk.rocksdb.InfoLogLevel.FATAL_LEVEL
import maryk.rocksdb.InfoLogLevel.WARN_LEVEL
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LoggerTest {
    private fun createTestFolder() = createTestDBFolder("LoggerTest").also {
        createFolder(it)
    }

    @Test
    fun customLogger() {
        val logMessageCounter = AtomicInteger()
        Options().setInfoLogLevel(DEBUG_LEVEL).setCreateIfMissing(true).use { options ->
            object : Logger(options) {
                // Create new logger with max log level passed by options
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    assertNotNull(logMsg)
                    assertTrue(logMsg.isNotEmpty())
                    logMessageCounter.incrementAndGet()
                }
            }.use { logger ->
                // Set custom logger to options
                options.setLogger(logger)

                openRocksDB(
                    options,
                    createTestFolder()
                ).use {
                    // there should be more than zero received log messages in
                    // debug level.
                    assertTrue(0 < logMessageCounter.get())
                }
            }
        }
    }

    @Test
    fun warnLogger() {
        val logMessageCounter = AtomicInteger()
        Options().setInfoLogLevel(WARN_LEVEL).setCreateIfMissing(true).use { options ->
            object : Logger(options) {
                // Create new logger with max log level passed by options
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    assertNotNull(logMsg)
                    assertTrue(logMsg.isNotEmpty())
                    logMessageCounter.incrementAndGet()
                }
            }.use { logger ->
                // Set custom logger to options
                options.setLogger(logger)

                openRocksDB(
                    options,
                    createTestFolder()
                ).use {
                    // there should be zero messages
                    // using warn level as log level.
                    assertEquals(0, logMessageCounter.get())
                }
            }
        }
    }


    @Test
    fun fatalLogger() {
        val logMessageCounter = AtomicInteger()
        Options().setInfoLogLevel(FATAL_LEVEL).setCreateIfMissing(true).use { options ->
            object : Logger(options) {
                // Create new logger with max log level passed by options
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    assertNotNull(logMsg)
                    assertTrue(logMsg.isNotEmpty())
                    logMessageCounter.incrementAndGet()
                }
            }.use { logger ->
                // Set custom logger to options
                options.setLogger(logger)

                openRocksDB(
                    options,
                    createTestFolder()
                ).use {
                    // there should be zero messages
                    // using fatal level as log level.
                    assertEquals(0, logMessageCounter.get())
                }
            }
        }
    }

    @Test
    fun dbOptionsLogger() {
        val logMessageCounter = AtomicInteger()
        DBOptions().setInfoLogLevel(FATAL_LEVEL).setCreateIfMissing(true).use { options ->
            object : Logger(options) {
                // Create new logger with max log level passed by options
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    assertNotNull(logMsg)
                    assertTrue(logMsg.isNotEmpty())
                    logMessageCounter.incrementAndGet()
                }
            }.use { logger ->
                // Set custom logger to options
                options.setLogger(logger)

                val cfDescriptors = listOf(
                    ColumnFamilyDescriptor(defaultColumnFamily)
                )
                val cfHandles = mutableListOf<ColumnFamilyHandle>()

                openRocksDB(
                    options,
                    createTestFolder(),
                    cfDescriptors, cfHandles
                ).use {
                    try {
                        // there should be zero messages
                        // using fatal level as log level.
                        assertEquals(0, logMessageCounter.get())
                    } finally {
                        for (columnFamilyHandle in cfHandles) {
                            columnFamilyHandle.close()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun setWarnLogLevel() {
        val logMessageCounter = AtomicInteger()
        Options().setInfoLogLevel(FATAL_LEVEL).setCreateIfMissing(true).use { options ->
            object : Logger(options) {
                // Create new logger with max log level passed by options
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    assertNotNull(logMsg)
                    assertTrue(logMsg.isNotEmpty())
                    logMessageCounter.incrementAndGet()
                }
            }.use { logger ->
                assertEquals(FATAL_LEVEL, logger.infoLogLevel())
                logger.setInfoLogLevel(WARN_LEVEL)
                assertEquals(WARN_LEVEL, logger.infoLogLevel())
            }
        }
    }

    @Test
    fun setInfoLogLevel() {
        val logMessageCounter = AtomicInteger()
        Options().setInfoLogLevel(FATAL_LEVEL).setCreateIfMissing(true).use { options ->
            object : Logger(options) {
                // Create new logger with max log level passed by options
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    assertNotNull(logMsg)
                    assertTrue(logMsg.isNotEmpty())
                    logMessageCounter.incrementAndGet()
                }
            }.use { logger ->
                assertEquals(FATAL_LEVEL, logger.infoLogLevel())
                logger.setInfoLogLevel(DEBUG_LEVEL)
                assertEquals(DEBUG_LEVEL, logger.infoLogLevel())
            }
        }
    }

    @Test
    fun changeLogLevelAtRuntime() {
        val logMessageCounter = AtomicInteger()
        Options().setInfoLogLevel(FATAL_LEVEL).setCreateIfMissing(true).use { options ->
            object : Logger(options) {
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    assertNotNull(logMsg)
                    assertTrue(logMsg.isNotEmpty())
                    logMessageCounter.incrementAndGet()
                }
            }.use { logger ->
                // Set custom logger to options
                options.setLogger(logger)

                openRocksDB(
                    options,
                    createTestFolder()
                ).use { db ->

                    // there should be zero messages
                    // using fatal level as log level.
                    assertEquals(0, logMessageCounter.get())

                    // change log level to debug level
                    logger.setInfoLogLevel(DEBUG_LEVEL)

                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    db.flush(FlushOptions().setWaitForFlush(true))

                    // messages shall be received due to previous actions.
                    assertNotEquals(0, logMessageCounter.get())
                }
            }
        }// Create new logger with max log level passed by options
    }
}
