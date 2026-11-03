@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package maryk.rocksdb

actual fun MutableDBOptions.close() { this.close() }
actual fun MutableDBOptionsBuilder.close() { this.close() }
actual fun MutableColumnFamilyOptions.close() { this.close() }
actual fun MutableColumnFamilyOptionsBuilder.close() { this.close() }
