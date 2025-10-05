package maryk.rocksdb

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

actual class StatisticsCollector actual constructor(
    private val statsCollectorInputList: List<StatsCollectorInput>,
    statsCollectionIntervalInMilliSeconds: Int
) {
    private val intervalMillis = statsCollectionIntervalInMilliSeconds.coerceAtLeast(0)
    private val started = AtomicBoolean(false)
    private val executor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "RocksDB-StatisticsCollector").apply { isDaemon = true }
        }
    private var future: Future<*>? = null

    actual fun start() {
        if (!started.compareAndSet(false, true)) {
            return
        }
        future = if (intervalMillis == 0) {
            executor.submit { loopUntilInterrupted() }
        } else {
            executor.scheduleAtFixedRate(
                { collectOnce() },
                0L,
                intervalMillis.toLong(),
                TimeUnit.MILLISECONDS
            )
        }
    }

    actual fun shutDown(shutdownTimeout: Int) {
        if (!started.compareAndSet(true, false)) {
            return
        }
        future?.cancel(true)
        future = null
        executor.shutdownNow()
        val timeout = shutdownTimeout.toLong().coerceAtLeast(0L)
        if (timeout > 0L) {
            executor.awaitTermination(timeout, TimeUnit.MILLISECONDS)
        } else {
            executor.awaitTermination(0, TimeUnit.MILLISECONDS)
        }
    }

    private fun loopUntilInterrupted() {
        while (!Thread.currentThread().isInterrupted) {
            collectOnce()
            if (Thread.currentThread().isInterrupted) {
                break
            }
            Thread.yield()
        }
    }

    private fun collectOnce() {
        for (input in statsCollectorInputList) {
            val statistics = input.statistics
            val callback = input.callback

            for (ticker in TickerType.entries) {
                if (ticker != TickerType.TICKER_ENUM_MAX) {
                    val tickerValue = statistics.getTickerCount(ticker)
                    callback.tickerCallback(ticker, tickerValue)
                }
            }

            for (histogram in HistogramType.entries) {
                if (histogram != HistogramType.HISTOGRAM_ENUM_MAX) {
                    val histogramData = statistics.getHistogramData(histogram)
                    callback.histogramCallback(histogram, histogramData)
                }
            }
        }
    }
}
