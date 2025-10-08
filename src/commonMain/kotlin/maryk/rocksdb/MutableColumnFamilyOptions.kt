package maryk.rocksdb

/** Mutable column family options entry point. */
expect class MutableColumnFamilyOptions : AbstractMutableOptions

expect fun mutableColumnFamilyOptionsBuilder(): MutableColumnFamilyOptionsBuilder

expect fun parseMutableColumnFamilyOptions(
    str: String,
    ignoreUnknown: Boolean = false
): MutableColumnFamilyOptionsBuilder

expect class MutableColumnFamilyOptionsBuilder :
    MutableColumnFamilyOptionsInterface<MutableColumnFamilyOptionsBuilder> {
    /** Builds the immutable options object. */
    fun build(): MutableColumnFamilyOptions
}
