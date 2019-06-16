package maryk.rocksdb

/**
 * WALFilter allows an application to inspect write-ahead-log (WAL)
 * records or modify their processing on recovery.
 */
expect interface WalFilter {
    /**
     * Provide ColumnFamily-&gt;LogNumber map to filter
     * so that filter can determine whether a log number applies to a given
     * column family (i.e. that log hasn't been flushed to SST already for the
     * column family).
     *
     * We also pass in name&gt;id map as only name is known during
     * recovery (as handles are opened post-recovery).
     * while write batch callbacks happen in terms of column family id.
     *
     * @param cfLognumber column_family_id to lognumber map
     * @param cfNameId column_family_name to column_family_id map
     */
    fun columnFamilyLogNumberMap(
        cfLognumber: Map<Int, Long>,
        cfNameId: Map<String, Int>
    )

    /**
     * LogRecord is invoked for each log record encountered for all the logs
     * during replay on logs on recovery. This method can be used to:
     * * inspect the record (using the batch parameter)
     * * ignoring current record
     * (by returning WalProcessingOption::kIgnoreCurrentRecord)
     * * reporting corrupted record
     * (by returning WalProcessingOption::kCorruptedRecord)
     * * stop log replay
     * (by returning kStop replay) - please note that this implies
     * discarding the logs from current record onwards.
     *
     * @param logNumber log number of the current log.
     * Filter might use this to determine if the log
     * record is applicable to a certain column family.
     * @param logFileName log file name - only for informational purposes
     * @param batch batch encountered in the log during recovery
     * @param newBatch new batch to populate if filter wants to change
     * the batch (for example to filter some records out, or alter some
     * records). Please note that the new batch MUST NOT contain
     * more records than original, else recovery would be failed.
     *
     * @return Processing option for the current record.
     */
    fun logRecordFound(
        logNumber: Long,
        logFileName: String, batch: WriteBatch,
        newBatch: WriteBatch
    ): LogRecordFoundResult

    /**
     * Returns a name that identifies this WAL filter.
     * The name will be printed to LOG file on start up for diagnosis.
     *
     * @return the name
     */
    fun name(): String
}

expect class LogRecordFoundResult {
    /**
     * @param walProcessingOption the processing option
     * @param batchChanged Whether batch was changed by the filter.
     * It must be set to true if newBatch was populated,
     * else newBatch has no effect.
     */
    constructor(
        walProcessingOption: WalProcessingOption,
        batchChanged: Boolean
    )
}
