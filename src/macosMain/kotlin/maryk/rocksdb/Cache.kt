package maryk.rocksdb

import rocksdb.RocksDBCache

actual abstract class Cache
    protected constructor(internal val native: RocksDBCache)
: RocksObject()
