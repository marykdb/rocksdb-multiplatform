package maryk.rocksdb

/**
 * MemTableConfig is used to config the internal mem-table of a RocksDB.
 * It is required for each memtable to have one such sub-class.
 *
 * To make a RocksDB to use a specific MemTable format, its associated
 * MemTableConfig should be properly set and passed into Options
 * via Options.setMemTableFactory() and open the db using that Options.
 *
 * @see Options
 */
expect abstract class MemTableConfig()
