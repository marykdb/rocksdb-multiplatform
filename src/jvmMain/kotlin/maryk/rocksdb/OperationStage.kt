package maryk.rocksdb

actual typealias OperationStage = org.rocksdb.OperationStage

actual fun getOperationStageName(operationStage: OperationStage) =
    ThreadStatus.getOperationStageName(operationStage)
