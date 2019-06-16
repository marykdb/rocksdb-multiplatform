package maryk.rocksdb

/**
 * The operation stage.
 */
expect enum class OperationStage {
    STAGE_UNKNOWN,
    STAGE_FLUSH_RUN,
    STAGE_FLUSH_WRITE_L0,
    STAGE_COMPACTION_PREPARE,
    STAGE_COMPACTION_RUN,
    STAGE_COMPACTION_PROCESS_KV,
    STAGE_COMPACTION_INSTALL,
    STAGE_COMPACTION_SYNC_FILE,
    STAGE_PICK_MEMTABLES_TO_FLUSH,
    STAGE_MEMTABLE_ROLLBACK,
    STAGE_MEMTABLE_INSTALL_FLUSH_RESULTS
}

/**
 * Obtain a human-readable string describing the specified operation stage.
 *
 * @param operationStage the stage of the operation.
 *
 * @return the description of the operation stage.
 */
expect fun getOperationStageName(
    operationStage: OperationStage
): String
