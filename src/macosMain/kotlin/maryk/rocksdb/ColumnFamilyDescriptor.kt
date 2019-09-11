package maryk.rocksdb

actual class ColumnFamilyDescriptor actual constructor(
    private val columnFamilyName: ByteArray,
    private val columnFamilyOptions: ColumnFamilyOptions
) {
    actual constructor(columnFamilyName: ByteArray) : this(columnFamilyName,ColumnFamilyOptions())

    actual fun getName() = columnFamilyName

    actual fun getOptions() = columnFamilyOptions
}
