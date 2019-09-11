package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class ColumnFamilyOptions private constructor(nativeHandle: CPointer<*>) : RocksObject(nativeHandle) {
    actual constructor() : this(newColumnFamilyOptions())

    actual constructor(other: ColumnFamilyOptions) : this(copyColumnFamilyOptions(other.nativeHandle)) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual constructor(options: Options) : this(newColumnFamilyOptionsFromOptions(options.nativeHandle))

    actual fun setMinWriteBufferNumberToMerge(minWriteBufferNumberToMerge: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun minWriteBufferNumberToMerge(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxWriteBufferNumberToMaintain(maxWriteBufferNumberToMaintain: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxWriteBufferNumberToMaintain(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInplaceUpdateSupport(inplaceUpdateSupport: Boolean): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun inplaceUpdateSupport(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBloomLocality(bloomLocality: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bloomLocality(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionPerLevel(compressionLevels: List<CompressionType>): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionPerLevel(): List<CompressionType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setNumLevels(numLevels: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numLevels(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionStyle(compactionStyle: CompactionStyle): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionStyle(): CompactionStyle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionPriority(compactionPriority: CompactionPriority): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionPriority(): CompactionPriority {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionOptionsUniversal(compactionOptionsUniversal: CompactionOptionsUniversal): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionOptionsUniversal(): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionOptionsFIFO(compactionOptionsFIFO: CompactionOptionsFIFO): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionOptionsFIFO(): CompactionOptionsFIFO {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setOptimizeFiltersForHits(optimizeFiltersForHits: Boolean): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeFiltersForHits(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setForceConsistencyChecks(forceConsistencyChecks: Boolean): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun forceConsistencyChecks(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeForSmallDb(): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeForPointLookup(blockCacheSizeMb: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeLevelStyleCompaction(): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeLevelStyleCompaction(memtableMemoryBudget: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeUniversalStyleCompaction(): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeUniversalStyleCompaction(memtableMemoryBudget: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setComparator(builtinComparator: BuiltinComparator): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setComparator(comparator: AbstractComparator<out AbstractSlice<*>>): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMergeOperatorName(name: String): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMergeOperator(mergeOperator: MergeOperator): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionFilter(compactionFilter: AbstractCompactionFilter<out AbstractSlice<*>>): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionFilter(): AbstractCompactionFilter<out AbstractSlice<*>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionFilterFactory(compactionFilterFactory: AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>>): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionFilterFactory(): AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useFixedLengthPrefixExtractor(n: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useCappedPrefixExtractor(n: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelZeroFileNumCompactionTrigger(numFiles: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelZeroFileNumCompactionTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelZeroSlowdownWritesTrigger(numFiles: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelZeroSlowdownWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelZeroStopWritesTrigger(numFiles: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelZeroStopWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxTableFilesSizeFIFO(maxTableFilesSize: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxTableFilesSizeFIFO(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memTableConfig(): MemTableConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemTableConfig(memTableConfig: MemTableConfig): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memTableFactoryName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tableFormatConfig(): TableFormatConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTableFormatConfig(config: TableFormatConfig): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tableFactoryName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBottommostCompressionType(bottommostCompressionType: CompressionType): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bottommostCompressionType(): CompressionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBottommostCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bottommostCompressionOptions(): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionOptions(compressionOptions: CompressionOptions): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionOptions(): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteBufferSize(writeBufferSize: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun disableAutoCompactions(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0FileNumCompactionTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxCompactionBytes(maxCompactionBytes: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxCompactionBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelBase(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelCompactionDynamicLevelBytes(enableLevelCompactionDynamicLevelBytes: Boolean): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelCompactionDynamicLevelBytes(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionType(compressionType: CompressionType): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionType(): CompressionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxWriteBufferNumber(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInplaceUpdateNumLocks(inplaceUpdateNumLocks: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun inplaceUpdateNumLocks(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemtablePrefixBloomSizeRatio(memtablePrefixBloomSizeRatio: Double): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memtablePrefixBloomSizeRatio(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemtableHugePageSize(memtableHugePageSize: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memtableHugePageSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setArenaBlockSize(arenaBlockSize: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun arenaBlockSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0SlowdownWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0StopWritesTrigger(level0StopWritesTrigger: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0StopWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTargetFileSizeBase(targetFileSizeBase: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun targetFileSizeBase(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTargetFileSizeMultiplier(multiplier: Int): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun targetFileSizeMultiplier(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelMultiplier(multiplier: Double): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelMultiplier(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelMultiplierAdditional(maxBytesForLevelMultiplierAdditional: IntArray): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelMultiplierAdditional(): IntArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSoftPendingCompactionBytesLimit(softPendingCompactionBytesLimit: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun softPendingCompactionBytesLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setHardPendingCompactionBytesLimit(hardPendingCompactionBytesLimit: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hardPendingCompactionBytesLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSequentialSkipInIterations(maxSequentialSkipInIterations: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSequentialSkipInIterations(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSuccessiveMerges(maxSuccessiveMerges: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSuccessiveMerges(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setParanoidFileChecks(paranoidFileChecks: Boolean): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun paranoidFileChecks(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setReportBgIoStats(reportBgIoStats: Boolean): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun reportBgIoStats(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTtl(ttl: Long): ColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ttl(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newColumnFamilyOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun copyColumnFamilyOptions(nativeHandle: CPointer<*>): Options {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newColumnFamilyOptionsFromOptions(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
