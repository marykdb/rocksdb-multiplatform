package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import kotlinx.cinterop.value
import kotlinx.cinterop.toKString
import maryk.toByteArray
import maryk.wrapWithErrorThrower
import platform.posix.size_tVar
import rocksdb.rocksdb_free
import rocksdb.rocksdb_options_get_statistics_level
import rocksdb.rocksdb_options_set_statistics_level
import rocksdb.rocksdb_options_statistics_get_and_reset_ticker_count
import rocksdb.rocksdb_options_statistics_get_histogram_data
import rocksdb.rocksdb_options_statistics_get_histogram_string
import rocksdb.rocksdb_options_statistics_get_string
import rocksdb.rocksdb_options_statistics_get_ticker_count
import rocksdb.rocksdb_options_statistics_set_histograms
import rocksdb.rocksdb_options_enable_statistics
import rocksdb.rocksdb_statistics_histogram_data_create

actual class Statistics internal constructor(
    internal var native: CPointer<rocksdb_options_t>?,
    private val enabledHistograms: Set<HistogramType>,
) : RocksObject() {
    actual constructor() : this(native = null, enabledHistograms = emptySet())

    constructor(enabledHistograms: Set<HistogramType>) : this(
        native = null,
        enabledHistograms = enabledHistograms
    )

    private var statsLevel: StatsLevel? = null

    internal fun connectWithNative(native: CPointer<rocksdb_options_t>) {
        this.native = native
        rocksdb_options_enable_statistics(native)

        if (enabledHistograms.isNotEmpty()) {
            memScoped {
                val histogramArray = allocArray<UIntVar>(enabledHistograms.size)
                enabledHistograms.forEachIndexed { index, histogramType ->
                    histogramArray[index] = histogramType.value
                }
                rocksdb_options_statistics_set_histograms(
                    native,
                    histogramArray,
                    enabledHistograms.size.toULong()
                )
            }
        }

        statsLevel?.let { level -> setStatsLevel(level) }
    }

    actual fun statsLevel(): StatsLevel? = native?.let {
        getStatsLevel(rocksdb_options_get_statistics_level(it).toUByte())
    } ?: statsLevel

    actual fun setStatsLevel(statsLevel: StatsLevel) {
        this.statsLevel = statsLevel
        native?.let { rocksdb_options_set_statistics_level(it, statsLevel.value.toInt()) }
    }

    actual fun getTickerCount(tickerType: TickerType): Long {
        val attachedNative = requireNative()
        return rocksdb_options_statistics_get_ticker_count(attachedNative, tickerType.value).toLong()
    }

    actual fun getAndResetTickerCount(tickerType: TickerType): Long {
        val attachedNative = requireNative()
        return rocksdb_options_statistics_get_and_reset_ticker_count(attachedNative, tickerType.value)
            .toLong()
    }

    actual fun getHistogramData(histogramType: HistogramType): HistogramData {
        val attachedNative = requireNative()
        val histogramData = rocksdb_statistics_histogram_data_create()
        rocksdb_options_statistics_get_histogram_data(attachedNative, histogramType.value, histogramData)
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

    actual fun getHistogramString(histogramType: HistogramType): String {
        val attachedNative = requireNative()
        return memScoped {
            val lengthVar = alloc<size_tVar>()
            val raw = rocksdb_options_statistics_get_histogram_string(
                attachedNative,
                histogramType.value,
                lengthVar.ptr
            )
            raw?.let {
                val stringValue = it.toByteArray(lengthVar.value).decodeToString()
                rocksdb_free(it)
                stringValue
            } ?: ""
        }
    }

    actual fun reset() {
        native?.let { attached ->
            wrapWithErrorThrower { error ->
                rocksdb.rocksdb_options_statistics_reset(attached, error)
            }
        }
    }

    actual override fun toString(): String {
        val attachedNative = requireNative()
        val raw = rocksdb_options_statistics_get_string(attachedNative)
        return raw?.toKString().also { raw?.let { rocksdb_free(it) } } ?: ""
    }

    private fun requireNative(): CPointer<rocksdb_options_t> =
        native ?: error("Statistics must be attached to Options before use")
}

actual fun createStatistics(enabledHistograms: Set<HistogramType>): Statistics =
    Statistics(enabledHistograms)
