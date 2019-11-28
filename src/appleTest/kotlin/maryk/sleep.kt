package maryk

import platform.posix.sleep

actual fun sleep(millis: Long) {
    sleep(millis.toUInt())
}
