package maryk

import kotlin.text.decodeToString as decodeToStringKt

actual fun ByteArray.decodeToString() = this.decodeToStringKt()
