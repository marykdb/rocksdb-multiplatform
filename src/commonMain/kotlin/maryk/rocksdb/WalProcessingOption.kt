package maryk.rocksdb

expect enum class WalProcessingOption {
    /** Continue processing as usual. */
    CONTINUE_PROCESSING,

    /** Ignore the current record but continue processing of log(s). */
    IGNORE_CURRENT_RECORD,

    /**
     * Stop replay of logs and discard logs.
     * Logs won't be replayed on subsequent recovery.
     */
    STOP_REPLAY,

    /** Corrupted record detected by filter. */
    CORRUPTED_RECORD;
}
