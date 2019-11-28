package maryk.rocksdb

import rocksdb.RocksDBEnvPriority
import rocksdb.RocksDBEnvPriority.RocksDBEnvPriorityBottom
import rocksdb.RocksDBEnvPriority.RocksDBEnvPriorityHigh
import rocksdb.RocksDBEnvPriority.RocksDBEnvPriorityLow
import rocksdb.RocksDBEnvPriority.RocksDBEnvPriorityTotal
import rocksdb.RocksDBEnvPriority.RocksDBEnvPriorityUser

actual enum class Priority(
    internal val value: RocksDBEnvPriority
) {
    BOTTOM(RocksDBEnvPriorityBottom),
    LOW(RocksDBEnvPriorityLow),
    HIGH(RocksDBEnvPriorityHigh),
    TOTAL(RocksDBEnvPriorityTotal),
    USER(RocksDBEnvPriorityUser);
}
