@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import kotlinx.cinterop.value
import kotlinx.cinterop.toKString
import maryk.asSizeT
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
import rocksdb.rocksdb_statistics_histogram_data_destroy
import kotlin.concurrent.AtomicInt

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
    private val attached = AtomicInt(if (native == null) 0 else 1)
    private val activeReaders = AtomicInt(0)

    @OptIn(UnsafeNumber::class)
    internal fun connectWithNative(native: CPointer<rocksdb_options_t>) {
        check(isOwningHandle()) { "Statistics is closed." }
        attached.value = 0
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
                    enabledHistograms.size.asSizeT()
                )
            }
        }

        statsLevel?.let { level ->
            rocksdb_options_set_statistics_level(native, level.value.toInt())
        }
        attached.value = 1
    }

    internal fun disconnectFromNative(native: CPointer<rocksdb_options_t>) {
        if (this.native != native) {
            return
        }
        attached.value = 0
        while (activeReaders.value != 0) {
            // Statistics calls are short C API reads; wait until any in-flight call
            // drops the borrowed options pointer before the owning options is freed.
        }
        if (this.native == native) {
            this.native = null
        }
    }

    internal fun isAttachedToNative(): Boolean =
        isOwningHandle() && attached.value == 1 && native != null

    override fun close() {
        if (tryClose()) {
            attached.value = 0
            while (activeReaders.value != 0) {
            }
            native = null
        }
    }

    actual fun statsLevel(): StatsLevel? {
        check(isOwningHandle()) { "Statistics is closed." }
        return if (isAttachedToNative()) {
            withNative { getStatsLevel(rocksdb_options_get_statistics_level(it).toUByte()) }
        } else {
            statsLevel
        }
    }

    actual fun setStatsLevel(statsLevel: StatsLevel) {
        check(isOwningHandle()) { "Statistics is closed." }
        this.statsLevel = statsLevel
        if (isAttachedToNative()) {
            withNative { rocksdb_options_set_statistics_level(it, statsLevel.value.toInt()) }
        }
    }

    actual fun getTickerCount(tickerType: TickerType): Long {
        return withNative {
            rocksdb_options_statistics_get_ticker_count(it, tickerType.value).toLong()
        }
    }

    actual fun getAndResetTickerCount(tickerType: TickerType): Long {
        return withNative {
            rocksdb_options_statistics_get_and_reset_ticker_count(it, tickerType.value).toLong()
        }
    }

    actual fun getHistogramData(histogramType: HistogramType): HistogramData {
        val histogramData = rocksdb_statistics_histogram_data_create()
        try {
            return withNative {
                rocksdb_options_statistics_get_histogram_data(it, histogramType.value, histogramData)
                HistogramData(
                    median = rocksdb.rocksdb_statistics_histogram_data_get_median(histogramData),
                    p95 = rocksdb.rocksdb_statistics_histogram_data_get_p95(histogramData),
                    p99 = rocksdb.rocksdb_statistics_histogram_data_get_p99(histogramData),
                    average = rocksdb.rocksdb_statistics_histogram_data_get_average(histogramData),
                    stdDev = rocksdb.rocksdb_statistics_histogram_data_get_std_dev(histogramData),
                    max = rocksdb.rocksdb_statistics_histogram_data_get_max(histogramData),
                    count = rocksdb.rocksdb_statistics_histogram_data_get_count(histogramData),
                    sum = rocksdb.rocksdb_statistics_histogram_data_get_sum(histogramData),
                    min = rocksdb.rocksdb_statistics_histogram_data_get_min(histogramData),
                )
            }
        } finally {
            rocksdb_statistics_histogram_data_destroy(histogramData)
        }
    }

    actual fun getHistogramString(histogramType: HistogramType): String {
        return withNative { attachedNative ->
            memScoped {
                val lengthVar = alloc<size_tVar>()
                val raw = rocksdb_options_statistics_get_histogram_string(
                    attachedNative,
                    histogramType.value,
                    lengthVar.ptr
                )
                raw?.let {
                    try {
                        it.toByteArray(lengthVar.value).decodeToString()
                    } finally {
                        rocksdb_free(it)
                    }
                } ?: ""
            }
        }
    }

    actual fun reset() {
        check(isOwningHandle()) { "Statistics is closed." }
        if (!isAttachedToNative()) {
            return
        }
        withNative { attached ->
            wrapWithErrorThrower { error ->
                rocksdb.rocksdb_options_statistics_reset(attached, error)
            }
        }
    }

    actual override fun toString(): String {
        return withNative { attachedNative ->
            val raw = rocksdb_options_statistics_get_string(attachedNative)
            raw?.let {
                try {
                    it.toKString()
                } finally {
                    rocksdb_free(it)
                }
            } ?: ""
        }
    }

    private fun <T> withNative(block: (CPointer<rocksdb_options_t>) -> T): T {
        check(isOwningHandle()) { "Statistics is closed." }
        acquireReader()
        try {
            if (attached.value != 1) {
                error("Statistics must be attached to Options before use")
            }
            val attachedNative = native
                ?: error("Statistics must be attached to Options before use")
            return block(attachedNative)
        } finally {
            releaseReader()
        }
    }

    private fun acquireReader() {
        while (true) {
            if (attached.value != 1) {
                error("Statistics must be attached to Options before use")
            }
            val current = activeReaders.value
            if (activeReaders.compareAndSet(current, current + 1)) {
                if (attached.value == 1) {
                    return
                }
                releaseReader()
                error("Statistics must be attached to Options before use")
            }
        }
    }

    private fun releaseReader() {
        while (true) {
            val current = activeReaders.value
            if (activeReaders.compareAndSet(current, current - 1)) {
                return
            }
        }
    }
}

actual fun createStatistics(enabledHistograms: Set<HistogramType>): Statistics =
    Statistics(enabledHistograms)
