package maryk.rocksdb

/**
 * Database with TTL support.
 *
 * **Use case**
 *
 * This API should be used to open the db when key-values inserted are
 * meant to be removed from the db in a non-strict 'ttl' amount of time
 * Therefore, this guarantees that key-values inserted will remain in the
 * db for &gt;= ttl amount of time and the db will make efforts to remove the
 * key-values as soon as possible after ttl seconds of their insertion.
 *
 * **Behaviour**
 *
 * TTL is accepted in seconds
 * (int32_t)Timestamp(creation) is suffixed to values in Put internally
 * Expired TTL values deleted in compaction only:(Timestamp+ttl&lt;time_now)
 * Get/Iterator may return expired entries(compaction not run on them yet)
 * Different TTL may be used during different Opens
 *
 * **Example**
 *
 *  * Open1 at t=0 with ttl=4 and insert k1,k2, close at t=2
 *  * Open2 at t=3 with ttl=5. Now k1,k2 should be deleted at t&gt;=5
 *
 * read_only=true opens in the usual read-only mode. Compactions will not be
 * triggered(neither manual nor automatic), so no expired entries removed
 *
 * **Constraints**
 *
 * Not specifying/passing or non-positive TTL behaves
 * like TTL = infinity
 *
 *
 * **!!!WARNING!!!**
 *
 * Calling DB::Open directly to re-open a db created by this API will get
 * corrupt values(timestamp suffixed) and no ttl effect will be there
 * during the second Open, so use this API consistently to open the db
 * Be careful when passing ttl with a small positive value because the
 * whole database may be deleted in a small amount of time.
 */
expect class TtlDB : RocksDB {
    /**
     * Creates a new ttl based column family with a name defined
     * in given ColumnFamilyDescriptor and allocates a
     * ColumnFamilyHandle within an internal structure.
     *
     * The ColumnFamilyHandle is automatically disposed with DB
     * disposal.
     *
     * @param columnFamilyDescriptor column family to be created.
     * @param ttl TTL to set for this column family.
     *
     * @return [ColumnFamilyHandle] instance.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun createColumnFamilyWithTtl(
        columnFamilyDescriptor: ColumnFamilyDescriptor,
        ttl: Int
    ): ColumnFamilyHandle
}

/**
 * Opens a TtlDB.
 *
 * Database is opened in read-write mode without default TTL.
 *
 * @param options [Options] instance.
 * @param db_path path to database.
 *
 * @return TtlDB instance.
 *
 * @throws RocksDBException thrown if an error occurs within the native
 * part of the library.
 */
expect fun openTtlDB(options: Options, db_path: String): TtlDB

/**
 * Opens a TtlDB.
 *
 * @param options [Options] instance.
 * @param db_path path to database.
 * @param ttl time to live for new entries.
 * @param readOnly boolean value indicating if database if db is
 * opened read-only.
 *
 * @return TtlDB instance.
 *
 * @throws RocksDBException thrown if an error occurs within the native
 * part of the library.
 */
expect fun openTtlDB(
    options: Options, db_path: String,
    ttl: Int, readOnly: Boolean
): TtlDB

/**
 * Opens a TtlDB.
 *
 * @param options [Options] instance.
 * @param db_path path to database.
 * @param columnFamilyDescriptors list of column family descriptors
 * @param columnFamilyHandles will be filled with ColumnFamilyHandle instances
 * on open.
 * @param ttlValues time to live values per column family handle
 * @param readOnly boolean value indicating if database if db is
 * opened read-only.
 *
 * @return TtlDB instance.
 *
 * @throws RocksDBException thrown if an error occurs within the native
 * part of the library.
 * @throws IllegalArgumentException when there is not a ttl value
 * per given column family handle.
 */
expect fun openTtlDB(
    options: DBOptions, db_path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>,
    ttlValues: List<Int>, readOnly: Boolean
): TtlDB
