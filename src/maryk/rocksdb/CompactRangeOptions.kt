package maryk.rocksdb

/**
 * CompactRangeOptions is used by CompactRange() call. In the documentation of the methods "the compaction" refers to
 * any compaction that is using this CompactRangeOptions.
 */
expect class CompactRangeOptions() {
    /**
     * Returns whether the compaction is exclusive or other compactions may run concurrently at the same time.
     *
     * @return true if exclusive, false if concurrent
     */
    fun exclusiveManualCompaction(): Boolean

    /**
     * Sets whether the compaction is exclusive or other compaction are allowed run concurrently at the same time.
     *
     * @param exclusiveCompaction true if compaction should be exclusive
     * @return This CompactRangeOptions
     */
    fun setExclusiveManualCompaction(exclusiveCompaction: Boolean): CompactRangeOptions

    /**
     * Returns whether compacted files will be moved to the minimum level capable of holding the data or given level
     * (specified non-negative target_level).
     * @return true, if compacted files will be moved to the minimum level
     */
    fun changeLevel(): Boolean

    /**
     * Whether compacted files will be moved to the minimum level capable of holding the data or given level
     * (specified non-negative target_level).
     *
     * @param changeLevel If true, compacted files will be moved to the minimum level
     * @return This CompactRangeOptions
     */
    fun setChangeLevel(changeLevel: Boolean): CompactRangeOptions

    /**
     * If change_level is true and target_level have non-negative value, compacted files will be moved to target_level.
     * @return The target level for the compacted files
     */
    fun targetLevel(): Int

    /**
     * If change_level is true and target_level have non-negative value, compacted files will be moved to target_level.
     *
     * @param targetLevel target level for the compacted files
     * @return This CompactRangeOptions
     */
    fun setTargetLevel(targetLevel: Int): CompactRangeOptions

    /**
     * target_path_id for compaction output. Compaction outputs will be placed in options.db_paths[target_path_id].
     *
     * @return target_path_id
     */
    fun targetPathId(): Int

    /**
     * Compaction outputs will be placed in options.db_paths[target_path_id]. Behavior is undefined if target_path_id is
     * out of range.
     *
     * @param targetPathId target path id
     * @return This CompactRangeOptions
     */
    fun setTargetPathId(targetPathId: Int): CompactRangeOptions

    /**
     * Returns the policy for compacting the bottommost level
     * @return The BottommostLevelCompaction policy
     */
    fun bottommostLevelCompaction(): BottommostLevelCompaction?

    /**
     * Sets the policy for compacting the bottommost level
     *
     * @param bottommostLevelCompaction The policy for compacting the bottommost level
     * @return This CompactRangeOptions
     */
    fun setBottommostLevelCompaction(bottommostLevelCompaction: BottommostLevelCompaction): CompactRangeOptions

    /**
     * If true, compaction will execute immediately even if doing so would cause the DB to
     * enter write stall mode. Otherwise, it'll sleep until load is low enough.
     * @return true if compaction will execute immediately
     */
    fun allowWriteStall(): Boolean

    /**
     * If true, compaction will execute immediately even if doing so would cause the DB to
     * enter write stall mode. Otherwise, it'll sleep until load is low enough.
     *
     * @return This CompactRangeOptions
     * @param allowWriteStall true if compaction should execute immediately
     */
    fun setAllowWriteStall(allowWriteStall: Boolean): CompactRangeOptions

    /**
     * If > 0, it will replace the option in the DBOptions for this compaction
     * @return number of subcompactions
     */
    fun maxSubcompactions(): Int

    /**
     * If > 0, it will replace the option in the DBOptions for this compaction
     *
     * @param maxSubcompactions number of subcompactions
     * @return This CompactRangeOptions
     */
    fun setMaxSubcompactions(maxSubcompactions: Int): CompactRangeOptions
}
