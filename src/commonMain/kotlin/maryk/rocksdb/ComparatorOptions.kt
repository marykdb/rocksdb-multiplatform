package maryk.rocksdb


/**
 * This class controls the behaviour
 * of Java implementations of
 * AbstractComparator
 *
 * Note that dispose() must be called before a ComparatorOptions
 * instance becomes out-of-scope to release the allocated memory in C++.
 */
expect class ComparatorOptions() : RocksObject {
    /**
     * Use adaptive mutex, which spins in the user space before resorting
     * to kernel. This could reduce context switch when the mutex is not
     * heavily contended. However, if the mutex is hot, we could end up
     * wasting spin time.
     * Default: false
     *
     * @return true if adaptive mutex is used.
     */
    fun useAdaptiveMutex(): Boolean

    /**
     * Use adaptive mutex, which spins in the user space before resorting
     * to kernel. This could reduce context switch when the mutex is not
     * heavily contended. However, if the mutex is hot, we could end up
     * wasting spin time.
     * Default: false
     *
     * @param useAdaptiveMutex true if adaptive mutex is used.
     * @return the reference to the current comparator options.
     */
    fun setUseAdaptiveMutex(useAdaptiveMutex: Boolean): ComparatorOptions
}
