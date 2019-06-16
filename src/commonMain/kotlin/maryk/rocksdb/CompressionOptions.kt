package maryk.rocksdb

/** Options for Compression */
expect class CompressionOptions() : RocksObject {

    fun setWindowBits(windowBits: Int): CompressionOptions

    fun windowBits(): Int

    fun setLevel(level: Int): CompressionOptions

    fun level(): Int

    fun setStrategy(strategy: Int): CompressionOptions

    fun strategy(): Int

    /**
     * Maximum size of dictionary used to prime the compression library. Currently
     * this dictionary will be constructed by sampling the first output file in a
     * subcompaction when the target level is bottommost. This dictionary will be
     * loaded into the compression library before compressing/uncompressing each
     * data block of subsequent files in the subcompaction. Effectively, this
     * improves compression ratios when there are repetitions across data blocks.
     *
     * A value of 0 indicates the feature is disabled.
     *
     * Default: 0.
     *
     * @param maxDictBytes Maximum bytes to use for the dictionary
     *
     * @return the reference to the current options
     */
    fun setMaxDictBytes(maxDictBytes: Int): CompressionOptions

    /**
     * Maximum size of dictionary used to prime the compression library.
     *
     * @return The maximum bytes to use for the dictionary
     */
    fun maxDictBytes(): Int

    /**
     * Maximum size of training data passed to zstd's dictionary trainer. Using
     * zstd's dictionary trainer can achieve even better compression ratio
     * improvements than using [.setMaxDictBytes] alone.
     *
     * The training data will be used to generate a dictionary
     * of [.maxDictBytes].
     *
     * Default: 0.
     *
     * @param zstdMaxTrainBytes Maximum bytes to use for training ZStd.
     *
     * @return the reference to the current options
     */
    fun setZStdMaxTrainBytes(zstdMaxTrainBytes: Int): CompressionOptions

    /**
     * Maximum size of training data passed to zstd's dictionary trainer.
     * @return Maximum bytes to use for training ZStd
     */
    fun zstdMaxTrainBytes(): Int

    /**
     * When the compression options are set by the user, it will be set to "true".
     * For bottommost_compression_opts, to enable it, user must set enabled=true.
     * Otherwise, bottommost compression will use compression_opts as default
     * compression options.
     *
     * For compression_opts, if compression_opts.enabled=false, it is still
     * used as compression options for compression process.
     *
     * Default: false.
     *
     * @param enabled true to use these compression options
     * for the bottommost_compression_opts, false otherwise
     *
     * @return the reference to the current options
     */
    fun setEnabled(enabled: Boolean): CompressionOptions

    /**
     * Determine whether these compression options
     * are used for the bottommost_compression_opts.
     *
     * @return true if these compression options are used
     * for the bottommost_compression_opts, false otherwise
     */
    fun enabled(): Boolean
}
