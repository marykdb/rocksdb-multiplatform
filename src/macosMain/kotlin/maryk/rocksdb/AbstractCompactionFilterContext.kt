package maryk.rocksdb

/**
 * Note that disposeInternal should be called only after all
 * RocksDB instances referencing the compaction filter are closed.
 * Otherwise an undefined behavior will occur.
 */
actual abstract class AbstractCompactionFilter<T : AbstractSlice<*>> protected constructor() : RocksObject()

actual open class AbstractCompactionFilterContext(
    private val fullCompaction: Boolean,
    private val manualCompaction: Boolean
) {
    actual fun isFullCompaction() = fullCompaction

    actual fun isManualCompaction() = manualCompaction
}
