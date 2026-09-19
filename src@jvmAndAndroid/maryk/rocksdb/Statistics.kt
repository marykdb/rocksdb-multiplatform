package maryk.rocksdb

import java.util.EnumSet

actual typealias Statistics = org.rocksdb.Statistics

actual fun createStatistics(enabledHistograms: Set<HistogramType>): Statistics {
    val javaSet = when {
        enabledHistograms.isEmpty() -> EnumSet.noneOf(HistogramType::class.java)
        enabledHistograms is EnumSet<*> ->
            @Suppress("UNCHECKED_CAST")
            enabledHistograms as EnumSet<HistogramType>
        else -> EnumSet.copyOf(enabledHistograms)
    }
    return org.rocksdb.Statistics(javaSet)
}
