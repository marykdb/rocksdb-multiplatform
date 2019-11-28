package maryk.rocksdb

import rocksdb.RocksDBCompactRangeOptions

actual class CompactRangeOptions internal constructor(
    internal val native: RocksDBCompactRangeOptions
) {
    actual constructor() : this(RocksDBCompactRangeOptions())

    actual fun exclusiveManualCompaction(): Boolean {
        return native.exclusiveManualCompaction
    }

    actual fun setExclusiveManualCompaction(exclusiveCompaction: Boolean): CompactRangeOptions {
        native.exclusiveManualCompaction = exclusiveCompaction
        return this
    }

    actual fun changeLevel(): Boolean {
        return native.changeLevel
    }

    actual fun setChangeLevel(changeLevel: Boolean): CompactRangeOptions {
        native.changeLevel = changeLevel
        return this
    }

    actual fun targetLevel(): Int {
        return native.targetLevel
    }

    actual fun setTargetLevel(targetLevel: Int): CompactRangeOptions {
        native.targetLevel = targetLevel
        return this
    }

    actual fun targetPathId(): Int {
        return native.targetPathId.toInt()
    }

    actual fun setTargetPathId(targetPathId: Int): CompactRangeOptions {
        native.targetPathId = targetPathId.toUInt()
        return this
    }

    actual fun bottommostLevelCompaction(): BottommostLevelCompaction? {
        return bottommostLevelCompactionFromByte(native.bottommostLevelCompaction)
    }

    actual fun setBottommostLevelCompaction(bottommostLevelCompaction: BottommostLevelCompaction): CompactRangeOptions {
        native.bottommostLevelCompaction = bottommostLevelCompaction.value
        return this
    }

    actual fun allowWriteStall(): Boolean {
        return native.allowWriteStall
    }

    actual fun setAllowWriteStall(allowWriteStall: Boolean): CompactRangeOptions {
        native.allowWriteStall = allowWriteStall
        return this
    }

    actual fun maxSubcompactions(): Int {
        return native.maxSubcompactions.toInt()
    }

    actual fun setMaxSubcompactions(maxSubcompactions: Int): CompactRangeOptions {
        native.maxSubcompactions = maxSubcompactions.toUInt()
        return this
    }
}
