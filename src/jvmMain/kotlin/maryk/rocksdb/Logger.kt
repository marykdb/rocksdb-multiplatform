package maryk.rocksdb

actual typealias Logger = org.rocksdb.Logger

actual fun createLogger(logLevel: InfoLogLevel, callback: (InfoLogLevel, String) -> Unit): Logger =
    object : Logger(logLevel) {
        override fun log(logLevel: InfoLogLevel, logMsg: String) {
            try {
                callback(logLevel, logMsg)
            } catch (_: Throwable) {
                // Exceptions must not cross the JNI callback boundary.
            }
        }
    }
