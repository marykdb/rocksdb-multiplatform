package maryk.rocksdb

import cnames.structs.rocksdb_filterpolicy_t
import kotlinx.cinterop.CPointer

actual abstract class FilterPolicy : RocksObject() {
    /** Creates a separate policy whose ownership is transferred to the table options. */
    internal open fun createNativeCopy(): CPointer<rocksdb_filterpolicy_t> =
        error("This filter policy does not provide a Native policy factory")
}
