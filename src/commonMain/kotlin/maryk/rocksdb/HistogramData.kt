package maryk.rocksdb

expect class HistogramData {
    constructor(
        median: Double,
        percentile95: Double,
        percentile99: Double,
        average: Double,
        standardDeviation: Double
    )

    constructor(
        median: Double,
        percentile95: Double,
        percentile99: Double,
        average: Double,
        standardDeviation: Double,
        max: Double,
        count: Long,
        sum: Long,
        min: Double
    )

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
