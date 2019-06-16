package maryk.rocksdb

/**
 * The type used to refer to a thread operation.
 *
 * A thread operation describes high-level action of a thread,
 * examples include compaction and flush.
 */
expect enum class OperationType {
    OP_UNKNOWN,
    OP_COMPACTION,
    OP_FLUSH
}

/**
 * Get the name of an operation given its type.
 *
 * @param operationType the type of operation.
 *
 * @return the name of the operation.
 */
expect fun getOperationName(operationType: OperationType): String

/**
 * Obtain the name of the "i"th operation property of the
 * specified operation.
 *
 * @param operationType the operation type.
 * @param i the index of the operation property.
 *
 * @return the name of the operation property
 */
expect fun getOperationPropertyName(
    operationType: OperationType, i: Int
): String

/**
 * Translate the "i"th property of the specified operation given
 * a property value.
 *
 * @param operationType the operation type.
 * @param operationProperties the operation properties.
 *
 * @return the property values.
 */
expect fun interpretOperationProperties(
    operationType: OperationType, operationProperties: LongArray
): Map<String, Long>
