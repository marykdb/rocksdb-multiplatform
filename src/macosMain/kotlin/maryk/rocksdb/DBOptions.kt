package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class DBOptions constructor(nativeHandle: CPointer<*>) : RocksObject(nativeHandle) {
    actual constructor() : this(newDBOptions())

    actual constructor(other: DBOptions) : this(copyDBOptions(other.nativeHandle)) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual constructor(options: Options) : this(newDBOptionsFromOptions(options.nativeHandle)) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun optimizeForSmallDb(): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnv(env: Env): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getEnv(): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIncreaseParallelism(totalThreads: Int): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCreateIfMissing(flag: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createIfMissing(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCreateMissingColumnFamilies(flag: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createMissingColumnFamilies(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setErrorIfExists(errorIfExists: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun errorIfExists(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setParanoidChecks(paranoidChecks: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun paranoidChecks(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRateLimiter(rateLimiter: RateLimiter): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSstFileManager(sstFileManager: SstFileManager): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLogger(logger: Logger): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun infoLogLevel(): InfoLogLevel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxFileOpeningThreads(maxFileOpeningThreads: Int): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxFileOpeningThreads(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStatistics(statistics: Statistics): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun statistics(): Statistics {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseFsync(useFsync: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useFsync(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDbPaths(dbPaths: Collection<DbPath>): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dbPaths(): List<DbPath> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDbLogDir(dbLogDir: String): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dbLogDir(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalDir(walDir: String): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walDir(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDeleteObsoleteFilesPeriodMicros(micros: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun deleteObsoleteFilesPeriodMicros(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSubcompactions(maxSubcompactions: Int): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSubcompactions(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxLogFileSize(maxLogFileSize: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxLogFileSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLogFileTimeToRoll(logFileTimeToRoll: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun logFileTimeToRoll(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setKeepLogFileNum(keepLogFileNum: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keepLogFileNum(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRecycleLogFileNum(recycleLogFileNum: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun recycleLogFileNum(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxManifestFileSize(maxManifestFileSize: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxManifestFileSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTableCacheNumshardbits(tableCacheNumshardbits: Int): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun tableCacheNumshardbits(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalTtlSeconds(walTtlSeconds: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walTtlSeconds(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalSizeLimitMB(sizeLimitMB: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walSizeLimitMB(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setManifestPreallocationSize(size: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun manifestPreallocationSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseDirectReads(useDirectReads: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useDirectReads(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseDirectIoForFlushAndCompaction(useDirectIoForFlushAndCompaction: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useDirectIoForFlushAndCompaction(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowFAllocate(allowFAllocate: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowFAllocate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowMmapReads(allowMmapReads: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowMmapReads(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowMmapWrites(allowMmapWrites: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowMmapWrites(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIsFdCloseOnExec(isFdCloseOnExec: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isFdCloseOnExec(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAdviseRandomOnOpen(adviseRandomOnOpen: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun adviseRandomOnOpen(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDbWriteBufferSize(dbWriteBufferSize: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteBufferManager(writeBufferManager: WriteBufferManager): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeBufferManager(): WriteBufferManager {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dbWriteBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAccessHintOnCompactionStart(accessHint: AccessHint): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun accessHintOnCompactionStart(): AccessHint {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setNewTableReaderForCompactionInputs(newTableReaderForCompactionInputs: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newTableReaderForCompactionInputs(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRandomAccessMaxBufferSize(randomAccessMaxBufferSize: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun randomAccessMaxBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseAdaptiveMutex(useAdaptiveMutex: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useAdaptiveMutex(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnableThreadTracking(enableThreadTracking: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun enableThreadTracking(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnablePipelinedWrite(enablePipelinedWrite: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun enablePipelinedWrite(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowConcurrentMemtableWrite(allowConcurrentMemtableWrite: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowConcurrentMemtableWrite(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setEnableWriteThreadAdaptiveYield(enableWriteThreadAdaptiveYield: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun enableWriteThreadAdaptiveYield(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteThreadMaxYieldUsec(writeThreadMaxYieldUsec: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeThreadMaxYieldUsec(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteThreadSlowYieldUsec(writeThreadSlowYieldUsec: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeThreadSlowYieldUsec(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSkipStatsUpdateOnDbOpen(skipStatsUpdateOnDbOpen: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun skipStatsUpdateOnDbOpen(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalRecoveryMode(walRecoveryMode: WALRecoveryMode): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walRecoveryMode(): WALRecoveryMode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllow2pc(allow2pc: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allow2pc(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRowCache(rowCache: Cache): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun rowCache(): Cache {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalFilter(walFilter: AbstractWalFilter): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walFilter(): WalFilter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setFailIfOptionsFileError(failIfOptionsFileError: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun failIfOptionsFileError(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDumpMallocStats(dumpMallocStats: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dumpMallocStats(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAvoidFlushDuringRecovery(avoidFlushDuringRecovery: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun avoidFlushDuringRecovery(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowIngestBehind(allowIngestBehind: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowIngestBehind(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setPreserveDeletes(preserveDeletes: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun preserveDeletes(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTwoWriteQueues(twoWriteQueues: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun twoWriteQueues(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setManualWalFlush(manualWalFlush: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun manualWalFlush(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAtomicFlush(atomicFlush: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun atomicFlush(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBackgroundJobs(maxBackgroundJobs: Int): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBackgroundJobs(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun baseBackgroundCompactions(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun avoidFlushDuringShutdown(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writableFileMaxBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDelayedWriteRate(delayedWriteRate: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    actual fun delayedWriteRate(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxTotalWalSize(maxTotalWalSize: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxTotalWalSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun statsDumpPeriodSec(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxOpenFiles(maxOpenFiles: Int): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxOpenFiles(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBytesPerSync(bytesPerSync: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bytesPerSync(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalBytesPerSync(walBytesPerSync: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walBytesPerSync(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionReadaheadSize(compactionReadaheadSize: Long): DBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionReadaheadSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newDBOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun copyDBOptions(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented")
}

private fun newDBOptionsFromOptions(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented")
}
