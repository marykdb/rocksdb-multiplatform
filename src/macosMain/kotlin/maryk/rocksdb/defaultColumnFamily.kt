package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual val defaultColumnFamily: ByteArray
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
actual val rocksDBNotFound: Int
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

actual open class RocksDB
    protected constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle) {
    actual fun closeE() {
    }

    actual override fun close() {
    }

    actual fun createColumnFamily(columnFamilyDescriptor: ColumnFamilyDescriptor): ColumnFamilyHandle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createColumnFamilies(
        columnFamilyOptions: ColumnFamilyOptions,
        columnFamilyNames: List<ByteArray>
    ): List<ColumnFamilyHandle> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createColumnFamilies(columnFamilyDescriptors: List<ColumnFamilyDescriptor>): List<ColumnFamilyHandle> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun dropColumnFamily(columnFamilyHandle: ColumnFamilyHandle) {
    }

    actual fun dropColumnFamilies(columnFamilies: List<ColumnFamilyHandle>) {
    }

    actual fun put(key: ByteArray, value: ByteArray) {
    }

    actual fun put(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun put(writeOpts: WriteOptions, key: ByteArray, value: ByteArray) {
    }

    actual fun put(
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun delete(key: ByteArray) {
    }

    actual fun delete(key: ByteArray, offset: Int, len: Int) {
    }

    actual fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
    }

    actual fun delete(writeOpt: WriteOptions, key: ByteArray) {
    }

    actual fun delete(
        writeOpt: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        key: ByteArray
    ) {
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
    }

    actual fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
    }

    actual fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        beginKey: ByteArray,
        endKey: ByteArray
    ) {
    }

    actual fun deleteRange(writeOpt: WriteOptions, beginKey: ByteArray, endKey: ByteArray) {
    }

    actual fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        beginKey: ByteArray,
        endKey: ByteArray
    ) {
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
    }

    actual fun merge(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun merge(writeOpts: WriteOptions, key: ByteArray, value: ByteArray) {
    }

    actual fun merge(
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
    }

    actual fun write(writeOpts: WriteOptions, updates: WriteBatch) {
    }

    actual fun write(writeOpts: WriteOptions, updates: WriteBatchWithIndex) {
    }

    actual fun get(key: ByteArray, value: ByteArray): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(opt: ReadOptions, key: ByteArray, value: ByteArray): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        value: ByteArray
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual operator fun get(key: ByteArray): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(key: ByteArray, offset: Int, len: Int): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(opt: ReadOptions, key: ByteArray): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGetAsList(keys: List<ByteArray>): List<ByteArray> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGetAsList(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGetAsList(
        opt: ReadOptions,
        keys: List<ByteArray>
    ): List<ByteArray> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGetAsList(
        opt: ReadOptions,
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(key: ByteArray, value: StringBuilder): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: StringBuilder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray,
        value: StringBuilder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: StringBuilder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIterator(): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIterator(readOptions: ReadOptions): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIterator(columnFamilyHandle: ColumnFamilyHandle): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIterator(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions
    ): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIterators(columnFamilyHandleList: List<ColumnFamilyHandle>): List<RocksIterator> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun newIterators(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        readOptions: ReadOptions
    ): List<RocksIterator> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getSnapshot(): Snapshot? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun releaseSnapshot(snapshot: Snapshot?) {
    }

    actual fun getProperty(
        columnFamilyHandle: ColumnFamilyHandle?,
        property: String
    ): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getProperty(property: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getMapProperty(property: String): Map<String, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getMapProperty(
        columnFamilyHandle: ColumnFamilyHandle?,
        property: String
    ): Map<String, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getLongProperty(property: String): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getLongProperty(
        columnFamilyHandle: ColumnFamilyHandle?,
        property: String
    ): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun resetStats() {
    }

    actual fun getAggregatedLongProperty(property: String): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getApproximateSizes(
        columnFamilyHandle: ColumnFamilyHandle?,
        ranges: List<Range>,
        vararg sizeApproximationFlags: SizeApproximationFlag
    ): LongArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getApproximateSizes(
        ranges: List<Range>,
        vararg sizeApproximationFlags: SizeApproximationFlag
    ): LongArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getApproximateMemTableStats(
        columnFamilyHandle: ColumnFamilyHandle?,
        range: Range
    ): CountAndSize {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getApproximateMemTableStats(range: Range): CountAndSize {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactRange() {
    }

    actual fun compactRange(columnFamilyHandle: ColumnFamilyHandle?) {
    }

    actual fun compactRange(begin: ByteArray, end: ByteArray) {
    }

    actual fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle?,
        begin: ByteArray?,
        end: ByteArray?
    ) {
    }

    actual fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle,
        begin: ByteArray?,
        end: ByteArray?,
        compactRangeOptions: CompactRangeOptions
    ) {
    }

    actual fun setOptions(
        columnFamilyHandle: ColumnFamilyHandle,
        mutableColumnFamilyOptions: MutableColumnFamilyOptions
    ) {
    }

    actual fun setOptions(mutableColumnFamilyOptions: MutableColumnFamilyOptions) {
    }

    actual fun setDBOptions(mutableDBoptions: MutableDBOptions) {
    }

    actual fun compactFiles(
        compactionOptions: CompactionOptions,
        inputFileNames: List<String>,
        outputLevel: Int,
        outputPathId: Int,
        compactionJobInfo: CompactionJobInfo?
    ): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactFiles(
        compactionOptions: CompactionOptions,
        columnFamilyHandle: ColumnFamilyHandle?,
        inputFileNames: List<String>,
        outputLevel: Int,
        outputPathId: Int,
        compactionJobInfo: CompactionJobInfo?
    ): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun pauseBackgroundWork() {
    }

    actual fun continueBackgroundWork() {
    }

    actual fun enableAutoCompaction(columnFamilyHandles: List<ColumnFamilyHandle>) {
    }

    actual fun numberLevels(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun numberLevels(columnFamilyHandle: ColumnFamilyHandle?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxMemCompactionLevel(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxMemCompactionLevel(columnFamilyHandle: ColumnFamilyHandle?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0StopWriteTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0StopWriteTrigger(columnFamilyHandle: ColumnFamilyHandle?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getEnv(): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun flush(flushOptions: FlushOptions) {
    }

    actual fun flush(flushOptions: FlushOptions, columnFamilyHandle: ColumnFamilyHandle?) {
    }

    actual fun flush(
        flushOptions: FlushOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>?
    ) {
    }

    actual fun flushWal(sync: Boolean) {
    }

    actual fun syncWal() {
    }

    actual fun getLatestSequenceNumber(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setPreserveDeletesSequenceNumber(sequenceNumber: Long): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun disableFileDeletions() {
    }

    actual fun enableFileDeletions(force: Boolean) {
    }

    actual fun getLiveFiles(): LiveFiles {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getLiveFiles(flushMemtable: Boolean): LiveFiles {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getSortedWalFiles(): List<LogFile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getUpdatesSince(sequenceNumber: Long): TransactionLogIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun deleteFile(name: String) {
    }

    actual fun getLiveFilesMetaData(): List<LiveFileMetaData> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getColumnFamilyMetaData(columnFamilyHandle: ColumnFamilyHandle?): ColumnFamilyMetaData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun GetColumnFamilyMetaData(): ColumnFamilyMetaData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ingestExternalFile(
        filePathList: List<String>,
        ingestExternalFileOptions: IngestExternalFileOptions
    ) {
    }

    actual fun ingestExternalFile(
        columnFamilyHandle: ColumnFamilyHandle,
        filePathList: List<String>,
        ingestExternalFileOptions: IngestExternalFileOptions
    ) {
    }

    actual fun verifyChecksum() {
    }

    actual fun getDefaultColumnFamily(): ColumnFamilyHandle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getPropertiesOfAllTables(columnFamilyHandle: ColumnFamilyHandle?): Map<String, TableProperties> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getPropertiesOfAllTables(): Map<String, TableProperties> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getPropertiesOfTablesInRange(
        columnFamilyHandle: ColumnFamilyHandle?,
        ranges: List<Range>
    ): Map<String, TableProperties> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getPropertiesOfTablesInRange(ranges: List<Range>): Map<String, TableProperties> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun suggestCompactRange(columnFamilyHandle: ColumnFamilyHandle?): Range {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun suggestCompactRange(): Range {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun promoteL0(columnFamilyHandle: ColumnFamilyHandle?, targetLevel: Int) {
    }

    actual fun promoteL0(targetLevel: Int) {
    }

    actual fun startTrace(traceOptions: TraceOptions, traceWriter: AbstractTraceWriter) {
    }

    actual fun endTrace() {
    }


}

actual fun destroyRocksDB(path: String, options: Options) {
}

actual fun listColumnFamilies(
    options: Options,
    path: String
): List<ByteArray> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
