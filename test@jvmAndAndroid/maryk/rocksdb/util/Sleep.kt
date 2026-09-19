package maryk.rocksdb.util

actual fun sleepMillis(millis: Long) {
    if (millis <= 0L) {
        Thread.yield()
    } else {
        Thread.sleep(millis)
    }
}
