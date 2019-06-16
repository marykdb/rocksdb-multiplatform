package maryk.rocksdb

/**
 * Enum CompactionStyle
 *
 * RocksDB supports different styles of compaction. Available
 * compaction styles can be chosen using this enumeration.
 *
 *  1. **LEVEL** - Level based Compaction style
 *  2. **UNIVERSAL** - Universal Compaction Style is a
 *     compaction style, targeting the use cases requiring lower write
 *     amplification, trading off read amplification and space
 *     amplification.
 *  3. **FIFO** - FIFO compaction style is the simplest
 *     compaction strategy. It is suited for keeping event log data with
 *     very low overhead (query log for example). It periodically deletes
 *     the old data, so it's basically a TTL compaction style.
 *  4. **NONE** - Disable background compaction.
 *     Compaction jobs are submitted [RocksDB.compactFiles] ().
 *
 * @see [Universal Compaction](https://github.com/facebook/rocksdb/wiki/Universal-Compaction)
 * @see [FIFO Compaction](https://github.com/facebook/rocksdb/wiki/FIFO-compaction-style)
 */
expect enum class CompactionStyle {
    LEVEL,
    UNIVERSAL,
    FIFO,
    NONE;
}

