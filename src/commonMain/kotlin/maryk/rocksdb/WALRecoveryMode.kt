package maryk.rocksdb

expect enum class WALRecoveryMode {
    /**
     * Original levelDB recovery
     *
     * We tolerate incomplete record in trailing data on all logs
     * Use case : This is legacy behavior (default)
     */
    TolerateCorruptedTailRecords,

    /**
     * Recover from clean shutdown
     *
     * We don't expect to find any corruption in the WAL
     * Use case : This is ideal for unit tests and rare applications that
     * can require high consistency guarantee
     */
    AbsoluteConsistency,

    /**
     * Recover to point-in-time consistency
     * We stop the WAL playback on discovering WAL inconsistency
     * Use case : Ideal for systems that have disk controller cache like
     * hard disk, SSD without super capacitor that store related data
     */
    PointInTimeRecovery,

    /**
     * Recovery after a disaster
     * We ignore any corruption in the WAL and try to salvage as much data as
     * possible
     * Use case : Ideal for last ditch effort to recover data or systems that
     * operate with low grade unrelated data
     */
    SkipAnyCorruptedRecords;

    /**
     * Returns the byte value of the enumerations value.
     *
     * @return byte representation
     */
    fun getValue(): Byte
}

expect fun getWALRecoveryMode(identifier: Byte): WALRecoveryMode
