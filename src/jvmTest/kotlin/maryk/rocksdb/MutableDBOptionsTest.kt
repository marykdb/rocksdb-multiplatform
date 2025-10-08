package maryk.rocksdb

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MutableDBOptionsTest {
    private lateinit var dbDir: File

    @BeforeTest
    fun setup() {
        loadRocksDBLibrary()
        dbDir = createTempDirectory(prefix = "rocksdb-mutable-db").toFile()
    }

    @AfterTest
    fun tearDown() {
        if (::dbDir.isInitialized) {
            dbDir.deleteRecursively()
        }
    }

    @Test
    fun builderRoundTrip() {
        val builder = mutableDBOptionsBuilder()
            .setBytesPerSync(1024)
            .setMaxBackgroundJobs(4)
            .setAvoidFlushDuringShutdown(false)
            .setDailyOffpeakTimeUTC("22:00-04:00")

        assertEquals(1024, builder.bytesPerSync())
        assertEquals(4, builder.maxBackgroundJobs())
        assertFalse(builder.avoidFlushDuringShutdown())
        assertEquals("22:00-04:00", builder.dailyOffpeakTimeUTC())
    }

    @Test
    fun builderBuildsOptions() {
        val options = mutableDBOptionsBuilder()
            .setMaxOpenFiles(99)
            .setDelayedWriteRate(2048)
            .setStrictBytesPerSync(true)
            .build()

        val rendered = options.toString()
        assertTrue(rendered.contains("max_open_files=99"))
        assertTrue(rendered.contains("delayed_write_rate=2048"))
        assertTrue(rendered.contains("strict_bytes_per_sync=true"))
    }

    @Test
    fun parseOptionsString() {
        val optionsString = "max_open_files=55;delayed_write_rate=3000;" +
            "avoid_flush_during_shutdown=true;daily_offpeak_time_utc=01\\:00-05\\:30"

        val parsed = parseMutableDBOptions(optionsString)

        assertEquals(55, parsed.maxOpenFiles())
        assertEquals(3000, parsed.delayedWriteRate())
        assertTrue(parsed.avoidFlushDuringShutdown())
        assertEquals("01:00-05:30", parsed.dailyOffpeakTimeUTC())
    }

    @Test
    fun optionsExposeEnvironment() {
        MemEnv(getDefaultEnv()).use { memEnv ->
            DBOptions().use { dbOptions ->
                dbOptions.setEnv(memEnv)
                assertEquals(memEnv, dbOptions.getEnv())
            }

            Options().use { options ->
                options.setEnv(memEnv)
                assertEquals(memEnv, options.getEnv())
            }
        }
    }

    @Test
    fun optionsIntegrateWithRocksDB() {
        Options().setCreateIfMissing(true).use { options ->
            options.setDailyOffpeakTimeUTC("23:00-06:00")

            RocksDB.open(options, dbDir.absolutePath).use { db ->
                val value = "mutable-db".encodeToByteArray()
                val key = "key".encodeToByteArray()
                db.put(key, value)
                assertTrue(db.get(key)?.contentEquals(value) == true)
            }
        }
    }
}
