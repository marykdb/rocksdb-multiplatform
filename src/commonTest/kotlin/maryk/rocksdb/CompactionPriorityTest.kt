package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.expect

class CompactionPriorityTest {
    @Test
    fun failIfIllegalByteValueProvided() {
        assertFailsWith<IllegalArgumentException> {
            getCompactionPriority((-1).toByte())
        }
    }

    @Test
    fun getCompactionPriority() {
        expect(CompactionPriority.OldestLargestSeqFirst) {
            getCompactionPriority(
                CompactionPriority.OldestLargestSeqFirst.getValue()
            )
        }
    }

    @Test
    fun valueOf() {
        expect(CompactionPriority.OldestSmallestSeqFirst) {
            CompactionPriority.valueOf("OldestSmallestSeqFirst")
        }
    }
}
