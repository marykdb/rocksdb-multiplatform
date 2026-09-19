package maryk

expect abstract class Buffer {
    abstract fun array(): Any
    fun remaining(): Int
    fun position(): Int
}
