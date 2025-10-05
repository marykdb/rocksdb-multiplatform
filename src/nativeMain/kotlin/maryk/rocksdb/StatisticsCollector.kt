@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import rocksdb.rocksdb_options_statistics_get_histogram_data
import rocksdb.rocksdb_options_statistics_get_ticker_count
import rocksdb.rocksdb_statistics_histogram_data_create
import rocksdb.rocksdb_statistics_histogram_data_destroy
import rocksdb.rocksdb_statistics_histogram_data_get_average
import rocksdb.rocksdb_statistics_histogram_data_get_count
import rocksdb.rocksdb_statistics_histogram_data_get_max
import rocksdb.rocksdb_statistics_histogram_data_get_median
import rocksdb.rocksdb_statistics_histogram_data_get_min
import rocksdb.rocksdb_statistics_histogram_data_get_p95
import rocksdb.rocksdb_statistics_histogram_data_get_p99
import rocksdb.rocksdb_statistics_histogram_data_get_std_dev
import rocksdb.rocksdb_statistics_histogram_data_get_sum
import kotlin.concurrent.AtomicInt
import kotlin.concurrent.AtomicReference

actual class StatisticsCollector actual constructor(
    statsCollectorInputList: List<StatsCollectorInput>,
    statsCollectionIntervalInMilliSeconds: Int
) {
    private val intervalMillis = statsCollectionIntervalInMilliSeconds.coerceAtLeast(0)
    private val running = AtomicInt(0)
    private val completed = AtomicInt(0)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val jobRef = AtomicReference<Job?>(null)

    private val state: NativeCollectorState

    init {
        require(statsCollectorInputList.isNotEmpty()) { "Statistics collector inputs must not be empty" }
        val inputs = statsCollectorInputList.map { input ->
            val nativeStatistics = input.statistics.native
                ?: error("Statistics must be attached to Options before starting collection")
            NativeCollectorInput(nativeStatistics, input.callback)
        }
        state = NativeCollectorState(inputs, intervalMillis, running, completed)
    }

    actual fun start() {
        if (!running.compareAndSet(0, 1)) {
            return
        }
        completed.value = 0
        val collectorState = state
        val job = scope.launch {
            try {
                collectorState.run()
            } finally {
                running.value = 0
            }
        }
        jobRef.value = job
        job.invokeOnCompletion {
            jobRef.compareAndSet(job, null)
        }
    }

    actual fun shutDown(shutdownTimeout: Int) {
        if (!running.compareAndSet(1, 0)) {
            return
        }
        val job = jobRef.value
        if (job != null) {
            jobRef.compareAndSet(job, null)
            job.cancel()
            val waitMillis = shutdownTimeout.coerceAtLeast(0).toLong()
            runBlocking {
                if (waitMillis <= 0) {
                    job.join()
                } else {
                    withTimeoutOrNull(waitMillis) { job.join() }
                }
            }
        }
        completed.value = 0
    }
}

private class NativeCollectorState(
    private val inputs: List<NativeCollectorInput>,
    private val intervalMillis: Int,
    private val running: AtomicInt,
    private val completed: AtomicInt
) {
    suspend fun run() {
        try {
            while (running.value == 1 && currentCoroutineContext().isActive) {
                collectOnce()
                if (running.value != 1 || !currentCoroutineContext().isActive) {
                    break
                }
                if (intervalMillis > 0) {
                    delay(intervalMillis.toLong())
                } else {
                    yield()
                }
            }
        } finally {
            completed.value = 1
            running.value = 0
        }
    }

    private fun collectOnce() {
        for (input in inputs) {
            val statistics = input.statistics
            val callback = input.callback

            for (ticker in TickerType.entries) {
                if (ticker != TickerType.TICKER_ENUM_MAX) {
                    val tickerValue = rocksdb_options_statistics_get_ticker_count(statistics, ticker.value)
                        .toLong()
                    callback.tickerCallback(ticker, tickerValue)
                }
            }

            for (histogram in HistogramType.entries) {
                if (histogram != HistogramType.HISTOGRAM_ENUM_MAX) {
                    val histogramData = readHistogram(statistics, histogram)
                    callback.histogramCallback(histogram, histogramData)
                }
            }
        }
    }

    private fun readHistogram(
        statistics: CPointer<rocksdb_options_t>,
        histogram: HistogramType
    ): HistogramData {
        val histogramData = rocksdb_statistics_histogram_data_create()
        try {
            rocksdb_options_statistics_get_histogram_data(statistics, histogram.value, histogramData)
            return HistogramData(
                median = rocksdb_statistics_histogram_data_get_median(histogramData),
                p95 = rocksdb_statistics_histogram_data_get_p95(histogramData),
                p99 = rocksdb_statistics_histogram_data_get_p99(histogramData),
                average = rocksdb_statistics_histogram_data_get_average(histogramData),
                stdDev = rocksdb_statistics_histogram_data_get_std_dev(histogramData),
                max = rocksdb_statistics_histogram_data_get_max(histogramData),
                count = rocksdb_statistics_histogram_data_get_count(histogramData),
                sum = rocksdb_statistics_histogram_data_get_sum(histogramData),
                min = rocksdb_statistics_histogram_data_get_min(histogramData)
            )
        } finally {
            rocksdb_statistics_histogram_data_destroy(histogramData)
        }
    }
}

private data class NativeCollectorInput(
    val statistics: CPointer<rocksdb_options_t>,
    val callback: StatisticsCollectorCallback
)
