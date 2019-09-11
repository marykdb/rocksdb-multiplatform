package maryk.rocksdb

import kotlinx.cinterop.CPointer

/**
 * Note that disposeInternal should be called only after all
 * RocksDB instances referencing the compaction filter are closed.
 * Otherwise an undefined behavior will occur.
 */
actual abstract class AbstractCompactionFilter<T : AbstractSlice<*>> protected constructor(
    nativeHandle: CPointer<*>
) : RocksObject(nativeHandle)

actual open class AbstractCompactionFilterContext(
    private val fullCompaction: Boolean,
    private val manualCompaction: Boolean
) {
    actual fun isFullCompaction() = fullCompaction

    actual fun isManualCompaction() = manualCompaction
}
