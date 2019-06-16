package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class EnvOptionsTest {
    @Test
    fun dbOptionsConstructor() {
        val compactionReadaheadSize = (4 * 1024 * 1024).toLong()
        DBOptions().apply {
            setCompactionReadaheadSize(compactionReadaheadSize)
        }.use { dbOptions ->
            EnvOptions(dbOptions).use { envOptions ->
                assertEquals(compactionReadaheadSize, envOptions.compactionReadaheadSize())
            }
        }
    }

    @Test
    fun useMmapReads() {
        EnvOptions().use { envOptions ->
            val boolValue = Random.nextBoolean()
            envOptions.setUseMmapReads(boolValue)
            assertEquals(boolValue, envOptions.useMmapReads())
        }
    }

    @Test
    fun useMmapWrites() {
        EnvOptions().use { envOptions ->
            val boolValue = Random.nextBoolean()
            envOptions.setUseMmapWrites(boolValue)
            assertEquals(boolValue, envOptions.useMmapWrites())
        }
    }

    @Test
    fun useDirectReads() {
        EnvOptions().use { envOptions ->
            val boolValue = Random.nextBoolean()
            envOptions.setUseDirectReads(boolValue)
            assertEquals(boolValue, envOptions.useDirectReads())
        }
    }

    @Test
    fun useDirectWrites() {
        EnvOptions().use { envOptions ->
            val boolValue = Random.nextBoolean()
            envOptions.setUseDirectWrites(boolValue)
            assertEquals(boolValue, envOptions.useDirectWrites())
        }
    }

    @Test
    fun allowFallocate() {
        EnvOptions().use { envOptions ->
            val boolValue = Random.nextBoolean()
            envOptions.setAllowFallocate(boolValue)
            assertEquals(boolValue, envOptions.allowFallocate())
        }
    }

    @Test
    fun setFdCloexecs() {
        EnvOptions().use { envOptions ->
            val boolValue = Random.nextBoolean()
            envOptions.setSetFdCloexec(boolValue)
            assertEquals(boolValue, envOptions.setFdCloexec())
        }
    }

    @Test
    fun bytesPerSync() {
        EnvOptions().use { envOptions ->
            val longValue = Random.nextLong()
            envOptions.setBytesPerSync(longValue)
            assertEquals(longValue, envOptions.bytesPerSync())
        }
    }

    @Test
    fun fallocateWithKeepSize() {
        EnvOptions().use { envOptions ->
            val boolValue = Random.nextBoolean()
            envOptions.setFallocateWithKeepSize(boolValue)
            assertEquals(boolValue, envOptions.fallocateWithKeepSize())
        }
    }

    @Test
    fun compactionReadaheadSize() {
        EnvOptions().use { envOptions ->
            val intValue = Random.nextInt()
            envOptions.setCompactionReadaheadSize(intValue.toLong())
            assertEquals(intValue.toLong(), envOptions.compactionReadaheadSize())
        }
    }

    @Test
    fun randomAccessMaxBufferSize() {
        EnvOptions().use { envOptions ->
            val intValue = Random.nextInt()
            envOptions.setRandomAccessMaxBufferSize(intValue.toLong())
            assertEquals(intValue.toLong(), envOptions.randomAccessMaxBufferSize())
        }
    }

    @Test
    fun writableFileMaxBufferSize() {
        EnvOptions().use { envOptions ->
            val intValue = Random.nextInt()
            envOptions.setWritableFileMaxBufferSize(intValue.toLong())
            assertEquals(intValue.toLong(), envOptions.writableFileMaxBufferSize())
        }
    }

    @Test
    fun rateLimiter() {
        EnvOptions().use { envOptions ->
            RateLimiter(1000, (100 * 1000).toLong(), 1).use { rateLimiter1 ->
                envOptions.setRateLimiter(rateLimiter1)
                assertEquals(rateLimiter1, envOptions.rateLimiter())

                RateLimiter(1000).use { rateLimiter2 ->
                    envOptions.setRateLimiter(rateLimiter2)
                    assertEquals(rateLimiter2, envOptions.rateLimiter())
                }
            }
        }
    }
}
