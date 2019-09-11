package maryk

actual abstract class EnumSet<E : Enum<E>> : Iterable<E> {
    val size: Int = -1
}
