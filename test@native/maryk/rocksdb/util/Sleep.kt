package maryk.rocksdb.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

actual fun sleepMillis(millis: Long) {
    if (millis <= 0L) {
        return
    }
    val timeout: Duration = millis.milliseconds
    val timer = TimeSource.Monotonic.markNow()
    while (timer.elapsedNow() < timeout) {
        // Busy wait placeholder for native sleep implementation.
    }
}
