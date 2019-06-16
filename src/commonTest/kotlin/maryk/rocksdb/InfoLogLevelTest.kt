package maryk.rocksdb

import maryk.decodeToString
import maryk.encodeToByteArray
import maryk.readAllBytesFromFile
import maryk.rocksdb.InfoLogLevel.FATAL_LEVEL
import maryk.rocksdb.util.createTestDBFolder
import maryk.systemLineSeparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InfoLogLevelTest {
    private fun createTestFolder() = createTestDBFolder("InfoLogLevelTest")

    @Test
    fun testInfoLogLevel() {
        val testFolder = createTestFolder()
        openRocksDB(testFolder).use { db ->
            db.put("key".encodeToByteArray(), "value".encodeToByteArray())
            db.flush(FlushOptions().setWaitForFlush(true))
            assertTrue(getLogContentsWithoutHeader(testFolder).isNotEmpty())
        }
    }

    @Test
    fun testFatalLogLevel() {
        Options().apply {
            setCreateIfMissing(true)
            setInfoLogLevel(FATAL_LEVEL)
        }.use { options ->
            val testFolder = createTestFolder()
            openRocksDB(options, testFolder).use { db ->
                assertEquals(FATAL_LEVEL, options.infoLogLevel())
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                // As InfoLogLevel is set to FATAL_LEVEL, here we expect the log
                // content to be empty.
                assertTrue(getLogContentsWithoutHeader(testFolder).isEmpty())
            }
        }
    }

    @Test
    fun testFatalLogLevelWithDBOptions() {
        DBOptions().setInfoLogLevel(FATAL_LEVEL).use { dbOptions ->
            Options(
                dbOptions,
                ColumnFamilyOptions()
            ).setCreateIfMissing(true).use { options ->
                val testFolder = createTestFolder()
                openRocksDB(options, testFolder).use { db ->
                    assertEquals(FATAL_LEVEL, dbOptions.infoLogLevel())
                    assertEquals(FATAL_LEVEL, options.infoLogLevel())
                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    assertTrue(getLogContentsWithoutHeader(testFolder).isEmpty())
                }
            }
        }
    }

    @Test
    fun failIfIllegalByteValueProvided() {
        assertFailsWith<IllegalArgumentException> {
            getInfoLogLevel((-1).toByte())
        }
    }

    @Test
    fun valueOf() {
        assertEquals(InfoLogLevel.DEBUG_LEVEL, InfoLogLevel.valueOf("DEBUG_LEVEL"))
    }

    /**
     * Read LOG file contents into String.
     *
     * @return LOG file contents as String.
     * @throws IOException if file is not found.
     */
    private fun getLogContentsWithoutHeader(folder: String): String {
        val separator = systemLineSeparator
        val lines = readAllBytesFromFile("$folder/LOG")
            .decodeToString()
            .split(separator.toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        var firstNonHeader = lines.size
        // Identify the last line of the header
        for (i in lines.indices.reversed()) {
            if (lines[i].indexOf("DB pointer") >= 0) {
                firstNonHeader = i + 1
                break
            }
        }
        val builder = StringBuilder()
        for (i in firstNonHeader until lines.size) {
            builder.append(lines[i]).append(separator)
        }
        return builder.toString()
    }
}
