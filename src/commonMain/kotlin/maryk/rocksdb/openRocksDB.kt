package maryk.rocksdb

/**
 * The factory constructor of RocksDB that opens a RocksDB instance given
 * the [path] to the database using the default options w/ createIfMissing
 * set to true.
 *
 * Returns a {@link RocksDB} instance on success, null if the specified
 *     {@link RocksDB} can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying
 *    native library.
 * @see Options.createIfMissing
 */
expect fun openRocksDB(path: String): RocksDB

/**
 * The factory constructor of RocksDB that opens a RocksDB instance given
 * the path to the database using the specified [options] and db [path].
 *
 * <p>
 * Options instance *should* not be disposed before all DBs using this options
 * instance have been closed. If user doesn't call options dispose explicitly,
 * then this options instance will be GC'd automatically.</p>
 * <p>
 * Options instance can be re-used to open multiple DBs if DB statistics is
 * not used. If DB statistics are required, then its recommended to open DB
 * with new Options instance as underlying native statistics instance does not
 * use any locks to prevent concurrent updates.</p>
 *
 * Returns a {@link RocksDB} instance on success, null if the specified
 *     {@link RocksDB} can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying native library.
 */
expect fun openRocksDB(options: Options, path: String): RocksDB

/**
 * The factory constructor of RocksDB that opens a RocksDB instance given
 * the path to the database using the specified options and db path and a list
 * of column family names.
 *
 *
 * If opened in read write mode every existing column family name must be
 * passed within the list to this method.
 *
 *
 * If opened in read-only mode only a subset of existing column families must
 * be passed to this method.
 *
 *
 * Options instance *should* not be disposed before all DBs using this options
 * instance have been closed. If user doesn't call options dispose explicitly,
 * then this options instance will be GC'd automatically
 *
 *
 * ColumnFamily handles are disposed when the RocksDB instance is disposed.
 *
 *
 * @param path the path to the rocksdb.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 * on open.
 * @return a [RocksDB] instance on success, null if the specified
 * [RocksDB] can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 * @see DBOptions.setCreateIfMissing
 */
expect fun openRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: List<ColumnFamilyHandle>
): RocksDB

/**
 * The factory constructor of RocksDB that opens a RocksDB instance given
 * the path to the database using the specified options and db path and a list
 * of column family names.
 *
 * If opened in read write mode every existing column family name must be
 * passed within the list to this method.
 *
 * If opened in read-only mode only a subset of existing column families must
 * be passed to this method.
 *
 * Options instance *should* not be disposed before all DBs using this options
 * instance have been closed. If user doesn't call options dispose explicitly,
 * then this options instance will be GC'd automatically.
 *
 * Options instance can be re-used to open multiple DBs if DB statistics is
 * not used. If DB statistics are required, then its recommended to open DB
 * with new Options instance as underlying native statistics instance does not
 * use any locks to prevent concurrent updates.
 *
 * ColumnFamily handles are disposed when the RocksDB instance is disposed.
 *
 * @param options [org.rocksdb.DBOptions] instance.
 * @param path the path to the rocksdb.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 * on open.
 * @return a [RocksDB] instance on success, null if the specified
 * [RocksDB] can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 *
 * @see DBOptions.setCreateIfMissing
 */
expect fun openRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: List<ColumnFamilyHandle>
): RocksDB

/**
 * The factory constructor of RocksDB that opens a RocksDB instance in
 * Read-Only mode given the path to the database using the default
 * options.
 *
 * @param path the path to the RocksDB.
 * @return a [RocksDB] instance on success, null if the specified
 * [RocksDB] can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 */
expect fun openReadOnlyRocksDB(path: String): RocksDB

/**
 * The factory constructor of RocksDB that opens a RocksDB instance in
 * Read-Only mode given the path to the database using the default
 * options.
 *
 * @param path the path to the RocksDB.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 * on open.
 * @return a [RocksDB] instance on success, null if the specified
 * [RocksDB] can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 */
expect fun openReadOnlyRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB

/**
 * The factory constructor of RocksDB that opens a RocksDB instance in
 * Read-Only mode given the path to the database using the specified
 * options and db path.
 *
 * Options instance *should* not be disposed before all DBs using this options
 * instance have been closed. If user doesn't call options dispose explicitly,
 * then this options instance will be GC'd automatically.
 *
 * @param options [Options] instance.
 * @param path the path to the RocksDB.
 * @return a [RocksDB] instance on success, null if the specified
 * [RocksDB] can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 */
expect fun openReadOnlyRocksDB(options: Options, path: String): RocksDB

/**
 * The factory constructor of RocksDB that opens a RocksDB instance in
 * Read-Only mode given the path to the database using the specified
 * options and db path.
 *
 *
 * This open method allows to open RocksDB using a subset of available
 * column families
 *
 * Options instance *should* not be disposed before all DBs using this
 * options instance have been closed. If user doesn't call options dispose
 * explicitly,then this options instance will be GC'd automatically.
 *
 * @param options [DBOptions] instance.
 * @param path the path to the RocksDB.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 * on open.
 * @return a [RocksDB] instance on success, null if the specified
 * [RocksDB] can not be opened.
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 */
expect fun openReadOnlyRocksDB(
    options: DBOptions, path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB
