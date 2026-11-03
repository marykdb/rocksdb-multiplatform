package maryk.rocksdb

/**
 * Copy a built-in memtable configuration. Hash representations require a prefix extractor;
 * disable concurrent memtable writes for hash and vector representations.
 */
expect fun Options.configureMemTable(config: MemTableConfig): Options
expect fun ColumnFamilyOptions.configureMemTable(config: MemTableConfig): ColumnFamilyOptions

internal fun validateBuiltinMemTable(config: MemTableConfig) {
    when (config) {
        is SkipListMemTableConfig -> require(config.lookahead() >= 0)
        is VectorMemTableConfig -> require(config.reservedSize() >= 0)
        is HashSkipListMemTableConfig -> {
            require(config.bucketCount() > 0)
            require(config.height() > 0)
            require(config.branchingFactor() > 1)
        }
        is HashLinkedListMemTableConfig -> {
            require(config.bucketCount() > 0)
            require(config.hugePageTlbSize() >= 0)
            require(config.bucketEntriesLoggingThreshold() >= 0)
            require(config.thresholdUseSkiplist() > 0)
        }
        else -> throw IllegalArgumentException("Only built-in common memtable configurations are supported")
    }
}
