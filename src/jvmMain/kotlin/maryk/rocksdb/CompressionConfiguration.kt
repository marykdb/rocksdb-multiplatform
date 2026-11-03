@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package maryk.rocksdb

import java.util.WeakHashMap

actual fun Options.configureCompressionOptions(compressionOptions: CompressionOptions): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    check(compressionOptions.isOwningHandle()) { "Compression options handle is closed" }
    val copy = compressionOptions.configuredSettings()
    this.setCompressionOptions(compressionOptions)
    synchronized(compressionSettings) { compressionSettings[this] = copy }
    return this
}

actual fun Options.copyConfiguredCompressionOptions(): CompressionOptions? {
    check(isOwningHandle()) { "Options handle is closed" }
    return synchronized(compressionSettings) { compressionSettings[this] }?.newHandle()
}

actual fun Options.configureBottommostCompressionOptions(compressionOptions: CompressionOptions): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    check(compressionOptions.isOwningHandle()) { "Compression options handle is closed" }
    val copy = compressionOptions.configuredSettings()
    this.setBottommostCompressionOptions(compressionOptions)
    synchronized(bottommostCompressionSettings) { bottommostCompressionSettings[this] = copy }
    return this
}

actual fun Options.copyConfiguredBottommostCompressionOptions(): CompressionOptions? {
    check(isOwningHandle()) { "Options handle is closed" }
    return synchronized(bottommostCompressionSettings) { bottommostCompressionSettings[this] }?.newHandle()
}

actual fun ColumnFamilyOptions.configureCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    check(compressionOptions.isOwningHandle()) { "Compression options handle is closed" }
    val copy = compressionOptions.configuredSettings()
    this.setCompressionOptions(compressionOptions)
    synchronized(compressionSettings) { compressionSettings[this] = copy }
    return this
}

actual fun ColumnFamilyOptions.copyConfiguredCompressionOptions(): CompressionOptions? {
    check(isOwningHandle()) { "Options handle is closed" }
    return synchronized(compressionSettings) { compressionSettings[this] }?.newHandle()
}

actual fun ColumnFamilyOptions.configureBottommostCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    check(compressionOptions.isOwningHandle()) { "Compression options handle is closed" }
    val copy = compressionOptions.configuredSettings()
    this.setBottommostCompressionOptions(compressionOptions)
    synchronized(bottommostCompressionSettings) { bottommostCompressionSettings[this] = copy }
    return this
}

actual fun ColumnFamilyOptions.copyConfiguredBottommostCompressionOptions(): CompressionOptions? {
    check(isOwningHandle()) { "Options handle is closed" }
    return synchronized(bottommostCompressionSettings) { bottommostCompressionSettings[this] }?.newHandle()
}

private val compressionSettings = WeakHashMap<Any, ConfiguredCompressionSettings>()
private val bottommostCompressionSettings = WeakHashMap<Any, ConfiguredCompressionSettings>()

private data class ConfiguredCompressionSettings(
    val windowBits: Int,
    val level: Int,
    val strategy: Int,
    val maxDictBytes: Int,
    val maxTrainBytes: Int,
    val enabled: Boolean,
) {
    fun newHandle(): CompressionOptions = CompressionOptions()
        .setWindowBits(windowBits).setLevel(level).setStrategy(strategy)
        .setMaxDictBytes(maxDictBytes).setZStdMaxTrainBytes(maxTrainBytes).setEnabled(enabled)
}

private fun CompressionOptions.configuredSettings(): ConfiguredCompressionSettings =
    ConfiguredCompressionSettings(windowBits(), level(), strategy(), maxDictBytes(), zstdMaxTrainBytes(), enabled())
