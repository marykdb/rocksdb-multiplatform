package maryk.rocksdb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
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
            check(input.statistics.isAttachedToNative()) {
                "Statistics must be attached to Options before starting collection"
            }
            NativeCollectorInput(input.statistics, input.callback)
        }
        state = NativeCollectorState(inputs, intervalMillis, running, completed)
    }

    actual fun start() {
        if (!running.compareAndSet(0, 1)) {
            return
        }
        completed.value = 0
        val collectorState = state
        val job = scope.launch(start = CoroutineStart.LAZY) {
            try {
                collectorState.run()
            } finally {
                running.value = 0
            }
        }
        if (!jobRef.compareAndSet(null, job)) {
            running.compareAndSet(1, 0)
            completed.value = 0
            job.cancel()
            return
        }
        job.invokeOnCompletion {
            jobRef.compareAndSet(job, null)
        }
        job.start()
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
                    val tickerValue = try {
                        statistics.getTickerCount(ticker)
                    } catch (_: IllegalStateException) {
                        running.value = 0
                        return
                    }
                    callback.tickerCallback(ticker, tickerValue)
                }
            }

            for (histogram in HistogramType.entries) {
                if (histogram != HistogramType.HISTOGRAM_ENUM_MAX) {
                    val histogramData = try {
                        statistics.getHistogramData(histogram)
                    } catch (_: IllegalStateException) {
                        running.value = 0
                        return
                    }
                    callback.histogramCallback(histogram, histogramData)
                }
            }
        }
    }
}

private data class NativeCollectorInput(
    val statistics: Statistics,
    val callback: StatisticsCollectorCallback
)
