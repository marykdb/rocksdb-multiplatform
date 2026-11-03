package maryk.rocksdb

import rocksdb.rocksdb_options_set_compression_options_handle
import rocksdb.rocksdb_options_set_bottommost_compression_options_handle
import rocksdb.rocksdb_options_get_compression_options_handle
import rocksdb.rocksdb_options_get_bottommost_compression_options_handle

actual fun Options.configureCompressionOptions(compressionOptions: CompressionOptions): Options {
    checkOwningHandle()
    compressionOptions.checkOwningHandle()
    rocksdb_options_set_compression_options_handle(native, compressionOptions.native)
    hasConfiguredCompressionOptions = true
    return this
}

actual fun Options.copyConfiguredCompressionOptions(): CompressionOptions? {
    checkOwningHandle()
    if (!hasConfiguredCompressionOptions) return null
    return CompressionOptions(requireNotNull(rocksdb_options_get_compression_options_handle(native)) {
        "Unable to copy compression options"
    })
}

actual fun Options.configureBottommostCompressionOptions(compressionOptions: CompressionOptions): Options {
    checkOwningHandle()
    compressionOptions.checkOwningHandle()
    rocksdb_options_set_bottommost_compression_options_handle(native, compressionOptions.native)
    hasConfiguredBottommostCompressionOptions = true
    return this
}

actual fun Options.copyConfiguredBottommostCompressionOptions(): CompressionOptions? {
    checkOwningHandle()
    if (!hasConfiguredBottommostCompressionOptions) return null
    return CompressionOptions(requireNotNull(rocksdb_options_get_bottommost_compression_options_handle(native)) {
        "Unable to copy compression options"
    })
}

actual fun ColumnFamilyOptions.configureCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions {
    checkOwningHandle()
    compressionOptions.checkOwningHandle()
    rocksdb_options_set_compression_options_handle(native, compressionOptions.native)
    hasConfiguredCompressionOptions = true
    return this
}

actual fun ColumnFamilyOptions.copyConfiguredCompressionOptions(): CompressionOptions? {
    checkOwningHandle()
    if (!hasConfiguredCompressionOptions) return null
    return CompressionOptions(requireNotNull(rocksdb_options_get_compression_options_handle(native)) {
        "Unable to copy compression options"
    })
}

actual fun ColumnFamilyOptions.configureBottommostCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions {
    checkOwningHandle()
    compressionOptions.checkOwningHandle()
    rocksdb_options_set_bottommost_compression_options_handle(native, compressionOptions.native)
    hasConfiguredBottommostCompressionOptions = true
    return this
}

actual fun ColumnFamilyOptions.copyConfiguredBottommostCompressionOptions(): CompressionOptions? {
    checkOwningHandle()
    if (!hasConfiguredBottommostCompressionOptions) return null
    return CompressionOptions(requireNotNull(rocksdb_options_get_bottommost_compression_options_handle(native)) {
        "Unable to copy compression options"
    })
}
