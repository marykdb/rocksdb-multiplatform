@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_sst_partitioner_factory_t
import kotlinx.cinterop.CPointer
import maryk.asSizeT

actual abstract class SstPartitionerFactory internal constructor(
    internal val native: CPointer<rocksdb_sst_partitioner_factory_t>,
) : RocksObject() {
    override fun close() {
        if (tryClose()) {
            rocksdb.rocksdb_sst_partitioner_factory_destroy(native)
            super.close()
        }
    }
}

actual class SstPartitionerFixedPrefixFactory actual constructor(prefixLength: Long) : SstPartitionerFactory(
    requireNotNull(rocksdb.rocksdb_sst_partitioner_fixed_prefix_factory_create(prefixLength.asSizeT())) {
        "Unable to allocate SST partitioner factory"
    },
)
