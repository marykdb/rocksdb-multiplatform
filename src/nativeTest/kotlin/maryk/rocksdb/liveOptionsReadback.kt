package maryk.rocksdb

import maryk.toCheckedLong
import rocksdb.rocksdb_options_get_bytes_per_sync
import rocksdb.rocksdb_options_get_max_open_files

internal actual fun liveOptionsMaxOpenFiles(options: DBOptions): Int = rocksdb_options_get_max_open_files(options.native)
internal actual fun liveOptionsBytesPerSync(options: DBOptions): Long =
    rocksdb_options_get_bytes_per_sync(options.native).toCheckedLong("persisted bytes per sync")
