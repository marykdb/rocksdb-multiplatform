package maryk.rocksdb

actual typealias OperationType = org.rocksdb.OperationType

actual fun getOperationName(operationType: OperationType) =
    ThreadStatus.getOperationName(operationType)

actual fun getOperationPropertyName(operationType: OperationType, i: Int) =
    ThreadStatus.getOperationPropertyName(operationType, i)

actual fun interpretOperationProperties(
    operationType: OperationType,
    operationProperties: LongArray
): Map<String, Long> =
    ThreadStatus.interpretOperationProperties(operationType, operationProperties)
