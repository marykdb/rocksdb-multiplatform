package maryk.rocksdb

import kotlinx.cinterop.CPointer
import maryk.EnumSet

actual class Statistics : RocksObject {
    actual constructor() : super(newStatistics())

    actual constructor(otherStatistics: Statistics)
        : super(newStatistics(otherStatistics.nativeHandle))

    actual constructor(ignoreHistograms: EnumSet<HistogramType>)
        : super(newStatistics(toArrayValues(ignoreHistograms)))

    actual constructor(
        ignoreHistograms: EnumSet<HistogramType>,
        otherStatistics: Statistics
    ) : super(newStatistics(toArrayValues(ignoreHistograms), otherStatistics.nativeHandle))

    actual fun statsLevel(): StatsLevel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStatsLevel(statsLevel: StatsLevel) {
    }

    actual fun getTickerCount(tickerType: TickerType): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getAndResetTickerCount(tickerType: TickerType): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getHistogramData(histogramType: HistogramType): HistogramData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getHistogramString(histogramType: HistogramType): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun reset() {
    }

}

private fun newStatistics(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newStatistics(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun toArrayValues(histogramTypes: EnumSet<HistogramType>): ByteArray {
    val values = ByteArray(histogramTypes.size)
    var i = 0
    for (histogramType in histogramTypes) {
        values[i++] = histogramType.value
    }
    return values
}

private fun newStatistics(ignoreHistograms: ByteArray): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newStatistics(toArrayValues: ByteArray, nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
