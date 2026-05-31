package maryk

actual abstract class Buffer(
    internal var capacity: Int,
    internal var limit: Int,
    internal var position: Int = 0
) {
    init {
        require(capacity >= 0) { "capacity must be non-negative" }
        require(limit in 0..capacity) { "limit must be between 0 and capacity" }
        require(position in 0..limit) { "position must be between 0 and limit" }
    }

    actual abstract fun array(): Any

    open fun flip(): Buffer {
        limit = position
        position = 0
        return this
    }

    actual fun position(): Int = position

    actual fun remaining() = limit - position

    fun limit(newLimit: Int): Buffer {
        if (newLimit > capacity || newLimit < 0) throw IllegalArgumentException()
        limit = newLimit
        if (position > limit) position = limit
        return this
    }
}
