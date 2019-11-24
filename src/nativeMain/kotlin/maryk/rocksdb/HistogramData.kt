package maryk.rocksdb

import rocksdb.RocksDBStatisticsHistogram

actual class HistogramData(
    val native: RocksDBStatisticsHistogram
) {
    actual fun getMedian(): Double {
        return native.median
    }

    actual fun getPercentile95(): Double {
        return native.percentile95
    }

    actual fun getPercentile99(): Double {
        return native.percentile99
    }

    actual fun getAverage(): Double {
        return native.average
    }

    actual fun getStandardDeviation(): Double {
        return native.standardDeviation
    }

    actual fun getMax(): Double {
        return native.max
    }

    actual fun getCount(): Long {
        return native.count.toLong()
    }

    actual fun getSum(): Long {
        return native.sum.toLong()
    }

    actual fun getMin(): Double {
        return native.min
    }
}
