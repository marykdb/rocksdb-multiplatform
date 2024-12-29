package maryk

import maryk.rocksdb.Status
import maryk.rocksdb.getStatusCode
import maryk.rocksdb.getStatusSubCode

fun convertToStatus(error: String): Status? {
    val regex = Regex("""^\[(\d+)\|(\d+)]\s*(.+)$""")
    val matchResult = regex.find(error)

    return if (matchResult != null) {
        val (code, subcode, message) = matchResult.destructured
        Status(getStatusCode(code.toByte()), getStatusSubCode(subcode.toByte()), message)
    } else {
        null
    }
}
