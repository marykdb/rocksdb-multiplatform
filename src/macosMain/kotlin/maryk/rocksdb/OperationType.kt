package maryk.rocksdb

actual enum class OperationType(
    private val value: Byte
) {
    OP_UNKNOWN(0x0),
    OP_COMPACTION(0x1),
    OP_FLUSH(0x2)
}

actual fun getOperationName(operationType: OperationType): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun getOperationPropertyName(operationType: OperationType, i: Int): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun interpretOperationProperties(
    operationType: OperationType,
    operationProperties: LongArray
): Map<String, Long> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
