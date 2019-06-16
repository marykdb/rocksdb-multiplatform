package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test

class DirectComparatorTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("DirectComparatorTest")

    @Test
    fun directComparator() {
        val comparatorTest = object : AbstractComparatorTest() {
            override val ascendingIntKeyComparator = object : DirectComparator(ComparatorOptions()) {
                override fun name() = "test.AscendingIntKeyDirectComparator"

                override fun compare(a: DirectSlice, b: DirectSlice): Int {
                    val ax = ByteArray(4)
                    val bx = ByteArray(4)
                    a.data()[ax]
                    b.data()[bx]
                    return compareIntKeys(ax, bx)
                }
            }
        }

        // test the round-tripability of keys written and read with the DirectComparator
        comparatorTest.testRoundtrip(
            createTestFolder()
        )
    }
}
