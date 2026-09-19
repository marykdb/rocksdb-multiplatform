// Add the following to GetStatus.kt

package maryk.rocksdb
/**
 * The result of a fetch and the total size of the object fetched.
 * If the target of the fetch is not big enough, this may be bigger than the contents of the target.
 */
expect class GetStatus

/**
 * The status of the fetch operation.
 */
expect fun GetStatus.getStatus(): Status

/**
 * The size of the data fetched, which may be bigger than the buffer.
 */
expect fun GetStatus.getRequiredSize(): Int
