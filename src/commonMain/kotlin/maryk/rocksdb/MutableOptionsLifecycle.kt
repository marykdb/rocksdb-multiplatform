package maryk.rocksdb

/** Release native storage owned by this immutable options snapshot. JVM implementation is a no-op. Do not reuse after closing. */
expect fun MutableDBOptions.close()
/** Release native storage owned by this builder. JVM implementation is a no-op. Do not reuse after closing. */
expect fun MutableDBOptionsBuilder.close()
/** Release native storage owned by this immutable options snapshot. JVM implementation is a no-op. Do not reuse after closing. */
expect fun MutableColumnFamilyOptions.close()
/** Release native storage owned by this builder. JVM implementation is a no-op. Do not reuse after closing. */
expect fun MutableColumnFamilyOptionsBuilder.close()

/** Run [block] and release the options snapshot, preserving a primary block exception. */
inline fun <R> MutableDBOptions.use(block: (MutableDBOptions) -> R): R =
    useMutableOptions(this, { it.close() }, block)
/** Run [block] and release the builder, preserving a primary block exception. */
inline fun <R> MutableDBOptionsBuilder.use(block: (MutableDBOptionsBuilder) -> R): R =
    useMutableOptions(this, { it.close() }, block)
/** Run [block] and release the options snapshot, preserving a primary block exception. */
inline fun <R> MutableColumnFamilyOptions.use(block: (MutableColumnFamilyOptions) -> R): R =
    useMutableOptions(this, { it.close() }, block)
/** Run [block] and release the builder, preserving a primary block exception. */
inline fun <R> MutableColumnFamilyOptionsBuilder.use(block: (MutableColumnFamilyOptionsBuilder) -> R): R =
    useMutableOptions(this, { it.close() }, block)

@PublishedApi
internal inline fun <T, R> useMutableOptions(value: T, close: (T) -> Unit, block: (T) -> R): R {
    var failure: Throwable? = null
    try {
        return block(value)
    } catch (error: Throwable) {
        failure = error
        throw error
    } finally {
        try {
            close(value)
        } catch (closeError: Throwable) {
            if (failure == null) throw closeError
            if (failure !== closeError) failure.addSuppressed(closeError)
        }
    }
}
