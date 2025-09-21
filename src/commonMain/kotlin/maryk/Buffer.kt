package maryk

expect sealed class Buffer {
    abstract fun array(): Any
    fun remaining(): Int
    fun position(): Int
}
