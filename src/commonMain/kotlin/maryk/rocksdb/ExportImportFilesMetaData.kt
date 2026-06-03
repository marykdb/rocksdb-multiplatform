package maryk.rocksdb

/**
 * Metadata returned by [Checkpoint.exportColumnFamily] and consumed by
 * [RocksDB.createColumnFamilyWithImport].
 */
expect class ExportImportFilesMetaData : RocksObject
