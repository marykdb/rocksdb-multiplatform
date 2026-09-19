package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class CompactRangeOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun exclusiveManualCompaction() {
        val opt = CompactRangeOptions()
        var value = false
        opt.setExclusiveManualCompaction(value)
        assertEquals(value, opt.exclusiveManualCompaction())
        value = true
        opt.setExclusiveManualCompaction(value)
        assertEquals(value, opt.exclusiveManualCompaction())
    }

    @Test
    fun bottommostLevelCompaction() {
        val opt = CompactRangeOptions()
        var value = BottommostLevelCompaction.kSkip
        opt.setBottommostLevelCompaction(value)
        assertEquals(value, opt.bottommostLevelCompaction())
        value = BottommostLevelCompaction.kForce
        opt.setBottommostLevelCompaction(value)
        assertEquals(value, opt.bottommostLevelCompaction())
        value = BottommostLevelCompaction.kIfHaveCompactionFilter
        opt.setBottommostLevelCompaction(value)
        assertEquals(value, opt.bottommostLevelCompaction())
    }

    @Test
    fun changeLevel() {
        val opt = CompactRangeOptions()
        var value = false
        opt.setChangeLevel(value)
        assertEquals(value, opt.changeLevel())
        value = true
        opt.setChangeLevel(value)
        assertEquals(value, opt.changeLevel())
    }

    @Test
    fun targetLevel() {
        val opt = CompactRangeOptions()
        var value = 2
        opt.setTargetLevel(value)
        assertEquals(value, opt.targetLevel())
        value = 3
        opt.setTargetLevel(value)
        assertEquals(value, opt.targetLevel())
    }

    @Test
    fun targetPathId() {
        val opt = CompactRangeOptions()
        var value = 2
        opt.setTargetPathId(value)
        assertEquals(value, opt.targetPathId())
        value = 3
        opt.setTargetPathId(value)
        assertEquals(value, opt.targetPathId())
    }

    @Test
    fun allowWriteStall() {
        val opt = CompactRangeOptions()
        var value = false
        opt.setAllowWriteStall(value)
        assertEquals(value, opt.allowWriteStall())
        value = true
        opt.setAllowWriteStall(value)
        assertEquals(value, opt.allowWriteStall())
    }

    @Test
    fun maxSubcompactions() {
        val opt = CompactRangeOptions()
        var value = 2
        opt.setMaxSubcompactions(value)
        assertEquals(value, opt.maxSubcompactions())
        value = 3
        opt.setMaxSubcompactions(value)
        assertEquals(value, opt.maxSubcompactions())
    }
}
