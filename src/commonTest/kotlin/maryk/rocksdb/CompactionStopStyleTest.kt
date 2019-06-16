package maryk.rocksdb

import maryk.rocksdb.CompactionStopStyle.CompactionStopStyleSimilarSize
import maryk.rocksdb.CompactionStopStyle.CompactionStopStyleTotalSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CompactionStopStyleTest {
    @Test
    fun failIfIllegalByteValueProvided() {
        assertFailsWith<IllegalArgumentException> {
            getCompactionStopStyle((-1).toByte())
        }
    }

    @Test
    fun getCompactionStopStyle() {
        assertEquals(
            CompactionStopStyleTotalSize,
            getCompactionStopStyle(
                CompactionStopStyleTotalSize.getValue()
            )
        )
    }

    @Test
    fun valueOf() {
        assertEquals(CompactionStopStyleSimilarSize, CompactionStopStyle.valueOf("CompactionStopStyleSimilarSize"))
    }
}
