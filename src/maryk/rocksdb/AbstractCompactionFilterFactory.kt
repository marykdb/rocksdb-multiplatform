package maryk.rocksdb

/**
 * Each compaction will create a new [AbstractCompactionFilter]
 * allowing the application to know about different compactions
 *
 * @param <T> The concrete type of the compaction filter
 */
expect abstract class AbstractCompactionFilterFactory<T : AbstractCompactionFilter<*>>() : RocksCallbackObject {
    /**
     * A name which identifies this compaction filter
     *
     * The name will be printed to the LOG file on start up for diagnosis
     *
     * @return name which identifies this compaction filter.
     */
    abstract fun name(): String

    /**
     * Create a new compaction filter
     *
     * @param context The context describing the need for a new compaction filter
     *
     * @return A new instance of [AbstractCompactionFilter]
     */
    abstract fun createCompactionFilter(
        context: AbstractCompactionFilterContext
    ): T
}
