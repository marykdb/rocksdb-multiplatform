package maryk

actual abstract class Buffer(
    protected var capacity: Int
) {
    protected var position: Int = 0

    actual abstract fun array(): Any

    actual open fun flip(): Buffer {
        capacity = position
        position = 0
        return this
    }

    actual fun remaining() = capacity - position
}
