package maryk.rocksdb

/** Copy these settings into the options; the source handle may be closed afterwards. */
expect fun Options.configureCompressionOptions(compressionOptions: CompressionOptions): Options

/** Return an owned copy of settings attached through configureCompressionOptions/configureBottommostCompressionOptions, or null if none were attached. Close the returned copy. */
expect fun Options.copyConfiguredCompressionOptions(): CompressionOptions?

/** Copy these settings into the options; the source handle may be closed afterwards. */
expect fun Options.configureBottommostCompressionOptions(compressionOptions: CompressionOptions): Options

/** Return an owned copy of settings attached through configureCompressionOptions/configureBottommostCompressionOptions, or null if none were attached. Close the returned copy. */
expect fun Options.copyConfiguredBottommostCompressionOptions(): CompressionOptions?

/** Copy these settings into the options; the source handle may be closed afterwards. */
expect fun ColumnFamilyOptions.configureCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions

/** Return an owned copy of settings attached through configureCompressionOptions/configureBottommostCompressionOptions, or null if none were attached. Close the returned copy. */
expect fun ColumnFamilyOptions.copyConfiguredCompressionOptions(): CompressionOptions?

/** Copy these settings into the options; the source handle may be closed afterwards. */
expect fun ColumnFamilyOptions.configureBottommostCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions

/** Return an owned copy of settings attached through configureCompressionOptions/configureBottommostCompressionOptions, or null if none were attached. Close the returned copy. */
expect fun ColumnFamilyOptions.copyConfiguredBottommostCompressionOptions(): CompressionOptions?
