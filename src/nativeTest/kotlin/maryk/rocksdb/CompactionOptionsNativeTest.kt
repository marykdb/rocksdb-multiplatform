package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CompactionOptionsNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun outputFileSizeLimitRejectsNegativeValuesExceptUnlimitedSentinel() {
        CompactionOptions().use { options ->
            assertEquals(-1, options.outputFileSizeLimit())
            options.setOutputFileSizeLimit(-1)
            assertEquals(-1, options.outputFileSizeLimit())

            assertFailsWith<IllegalArgumentException> {
                options.setOutputFileSizeLimit(-2)
            }
        }
    }
}
