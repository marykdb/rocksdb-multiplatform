package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class Options private constructor(nativeHandle: CPointer<*>) : RocksObject(nativeHandle) {
    private var env: Env? = null

    actual constructor() : this(newOptions()) {
        env = getDefaultEnv()
    }

    actual constructor(dbOptions: DBOptions, columnFamilyOptions: ColumnFamilyOptions) : this(
        newOptions(
            dbOptions.nativeHandle,
            columnFamilyOptions.nativeHandle
        )
    ) {
        env = getDefaultEnv()
    }

    actual constructor(other: Options) : this(copyOptions(other.nativeHandle)) {
        this.env = other.env
    }

    actual fun setMaxBackgroundJobs(maxBackgroundJobs: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBackgroundJobs(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun baseBackgroundCompactions(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBackgroundCompactions(maxBackgroundCompactions: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun avoidFlushDuringShutdown(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writableFileMaxBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDelayedWriteRate(delayedWriteRate: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun delayedWriteRate(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxTotalWalSize(maxTotalWalSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxTotalWalSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun statsDumpPeriodSec(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxOpenFiles(maxOpenFiles: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxOpenFiles(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBytesPerSync(bytesPerSync: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bytesPerSync(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalBytesPerSync(walBytesPerSync: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walBytesPerSync(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionReadaheadSize(compactionReadaheadSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionReadaheadSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCreateIfMissing(flag: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMergeOperator(mergeOperator: MergeOperator): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMinWriteBufferNumberToMerge(minWriteBufferNumberToMerge: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun minWriteBufferNumberToMerge(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxWriteBufferNumberToMaintain(maxWriteBufferNumberToMaintain: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxWriteBufferNumberToMaintain(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInplaceUpdateSupport(inplaceUpdateSupport: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun inplaceUpdateSupport(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBloomLocality(bloomLocality: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bloomLocality(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionPerLevel(compressionLevels: List<CompressionType>): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionPerLevel(): List<CompressionType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setNumLevels(numLevels: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numLevels(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelCompactionDynamicLevelBytes(enableLevelCompactionDynamicLevelBytes: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelCompactionDynamicLevelBytes(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionStyle(compactionStyle: CompactionStyle): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionStyle(): CompactionStyle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionPriority(compactionPriority: CompactionPriority): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionPriority(): CompactionPriority {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionOptionsUniversal(compactionOptionsUniversal: CompactionOptionsUniversal): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionOptionsUniversal(): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionOptionsFIFO(compactionOptionsFIFO: CompactionOptionsFIFO): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionOptionsFIFO(): CompactionOptionsFIFO {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setOptimizeFiltersForHits(optimizeFiltersForHits: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeFiltersForHits(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setForceConsistencyChecks(forceConsistencyChecks: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun forceConsistencyChecks(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteBufferSize(writeBufferSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun disableAutoCompactions(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0FileNumCompactionTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxCompactionBytes(maxCompactionBytes: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxCompactionBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelBase(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionType(compressionType: CompressionType): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionType(): CompressionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeForSmallDb(): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeForPointLookup(blockCacheSizeMb: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeLevelStyleCompaction(): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeLevelStyleCompaction(memtableMemoryBudget: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeUniversalStyleCompaction(): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeUniversalStyleCompaction(memtableMemoryBudget: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setComparator(builtinComparator: BuiltinComparator): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setComparator(comparator: AbstractComparator<out AbstractSlice<*>>): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMergeOperatorName(name: String): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionFilter(compactionFilter: AbstractCompactionFilter<out AbstractSlice<*>>): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionFilter(): AbstractCompactionFilter<out AbstractSlice<*>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionFilterFactory(compactionFilterFactory: AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>>): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionFilterFactory(): AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useFixedLengthPrefixExtractor(n: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useCappedPrefixExtractor(n: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelZeroFileNumCompactionTrigger(numFiles: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelZeroFileNumCompactionTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelZeroSlowdownWritesTrigger(numFiles: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelZeroSlowdownWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevelZeroStopWritesTrigger(numFiles: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun levelZeroStopWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelMultiplier(multiplier: Double): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelMultiplier(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxTableFilesSizeFIFO(maxTableFilesSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxTableFilesSizeFIFO(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memTableConfig(): MemTableConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemTableConfig(memTableConfig: MemTableConfig): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memTableFactoryName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tableFormatConfig(): TableFormatConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTableFormatConfig(config: TableFormatConfig): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tableFactoryName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBottommostCompressionType(bottommostCompressionType: CompressionType): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bottommostCompressionType(): CompressionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBottommostCompressionOptions(compressionOptions: CompressionOptions): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bottommostCompressionOptions(): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionOptions(compressionOptions: CompressionOptions): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionOptions(): CompressionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxWriteBufferNumber(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInplaceUpdateNumLocks(inplaceUpdateNumLocks: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun inplaceUpdateNumLocks(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemtablePrefixBloomSizeRatio(memtablePrefixBloomSizeRatio: Double): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memtablePrefixBloomSizeRatio(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemtableHugePageSize(memtableHugePageSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memtableHugePageSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setArenaBlockSize(arenaBlockSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun arenaBlockSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0SlowdownWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0StopWritesTrigger(level0StopWritesTrigger: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0StopWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTargetFileSizeBase(targetFileSizeBase: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun targetFileSizeBase(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTargetFileSizeMultiplier(multiplier: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun targetFileSizeMultiplier(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelMultiplierAdditional(maxBytesForLevelMultiplierAdditional: IntArray): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelMultiplierAdditional(): IntArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSoftPendingCompactionBytesLimit(softPendingCompactionBytesLimit: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun softPendingCompactionBytesLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setHardPendingCompactionBytesLimit(hardPendingCompactionBytesLimit: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hardPendingCompactionBytesLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSequentialSkipInIterations(maxSequentialSkipInIterations: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSequentialSkipInIterations(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSuccessiveMerges(maxSuccessiveMerges: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSuccessiveMerges(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setParanoidFileChecks(paranoidFileChecks: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun paranoidFileChecks(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setReportBgIoStats(reportBgIoStats: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun reportBgIoStats(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTtl(ttl: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ttl(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnv(env: Env): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getEnv(): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIncreaseParallelism(totalThreads: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createIfMissing(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCreateMissingColumnFamilies(flag: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createMissingColumnFamilies(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setErrorIfExists(errorIfExists: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun errorIfExists(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setParanoidChecks(paranoidChecks: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun paranoidChecks(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRateLimiter(rateLimiter: RateLimiter): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSstFileManager(sstFileManager: SstFileManager): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLogger(logger: Logger): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun infoLogLevel(): InfoLogLevel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxFileOpeningThreads(maxFileOpeningThreads: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxFileOpeningThreads(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStatistics(statistics: Statistics): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun statistics(): Statistics {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseFsync(useFsync: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useFsync(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDbPaths(dbPaths: Collection<DbPath>): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dbPaths(): List<DbPath> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDbLogDir(dbLogDir: String): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dbLogDir(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalDir(walDir: String): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walDir(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDeleteObsoleteFilesPeriodMicros(micros: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun deleteObsoleteFilesPeriodMicros(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSubcompactions(maxSubcompactions: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSubcompactions(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBackgroundFlushes(maxBackgroundFlushes: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBackgroundFlushes(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxLogFileSize(maxLogFileSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxLogFileSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLogFileTimeToRoll(logFileTimeToRoll: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun logFileTimeToRoll(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setKeepLogFileNum(keepLogFileNum: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keepLogFileNum(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRecycleLogFileNum(recycleLogFileNum: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun recycleLogFileNum(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxManifestFileSize(maxManifestFileSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxManifestFileSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTableCacheNumshardbits(tableCacheNumshardbits: Int): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tableCacheNumshardbits(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalTtlSeconds(walTtlSeconds: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walTtlSeconds(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalSizeLimitMB(sizeLimitMB: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walSizeLimitMB(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setManifestPreallocationSize(size: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun manifestPreallocationSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseDirectReads(useDirectReads: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useDirectReads(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseDirectIoForFlushAndCompaction(useDirectIoForFlushAndCompaction: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useDirectIoForFlushAndCompaction(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowFAllocate(allowFAllocate: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowFAllocate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowMmapReads(allowMmapReads: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowMmapReads(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowMmapWrites(allowMmapWrites: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowMmapWrites(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIsFdCloseOnExec(isFdCloseOnExec: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isFdCloseOnExec(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAdviseRandomOnOpen(adviseRandomOnOpen: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun adviseRandomOnOpen(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDbWriteBufferSize(dbWriteBufferSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteBufferManager(writeBufferManager: WriteBufferManager): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeBufferManager(): WriteBufferManager {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dbWriteBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAccessHintOnCompactionStart(accessHint: AccessHint): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun accessHintOnCompactionStart(): AccessHint {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setNewTableReaderForCompactionInputs(newTableReaderForCompactionInputs: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newTableReaderForCompactionInputs(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRandomAccessMaxBufferSize(randomAccessMaxBufferSize: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun randomAccessMaxBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseAdaptiveMutex(useAdaptiveMutex: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useAdaptiveMutex(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnableThreadTracking(enableThreadTracking: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun enableThreadTracking(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnablePipelinedWrite(enablePipelinedWrite: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun enablePipelinedWrite(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowConcurrentMemtableWrite(allowConcurrentMemtableWrite: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowConcurrentMemtableWrite(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnableWriteThreadAdaptiveYield(enableWriteThreadAdaptiveYield: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun enableWriteThreadAdaptiveYield(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteThreadMaxYieldUsec(writeThreadMaxYieldUsec: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeThreadMaxYieldUsec(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteThreadSlowYieldUsec(writeThreadSlowYieldUsec: Long): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeThreadSlowYieldUsec(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSkipStatsUpdateOnDbOpen(skipStatsUpdateOnDbOpen: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun skipStatsUpdateOnDbOpen(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalRecoveryMode(walRecoveryMode: WALRecoveryMode): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walRecoveryMode(): WALRecoveryMode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllow2pc(allow2pc: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allow2pc(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRowCache(rowCache: Cache): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun rowCache(): Cache {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalFilter(walFilter: AbstractWalFilter): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walFilter(): WalFilter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setFailIfOptionsFileError(failIfOptionsFileError: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun failIfOptionsFileError(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDumpMallocStats(dumpMallocStats: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dumpMallocStats(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAvoidFlushDuringRecovery(avoidFlushDuringRecovery: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun avoidFlushDuringRecovery(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowIngestBehind(allowIngestBehind: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowIngestBehind(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setPreserveDeletes(preserveDeletes: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun preserveDeletes(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTwoWriteQueues(twoWriteQueues: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun twoWriteQueues(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setManualWalFlush(manualWalFlush: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun manualWalFlush(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAtomicFlush(atomicFlush: Boolean): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun atomicFlush(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun prepareForBulkLoad(): Options {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun newOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun newOptions(nativeHandle: CPointer<*>, nativeHandle1: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun copyOptions(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
