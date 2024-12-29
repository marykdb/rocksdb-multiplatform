package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.nativeHeap.alloc
import maryk.wrapWithErrorThrower2
import rocksdb.rocksdb_options_get_statistics_level
import rocksdb.rocksdb_options_set_statistics_level
import rocksdb.rocksdb_options_statistics_get_histogram_data
import rocksdb.rocksdb_options_statistics_get_ticker_count
import rocksdb.rocksdb_statistics_histogram_data_create

actual class Statistics internal constructor(
    internal var native: CPointer<rocksdb_options_t>?
) : RocksObject() {
    actual constructor() : this(native = null)

    private var statsLevel: StatsLevel? = null

    internal fun connectWithNative(native: CPointer<rocksdb_options_t>) {
        this.native = native
        statsLevel?.let { level -> setStatsLevel(level) }
    }

    actual fun statsLevel(): StatsLevel? = native?.let {
        getStatsLevel(rocksdb_options_get_statistics_level(native).toUByte())
    } ?: statsLevel

    actual fun setStatsLevel(statsLevel: StatsLevel) {
        if (native != null) {
            rocksdb_options_set_statistics_level(native, statsLevel.value.toInt())
        } else {
            statsLevel
        }
    }

    actual fun getTickerCount(tickerType: TickerType): Long {
        return rocksdb_options_statistics_get_ticker_count(native, tickerType.value).toLong()
    }

    actual fun getHistogramData(histogramType: HistogramType) :HistogramData {
        val histogramData = rocksdb_statistics_histogram_data_create()
        rocksdb_options_statistics_get_histogram_data(native, histogramType.value.toUInt(), histogramData)
        return HistogramData(
            median = rocksdb.rocksdb_statistics_histogram_data_get_median(histogramData),
            p95 = rocksdb.rocksdb_statistics_histogram_data_get_p95(histogramData),
            p99 = rocksdb.rocksdb_statistics_histogram_data_get_p99(histogramData),
            average = rocksdb.rocksdb_statistics_histogram_data_get_average(histogramData),
            stdDev = rocksdb.rocksdb_statistics_histogram_data_get_std_dev(histogramData),
            max = rocksdb.rocksdb_statistics_histogram_data_get_max(histogramData),
            count = rocksdb.rocksdb_statistics_histogram_data_get_count(histogramData),
            sum = rocksdb.rocksdb_statistics_histogram_data_get_sum(histogramData),
            min = rocksdb.rocksdb_statistics_histogram_data_get_min(histogramData),
        ).also {
            rocksdb.rocksdb_statistics_histogram_data_destroy(histogramData)
        }
    }

    actual fun reset() {
        wrapWithErrorThrower2 { error ->
            throw NotImplementedError("DO SOMETHING")
        }
    }
}
