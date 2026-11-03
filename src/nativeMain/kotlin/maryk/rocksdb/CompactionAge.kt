package maryk.rocksdb

import maryk.asUInt64
import maryk.toCheckedLong

/** RocksDB stores the public Java -2 default marker as UINT64_MAX - 1. */
internal fun Long.asCompactionAgeSeconds(): ULong =
    if (this == engineDefaultCompactionAgeSeconds) ULong.MAX_VALUE - 1uL else asUInt64()

internal fun ULong.compactionAgeSecondsToLong(): Long =
    if (this == ULong.MAX_VALUE - 1uL) engineDefaultCompactionAgeSeconds else toCheckedLong("compaction age seconds")
