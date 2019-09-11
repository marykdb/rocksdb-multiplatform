package maryk.rocksdb

actual interface WalFilter {
    actual fun columnFamilyLogNumberMap(
        cfLognumber: Map<Int, Long>,
        cfNameId: Map<String, Int>
    )

    actual fun logRecordFound(
        logNumber: Long,
        logFileName: String,
        batch: WriteBatch,
        newBatch: WriteBatch
    ): LogRecordFoundResult

    actual fun name(): String
}

actual class LogRecordFoundResult {
    actual constructor(walProcessingOption: WalProcessingOption, batchChanged: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
