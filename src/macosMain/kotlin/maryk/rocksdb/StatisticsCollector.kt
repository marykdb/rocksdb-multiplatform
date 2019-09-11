package maryk.rocksdb

actual class StatisticsCollector actual constructor(
    statsCollectorInputList: List<StatsCollectorInput>,
    statsCollectionInterval: Int
)
{
    actual fun start() {
    }

    actual fun shutDown(shutdownTimeout: Int) {
    }
}
