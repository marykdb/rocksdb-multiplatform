package maryk.rocksdb

actual class HistogramData {
    actual constructor(
        median: Double,
        percentile95: Double,
        percentile99: Double,
        average: Double,
        standardDeviation: Double
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual constructor(
        median: Double,
        percentile95: Double,
        percentile99: Double,
        average: Double,
        standardDeviation: Double,
        max: Double,
        count: Long,
        sum: Long,
        min: Double
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getMedian(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getPercentile95(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getPercentile99(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getAverage(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getStandardDeviation(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getMax(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getCount(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getSum(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getMin(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
