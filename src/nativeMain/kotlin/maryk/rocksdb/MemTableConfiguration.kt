@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import maryk.asSizeT
import maryk.asUInt32
import maryk.toUByte
import rocksdb.rocksdb_options_set_skip_list_rep
import rocksdb.rocksdb_options_set_vector_rep
import rocksdb.rocksdb_options_set_hash_link_list_rep_full
import rocksdb.rocksdb_options_set_hash_skip_list_rep

actual fun Options.configureMemTable(config: MemTableConfig): Options {
    checkOwningHandle()
    applyMemTableConfiguration(native, config)
    return this
}

actual fun ColumnFamilyOptions.configureMemTable(config: MemTableConfig): ColumnFamilyOptions {
    checkOwningHandle()
    applyMemTableConfiguration(native, config)
    return this
}

private fun applyMemTableConfiguration(options: CPointer<rocksdb_options_t>, config: MemTableConfig) {
    validateBuiltinMemTable(config)
    when (config) {
        is SkipListMemTableConfig -> rocksdb_options_set_skip_list_rep(options, config.lookahead().asSizeT())
        is VectorMemTableConfig -> rocksdb_options_set_vector_rep(options, config.reservedSize().toLong().asSizeT())
        is HashSkipListMemTableConfig -> rocksdb_options_set_hash_skip_list_rep(
            options, config.bucketCount().asSizeT(), config.height(), config.branchingFactor(),
        )
        is HashLinkedListMemTableConfig -> rocksdb_options_set_hash_link_list_rep_full(
            options, config.bucketCount().asSizeT(), config.hugePageTlbSize().asSizeT(),
            config.bucketEntriesLoggingThreshold(), config.ifLogBucketDistWhenFlush().toUByte(),
            config.thresholdUseSkiplist().asUInt32(),
        )
    }
}
