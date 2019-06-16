package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class CompactionOptionsUniversalTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun sizeRatio() {
        val sizeRatio = 4
        CompactionOptionsUniversal().use { opt ->
            opt.setSizeRatio(sizeRatio)
            assertEquals(sizeRatio, opt.sizeRatio())
        }
    }

    @Test
    fun minMergeWidth() {
        val minMergeWidth = 3
        CompactionOptionsUniversal().use { opt ->
            opt.setMinMergeWidth(minMergeWidth)
            assertEquals(minMergeWidth, opt.minMergeWidth())
        }
    }

    @Test
    fun maxMergeWidth() {
        val maxMergeWidth = Int.MAX_VALUE - 1234
        CompactionOptionsUniversal().use { opt ->
            opt.setMaxMergeWidth(maxMergeWidth)
            assertEquals(maxMergeWidth, opt.maxMergeWidth())
        }
    }

    @Test
    fun maxSizeAmplificationPercent() {
        val maxSizeAmplificationPercent = 150
        CompactionOptionsUniversal().use { opt ->
            opt.setMaxSizeAmplificationPercent(maxSizeAmplificationPercent)
            assertEquals(maxSizeAmplificationPercent, opt.maxSizeAmplificationPercent())
        }
    }

    @Test
    fun compressionSizePercent() {
        val compressionSizePercent = 500
        CompactionOptionsUniversal().use { opt ->
            opt.setCompressionSizePercent(compressionSizePercent)
            assertEquals(compressionSizePercent, opt.compressionSizePercent())
        }
    }

    @Test
    fun stopStyle() {
        val stopStyle = CompactionStopStyle.CompactionStopStyleSimilarSize
        CompactionOptionsUniversal().use { opt ->
            opt.setStopStyle(stopStyle)
            assertEquals(stopStyle, opt.stopStyle())
        }
    }

    @Test
    fun allowTrivialMove() {
        val allowTrivialMove = true
        CompactionOptionsUniversal().use { opt ->
            opt.setAllowTrivialMove(allowTrivialMove)
            assertEquals(allowTrivialMove, opt.allowTrivialMove())
        }
    }
}
