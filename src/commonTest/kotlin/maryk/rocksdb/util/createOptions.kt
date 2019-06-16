package maryk.rocksdb.util

import maryk.rocksdb.CompactionPriority.ByCompensatedSize
import maryk.rocksdb.Options
import maryk.rocksdb.WALRecoveryMode.TolerateCorruptedTailRecords

/**
 * Get the options for log iteration tests.
 * @return the options
 */
fun optionsForLogIterTest() = defaultOptions().apply {
    setCreateIfMissing(true)
    setWalTtlSeconds(1000)
}

/**
 * Get the default options.
 * @return the options
 */
fun defaultOptions(): Options = Options().apply {
    setWriteBufferSize(4090L * 4096L)
    setTargetFileSizeBase(2 * 1024 * 1024L)
    setMaxBytesForLevelBase(10 * 1024 * 1024L)
    setMaxOpenFiles(5000)
    setWalRecoveryMode(TolerateCorruptedTailRecords)
    setCompactionPriority(ByCompensatedSize)
}
