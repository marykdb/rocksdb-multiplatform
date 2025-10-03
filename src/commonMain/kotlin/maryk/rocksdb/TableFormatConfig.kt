package maryk.rocksdb

/**
 * TableFormatConfig configures the table factory backing SST files produced by RocksDB.
 * Instances are typically passed into [Options.setTableFormatConfig] or
 * [ColumnFamilyOptions.setTableFormatConfig] to bind the desired table format.
 */
expect abstract class TableFormatConfig
