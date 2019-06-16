package maryk.rocksdb

expect class ThreadStatus {
    /** Get the unique ID of the thread. */
    fun getThreadId(): Long

    /** Get the type of the thread. */
    fun getThreadType(): ThreadType

    /**
     * The name of the DB instance that the thread is currently
     * involved with.
     *
     * @return the name of the db, or null if the thread is not involved
     * in any DB operation.
     */
    fun getDbName(): String?

    /**
     * The name of the Column Family that the thread is currently
     * involved with.
     *
     * @return the name of the db, or null if the thread is not involved
     * in any column Family operation.
     */
    fun getCfName(): String?

    /**
     * Get the operation (high-level action) that the current thread is involved
     * with.
     */
    fun getOperationType(): OperationType

    /**
     * Get the elapsed time of the current thread operation in microseconds.
     */
    fun getOperationElapsedTime(): Long

    /**
     * Get the current stage where the thread is involved in the current
     * operation.
     *
     * @return the current stage of the current operation
     */
    fun getOperationStage(): OperationStage

    /**
     * Get the list of properties that describe some details about the current
     * operation.
     *
     * Each field in might have different meanings for different operations.
     *
     * @return the properties
     */
    fun getOperationProperties(): LongArray

    /**
     * Get the state (lower-level action) that the current thread is involved
     * with.
     *
     * @return the state
     */
    fun getStateType(): StateType
}

expect fun microsToString(operationElapsedTime: Long): String
