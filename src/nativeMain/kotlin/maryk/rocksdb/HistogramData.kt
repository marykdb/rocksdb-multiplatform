package maryk.rocksdb

import maryk.toCheckedLong

actual class HistogramData(
    val median: Double,
    val p95: Double,
    val p99: Double,
    val average: Double,
    val stdDev: Double,
    val max: Double,
    val count: ULong,
    val sum: ULong,
    val min: Double,
) {
    actual fun getMedian(): Double = median

    actual fun getPercentile95(): Double = p95

    actual fun getPercentile99(): Double = p99

    actual fun getAverage(): Double = average

    actual fun getStandardDeviation(): Double = stdDev

    actual fun getMax(): Double = max

    actual fun getCount(): Long = count.toCheckedLong("histogram count")

    actual fun getSum(): Long = sum.toCheckedLong("histogram sum")

    actual fun getMin(): Double = min
}
