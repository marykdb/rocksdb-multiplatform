package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFailsWith

class OptionsNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun optionsBloomLocalityRejectsNegativeValues() {
        Options().use { options ->
            assertFailsWith<IllegalArgumentException> {
                options.setBloomLocality(-1)
            }
        }
    }

    @Test
    fun columnFamilyOptionsBloomLocalityRejectsNegativeValues() {
        ColumnFamilyOptions().use { options ->
            assertFailsWith<IllegalArgumentException> {
                options.setBloomLocality(-1)
            }
        }
    }
}
