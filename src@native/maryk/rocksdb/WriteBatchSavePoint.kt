package maryk.rocksdb

actual class WriteBatchSavePoint actual constructor(
    private var size: Long,
    private var count: Long,
    private var contentFlags: Long
) {
    actual fun getSize() = size

    actual fun getCount() = count

    actual fun getContentFlags() = contentFlags

    actual fun isCleared() = size or count or contentFlags == 0L

    actual fun clear() {
        this.size = 0
        this.count = 0
        this.contentFlags = 0
    }
}
