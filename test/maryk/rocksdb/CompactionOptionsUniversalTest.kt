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
        CompactionOptionsUniversal().use { options ->
            options.setSizeRatio(sizeRatio)
            assertEquals(sizeRatio, options.sizeRatio())
        }
    }

    @Test
    fun minMergeWidth() {
        val minMergeWidth = 3
        CompactionOptionsUniversal().use { options ->
            options.setMinMergeWidth(minMergeWidth)
            assertEquals(minMergeWidth, options.minMergeWidth())
        }
    }

    @Test
    fun maxMergeWidth() {
        val maxMergeWidth = Int.MAX_VALUE - 1234
        CompactionOptionsUniversal().use { options ->
            options.setMaxMergeWidth(maxMergeWidth)
            assertEquals(maxMergeWidth, options.maxMergeWidth())
        }
    }

    @Test
    fun maxSizeAmplificationPercent() {
        val amplification = 150
        CompactionOptionsUniversal().use { options ->
            options.setMaxSizeAmplificationPercent(amplification)
            assertEquals(amplification, options.maxSizeAmplificationPercent())
        }
    }

    @Test
    fun compressionSizePercent() {
        val compressionSize = 500
        CompactionOptionsUniversal().use { options ->
            options.setCompressionSizePercent(compressionSize)
            assertEquals(compressionSize, options.compressionSizePercent())
        }
    }

    @Test
    fun stopStyle() {
        val stopStyle = CompactionStopStyle.CompactionStopStyleSimilarSize
        CompactionOptionsUniversal().use { options ->
            options.setStopStyle(stopStyle)
            assertEquals(stopStyle, options.stopStyle())
        }
    }
}
