package maryk.rocksdb

actual typealias GetStatus = org.rocksdb.GetStatus

/**
 * The status of the fetch operation.
 */
actual fun GetStatus.getStatus(): Status {
    return this.status
}

/**
 * The size of the data fetched, which may be bigger than the buffer.
 */
actual fun GetStatus.getRequiredSize(): Int {
    return this.requiredSize
}
