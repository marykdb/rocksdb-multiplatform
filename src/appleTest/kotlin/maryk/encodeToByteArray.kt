package maryk

import kotlin.text.encodeToByteArray as encodeToByteArrayKt

actual fun String.encodeToByteArray() = this.encodeToByteArrayKt()
