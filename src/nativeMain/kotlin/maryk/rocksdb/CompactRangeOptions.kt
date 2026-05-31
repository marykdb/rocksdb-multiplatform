package maryk.rocksdb

import cnames.structs.rocksdb_compactoptions_t
import kotlinx.cinterop.CPointer
import maryk.toBoolean
import maryk.toUByte
import rocksdb.rocksdb_compactoptions_create
import rocksdb.rocksdb_compactoptions_destroy
import rocksdb.rocksdb_compactoptions_get_bottommost_level_compaction
import rocksdb.rocksdb_compactoptions_get_change_level
import rocksdb.rocksdb_compactoptions_get_exclusive_manual_compaction
import rocksdb.rocksdb_compactoptions_get_target_level
import rocksdb.rocksdb_compactoptions_set_bottommost_level_compaction
import rocksdb.rocksdb_compactoptions_set_change_level
import rocksdb.rocksdb_compactoptions_set_exclusive_manual_compaction
import rocksdb.rocksdb_compactoptions_set_target_level

actual class CompactRangeOptions internal constructor(
    internal val native: CPointer<rocksdb_compactoptions_t>
) : RocksObject() {
    actual constructor() : this(requireNotNull(rocksdb_compactoptions_create()) { "Unable to allocate compact range options" })

    override fun close() {
        if (tryClose()) {
            rocksdb_compactoptions_destroy(native)
            super.close()
        }
    }

    actual fun exclusiveManualCompaction(): Boolean {
        checkOwningHandle()
        return rocksdb_compactoptions_get_exclusive_manual_compaction(native).toBoolean()
    }

    actual fun setExclusiveManualCompaction(exclusiveCompaction: Boolean): CompactRangeOptions {
        checkOwningHandle()
        rocksdb_compactoptions_set_exclusive_manual_compaction(native, exclusiveCompaction.toUByte())
        return this
    }

    actual fun changeLevel(): Boolean {
        checkOwningHandle()
        return rocksdb_compactoptions_get_change_level(native).toBoolean()
    }

    actual fun setChangeLevel(changeLevel: Boolean): CompactRangeOptions {
        checkOwningHandle()
        rocksdb_compactoptions_set_change_level(native, changeLevel.toUByte())
        return this
    }

    actual fun targetLevel(): Int {
        checkOwningHandle()
        return rocksdb_compactoptions_get_target_level(native)
    }

    actual fun setTargetLevel(targetLevel: Int): CompactRangeOptions {
        checkOwningHandle()
        rocksdb_compactoptions_set_target_level(native, targetLevel)
        return this
    }

    actual fun targetPathId(): Int {
        checkOwningHandle()
        return rocksdb.rocksdb_compactoptions_get_target_path_id(native)
    }

    actual fun setTargetPathId(targetPathId: Int): CompactRangeOptions {
        checkOwningHandle()
        rocksdb.rocksdb_compactoptions_set_target_path_id(native, targetPathId)
        return this
    }

    actual fun bottommostLevelCompaction(): BottommostLevelCompaction? {
        checkOwningHandle()
        return bottommostLevelCompactionFromByte(rocksdb_compactoptions_get_bottommost_level_compaction(native))
    }

    actual fun setBottommostLevelCompaction(bottommostLevelCompaction: BottommostLevelCompaction): CompactRangeOptions {
        checkOwningHandle()
        rocksdb_compactoptions_set_bottommost_level_compaction(native, bottommostLevelCompaction.value)
        return this
    }

    actual fun allowWriteStall(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_compactoptions_get_allow_write_stall(native).toBoolean()
    }

    actual fun setAllowWriteStall(allowWriteStall: Boolean): CompactRangeOptions {
        checkOwningHandle()
        rocksdb.rocksdb_compactoptions_set_allow_write_stall(native, allowWriteStall.toUByte())
        return this
    }

    actual fun maxSubcompactions(): Int {
        checkOwningHandle()
        return rocksdb.rocksdb_compactoptions_get_max_subcompactions(native)
    }

    actual fun setMaxSubcompactions(maxSubcompactions: Int): CompactRangeOptions {
        checkOwningHandle()
        rocksdb.rocksdb_compactoptions_set_max_subcompactions(native, maxSubcompactions)
        return this
    }
}
