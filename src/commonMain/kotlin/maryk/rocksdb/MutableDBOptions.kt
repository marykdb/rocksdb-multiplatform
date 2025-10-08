package maryk.rocksdb

/** Mutable DB options entry point. */
expect class MutableDBOptions : AbstractMutableOptions

expect fun mutableDBOptionsBuilder(): MutableDBOptionsBuilder

expect fun parseMutableDBOptions(
    str: String,
    ignoreUnknown: Boolean = false
): MutableDBOptionsBuilder

expect class MutableDBOptionsBuilder : MutableDBOptionsInterface<MutableDBOptionsBuilder> {
    fun build(): MutableDBOptions
}
