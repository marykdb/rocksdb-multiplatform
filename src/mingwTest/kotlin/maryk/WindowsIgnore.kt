package maryk

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.test.Ignore

@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
@Ignore
actual annotation class WindowsIgnore actual constructor(actual val reason: String = "")
