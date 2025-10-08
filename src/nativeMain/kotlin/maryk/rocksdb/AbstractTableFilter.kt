package maryk.rocksdb

import cnames.structs.rocksdb_table_filter_t
import cnames.structs.rocksdb_tableproperties_t
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import maryk.toUByte
import rocksdb.rocksdb_table_filter_create
import rocksdb.rocksdb_table_filter_destroy

actual abstract class AbstractTableFilter protected actual constructor() : RocksCallbackObject() {
    private val stableRef = StableRef.create(this)

    internal val native: CPointer<rocksdb_table_filter_t> = rocksdb_table_filter_create(
        stableRef.asCPointer(),
        staticCFunction(::tableFilterDestructor),
        staticCFunction(::tableFilterCallback),
    ) ?: error("Unable to create table filter")

    protected actual abstract fun filter(tableProperties: TableProperties): Boolean

    internal fun shouldInclude(tableProperties: TableProperties): Boolean = filter(tableProperties)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_table_filter_destroy(native)
            super.close()
        }
    }
}

private fun tableFilterDestructor(state: COpaquePointer?) {
    state?.asStableRef<AbstractTableFilter>()?.dispose()
}

private fun tableFilterCallback(
    state: COpaquePointer?,
    properties: CPointer<rocksdb_tableproperties_t>?,
): UByte {
    val filter = state?.asStableRef<AbstractTableFilter>()?.get() ?: return 0u
    val shouldInclude = filter.shouldInclude(TableProperties(properties))
    return shouldInclude.toUByte()
}
