package maryk

actual abstract class Buffer(
    protected var capacity: Int,
    protected var limit: Int,
    protected var position: Int = 0
) {

    actual abstract fun array(): Any

    actual open fun flip(): Buffer {
        limit = position
        position = 0
        return this
    }

    actual fun remaining() = limit - position

    actual fun limit(newLimit: Int): Buffer {
        if (newLimit > capacity || newLimit < 0) throw IllegalArgumentException()
        limit = newLimit
        if (position > limit) position = limit
        return this
    }
}
