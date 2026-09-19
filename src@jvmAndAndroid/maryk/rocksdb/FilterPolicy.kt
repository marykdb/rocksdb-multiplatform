package maryk.rocksdb

/**
 * Filters are stored in rocksdb and are consulted automatically
 * by rocksdb to decide whether or not to read some
 * information from disk. In many cases, a filter can cut down the
 * number of disk seeks form a handful to a single disk seek per
 * DB::Get() call.
 */
actual typealias FilterPolicy = org.rocksdb.Filter
