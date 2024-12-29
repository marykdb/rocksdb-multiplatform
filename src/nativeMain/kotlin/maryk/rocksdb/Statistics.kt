package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import maryk.wrapWithErrorThrower2

actual class Statistics internal constructor(
    internal val native: CPointer<rocksdb_options_t>?
) : RocksObject() {
    actual constructor() : this(native = null)

    actual fun statsLevel() = getStatsLevel(rocksdb.rocksdb_options_get_statistics_level(native).toUByte())

    actual fun setStatsLevel(statsLevel: StatsLevel) {
        rocksdb.rocksdb_options_set_statistics_level(native, statsLevel.value.toInt())
    }

    actual fun getTickerCount(tickerType: TickerType): Long {
        return rocksdb.rocksdb_options_statistics_get_ticker_count(native, tickerType.value).toLong()
    }

    actual fun getAndResetTickerCount(tickerType: TickerType): Long {
        throw NotImplementedError("DO SOMETHING")
    }

    actual fun getHistogramData(histogramType: HistogramType) :HistogramData {
        throw NotImplementedError("DO SOMETHING")
//        native.histogramDataForType(histogramType.value)
    }

    actual fun getHistogramString(histogramType: HistogramType): String {
        throw NotImplementedError("DO SOMETHING")
//        native.histogramStringForType(histogramType.value)
    }

    actual fun reset() {
        wrapWithErrorThrower2 { error ->
            throw NotImplementedError("DO SOMETHING")
        }
    }
}
