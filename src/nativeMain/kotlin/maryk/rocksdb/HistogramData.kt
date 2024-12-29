package maryk.rocksdb

import cnames.structs.rocksdb_statistics_histogram_data_t
import kotlinx.cinterop.CPointer

actual class HistogramData(
    val native: CPointer<rocksdb_statistics_histogram_data_t>
) {
    actual fun getMedian(): Double =
        rocksdb.rocksdb_statistics_histogram_data_get_median(native)

    actual fun getPercentile95(): Double =
        rocksdb.rocksdb_statistics_histogram_data_get_p95(native)

    actual fun getPercentile99(): Double =
        rocksdb.rocksdb_statistics_histogram_data_get_p99(native)

    actual fun getAverage(): Double =
        rocksdb.rocksdb_statistics_histogram_data_get_average(native)

    actual fun getStandardDeviation(): Double =
        rocksdb.rocksdb_statistics_histogram_data_get_std_dev(native)

    actual fun getMax(): Double =
        rocksdb.rocksdb_statistics_histogram_data_get_max(native)

    actual fun getCount(): Long =
        rocksdb.rocksdb_statistics_histogram_data_get_count(native).toLong()

    actual fun getSum(): Long  =
        rocksdb.rocksdb_statistics_histogram_data_get_sum(native).toLong()

    actual fun getMin(): Double  =
        rocksdb.rocksdb_statistics_histogram_data_get_min(native)
}
