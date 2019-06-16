package maryk.rocksdb

/** Filter for iterating a table. */
expect interface TableFilter {
    /**
     * A callback to determine whether relevant keys for this scan exist in a
     * given table based on the table's properties. The callback is passed the
     * properties of each table during iteration. If the callback returns false,
     * the table will not be scanned. This option only affects Iterators and has
     * no impact on point lookups.
     *
     * @param tableProperties the table properties.
     *
     * @return true if the table should be scanned, false otherwise.
     */
    fun filter(tableProperties: TableProperties): Boolean
}
