package maryk

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
actual annotation class WindowsIgnore actual constructor(actual val reason: String = "")
