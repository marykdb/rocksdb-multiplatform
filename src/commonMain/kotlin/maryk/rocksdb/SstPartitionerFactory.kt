package maryk.rocksdb

/** Factory configuring SST partition boundaries during compaction. */
expect abstract class SstPartitionerFactory : RocksObject

/** Keep keys sharing the first [prefixLength] bytes together when choosing SST boundaries. */
expect class SstPartitionerFixedPrefixFactory(prefixLength: Long) : SstPartitionerFactory
