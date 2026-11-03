package maryk.rocksdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set
import maryk.wrapWithErrorThrower
import rocksdb.rocksdb_set_db_options
import rocksdb.rocksdb_set_options
import rocksdb.rocksdb_set_options_cf

internal fun RocksDB.applyMutableDBOptions(options: MutableDBOptions) = withLifecycleLock {
    checkOwningHandle()
    withLiveOptionMap(options.valuesForApply()) { keys, values, count ->
        wrapWithErrorThrower { error -> rocksdb_set_db_options(native, count, keys, values, error) }
    }
}

internal fun RocksDB.applyMutableColumnFamilyOptions(columnFamily: ColumnFamilyHandle?, options: MutableColumnFamilyOptions) = withLifecycleLock {
    checkOwningHandle()
    columnFamily?.checkOwningHandle()
    withLiveOptionMap(options.valuesForApply()) { keys, values, count ->
        wrapWithErrorThrower { error ->
            if (columnFamily == null) rocksdb_set_options(native, count, keys, values, error)
            else rocksdb_set_options_cf(native, columnFamily.native, count, keys, values, error)
        }
    }
}

private inline fun <R> withLiveOptionMap(
    values: Map<String, String>,
    block: (CPointer<CPointerVar<ByteVar>>, CPointer<CPointerVar<ByteVar>>, Int) -> R,
): R = memScoped {
    val nativeKeys = allocArray<CPointerVar<ByteVar>>(maxOf(1, values.size))
    val nativeValues = allocArray<CPointerVar<ByteVar>>(maxOf(1, values.size))
    values.entries.forEachIndexed { index, entry ->
        require('\u0000' !in entry.key && '\u0000' !in entry.value) { "Option keys and values must not contain NUL" }
        nativeKeys[index] = entry.key.cstr.getPointer(this)
        nativeValues[index] = entry.value.cstr.getPointer(this)
    }
    block(nativeKeys, nativeValues, values.size)
}

/** Split the native-validated RocksDB option grammar without expanding untouched defaults. */
internal fun parsedLiveOptionValues(
    source: String,
    ignoreUnknown: Boolean,
    validate: (String) -> Unit,
): Map<String, String> {
    var input = source.trimOptionWhitespace()
    while (input.length > 2 && input.first() == '{' && input.last() == '}') {
        input = input.substring(1, input.lastIndex).trimOptionWhitespace()
    }
    val values = linkedMapOf<String, String>()
    var position = 0
    while (position < input.length) {
        val equals = input.indexOfAny(charArrayOf('=', '{', '}', ';'), position)
        require(equals >= 0 && input[equals] == '=') { "Expected an option key/value pair" }
        val key = input.substring(position, equals).trimOptionWhitespace()
        require(key.isNotEmpty()) { "Empty option key" }
        var start = equals + 1
        while (start < input.length && input[start].isOptionWhitespace()) start++
        val value: String
        if (start < input.length && input[start] == '{') {
            var depth = 1
            var end = start + 1
            while (end < input.length && depth > 0) {
                when (input[end]) { '{' -> depth++; '}' -> depth-- }
                end++
            }
            require(depth == 0) { "Unbalanced option braces" }
            value = input.substring(start, end)
            position = end
            while (position < input.length && input[position].isOptionWhitespace()) position++
            require(position == input.length || input[position] == ';') { "Unexpected data after option value" }
        } else {
            position = input.indexOf(';', start).let { if (it == -1) input.length else it }
            value = input.substring(start, position).trimOptionWhitespace()
        }
        val supported = if (ignoreUnknown) {
            try {
                validate("$key=$value")
                true
            } catch (_: RocksDBException) {
                false
            }
        } else true
        if (supported) values[key] = value
        position++
    }
    return values
}

private fun Char.isOptionWhitespace(): Boolean = this == ' ' || this in '\t'..'\r'
private fun String.trimOptionWhitespace(): String = trim { it.isOptionWhitespace() }

internal fun compressionOptionValue(value: CompressionType): String = when (value) {
    CompressionType.NO_COMPRESSION -> "kNoCompression"
    CompressionType.SNAPPY_COMPRESSION -> "kSnappyCompression"
    CompressionType.ZLIB_COMPRESSION -> "kZlibCompression"
    CompressionType.BZLIB2_COMPRESSION -> "kBZip2Compression"
    CompressionType.LZ4_COMPRESSION -> "kLZ4Compression"
    CompressionType.LZ4HC_COMPRESSION -> "kLZ4HCCompression"
    CompressionType.XPRESS_COMPRESSION -> "kXpressCompression"
    CompressionType.ZSTD_COMPRESSION -> "kZSTD"
    CompressionType.DISABLE_COMPRESSION_OPTION -> "kDisableCompressionOption"
}
