package maryk.rocksdb

/** Reject paths that C-string conversion would truncate or replace. */
internal fun String.checkedNativePath(): String {
    require('\u0000' !in this) { "Filesystem path contains NUL" }
    var index = 0
    while (index < length) {
        val char = this[index++]
        if (char in '\uD800'..'\uDBFF') {
            require(index < length && this[index] in '\uDC00'..'\uDFFF') {
                "Filesystem path contains an unpaired UTF-16 surrogate"
            }
            index++
        } else {
            require(char !in '\uDC00'..'\uDFFF') {
                "Filesystem path contains an unpaired UTF-16 surrogate"
            }
        }
    }
    return this
}

internal expect fun backupDirectoryAccess(path: String): Int
