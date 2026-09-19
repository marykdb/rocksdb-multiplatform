package maryk.rocksdb

expect class HistogramData {
    fun getMedian(): Double

    fun getPercentile95(): Double

    fun getPercentile99(): Double

    fun getAverage(): Double

    fun getStandardDeviation(): Double

    fun getMax(): Double

    fun getCount(): Long

    fun getSum(): Long

    fun getMin(): Double
}
