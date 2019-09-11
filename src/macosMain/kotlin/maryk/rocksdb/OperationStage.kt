package maryk.rocksdb

actual enum class OperationStage(
    private val value: Byte
) {
    STAGE_UNKNOWN(0x0),
    STAGE_FLUSH_RUN(0x1),
    STAGE_FLUSH_WRITE_L0(0x2),
    STAGE_COMPACTION_PREPARE(0x3),
    STAGE_COMPACTION_RUN(0x4),
    STAGE_COMPACTION_PROCESS_KV(0x5),
    STAGE_COMPACTION_INSTALL(0x6),
    STAGE_COMPACTION_SYNC_FILE(0x7),
    STAGE_PICK_MEMTABLES_TO_FLUSH(0x8),
    STAGE_MEMTABLE_ROLLBACK(0x9),
    STAGE_MEMTABLE_INSTALL_FLUSH_RESULTS(0xA)
}

actual fun getOperationStageName(operationStage: OperationStage): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
