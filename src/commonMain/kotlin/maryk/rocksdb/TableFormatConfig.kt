package maryk.rocksdb

/**
 * TableFormatConfig is used to config the internal Table format of a RocksDB.
 * To make a RocksDB to use a specific Table format, its associated
 * TableFormatConfig should be properly set and passed into Options via
 * Options.setTableFormatConfig() and open the db using that Options.
 */
expect abstract class TableFormatConfig
