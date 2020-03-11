package maryk

expect abstract class Buffer {
    abstract fun array(): Any
    fun flip(): Buffer
    fun limit(newLimit: Int): Buffer
    fun remaining(): Int
}
