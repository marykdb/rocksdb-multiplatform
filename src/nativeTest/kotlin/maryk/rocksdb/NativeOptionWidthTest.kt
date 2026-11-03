@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.sizeOf
import platform.posix.size_tVar
import rocksdb.rocksdb_mutable_cf_options_set_min_tombstones_for_range_conversion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NativeOptionWidthTest {
    @Test
    fun sizeTOptionBoundariesFollowTheTargetAbi() {
        mutableColumnFamilyOptionsBuilder().use { builder ->
            val fields = listOf(
                builder::setArenaBlockSize to builder::arenaBlockSize,
                builder::setMemtableHugePageSize to builder::memtableHugePageSize,
                builder::setMaxSuccessiveMerges to builder::maxSuccessiveMerges,
                builder::setInplaceUpdateNumLocks to builder::inplaceUpdateNumLocks,
            )
            val beyond32Bits = 1L shl 32
            for ((set, get) in fields) {
                set(UInt.MAX_VALUE.toLong())
                assertEquals(UInt.MAX_VALUE.toLong(), get())
                if (sizeOf<size_tVar>() == 4L) {
                    assertFailsWith<IllegalArgumentException> { set(beyond32Bits) }
                    assertEquals(UInt.MAX_VALUE.toLong(), get())
                } else {
                    set(beyond32Bits)
                    assertEquals(beyond32Bits, get())
                    set(Long.MAX_VALUE)
                    assertEquals(Long.MAX_VALUE, get())
                }
                val unchanged = get()
                assertFailsWith<IllegalArgumentException> { set(-1) }
                assertEquals(unchanged, get())
                set(0)
                assertEquals(0L, get())
            }
        }
    }

    @Test
    fun tombstoneThresholdPreservesUnsignedValuesWithoutSignedWrapping() {
        mutableColumnFamilyOptionsBuilder().use { builder ->
            builder.setMinTombstonesForRangeConversion(Int.MAX_VALUE)
            assertEquals(Int.MAX_VALUE, builder.minTombstonesForRangeConversion())
            assertFailsWith<IllegalArgumentException> { builder.setMinTombstonesForRangeConversion(-1) }
            assertEquals(Int.MAX_VALUE, builder.minTombstonesForRangeConversion())
            rocksdb_mutable_cf_options_set_min_tombstones_for_range_conversion(builder.native, UInt.MAX_VALUE)
            assertFailsWith<IllegalArgumentException> { builder.minTombstonesForRangeConversion() }
            builder.setMinTombstonesForRangeConversion(0)
            assertEquals(0, builder.minTombstonesForRangeConversion())
        }
    }
}
