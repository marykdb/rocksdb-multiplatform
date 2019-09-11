package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class Transaction
    internal constructor(private val parent: RocksDB, transactionHandle: CPointer<*>)
: RocksObject(transactionHandle) {
    actual fun setSnapshot() {
    }

    actual fun setSnapshotOnNextOperation() {
    }

    actual fun setSnapshotOnNextOperation(transactionNotifier: AbstractTransactionNotifier) {
    }

    actual fun getSnapshot(): Snapshot? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun clearSnapshot() {
    }

    actual fun commit() {
    }

    actual fun rollback() {
    }

    actual fun setSavePoint() {
    }

    actual fun rollbackToSavePoint() {
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions,
        key: ByteArray
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun get(readOptions: ReadOptions, key: ByteArray): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGet(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: Array<ByteArray>
    ): Array<ByteArray?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGet(
        readOptions: ReadOptions,
        keys: Array<ByteArray>
    ): Array<ByteArray?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        exclusive: Boolean,
        do_validate: Boolean
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        exclusive: Boolean
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        key: ByteArray,
        exclusive: Boolean
    ): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGetForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: Array<ByteArray>
    ): Array<ByteArray?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun multiGetForUpdate(
        readOptions: ReadOptions,
        keys: Array<ByteArray>
    ): Array<ByteArray?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getIterator(readOptions: ReadOptions): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getIterator(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle
    ): RocksIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray,
        assume_tracked: Boolean
    ) {
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun put(key: ByteArray, value: ByteArray) {
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>,
        valueParts: Array<ByteArray>,
        assume_tracked: Boolean
    ) {
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>,
        valueParts: Array<ByteArray>
    ) {
    }

    actual fun put(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>) {
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray,
        assume_tracked: Boolean
    ) {
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        assume_tracked: Boolean
    ) {
    }

    actual fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
    }

    actual fun delete(key: ByteArray) {
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>,
        assume_tracked: Boolean
    ) {
    }

    actual fun delete(columnFamilyHandle: ColumnFamilyHandle, keyParts: Array<ByteArray>) {
    }

    actual fun delete(keyParts: Array<ByteArray>) {
    }

    actual fun putUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun putUntracked(key: ByteArray, value: ByteArray) {
    }

    actual fun putUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>,
        valueParts: Array<ByteArray>
    ) {
    }

    actual fun putUntracked(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>) {
    }

    actual fun mergeUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual fun mergeUntracked(key: ByteArray, value: ByteArray) {
    }

    actual fun deleteUntracked(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
    }

    actual fun deleteUntracked(key: ByteArray) {
    }

    actual fun deleteUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>
    ) {
    }

    actual fun deleteUntracked(keyParts: Array<ByteArray>) {
    }

    actual fun putLogData(blob: ByteArray) {
    }

    actual fun disableIndexing() {
    }

    actual fun enableIndexing() {
    }

    actual fun getNumKeys(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getNumPuts(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getNumDeletes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getNumMerges(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getElapsedTime(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getWriteBatch(): WriteBatchWithIndex {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLockTimeout(lockTimeout: Long) {
    }

    actual fun getWriteOptions(): WriteOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteOptions(writeOptions: WriteOptions) {
    }

    actual fun undoGetForUpdate(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
    }

    actual fun undoGetForUpdate(key: ByteArray) {
    }

    actual fun rebuildFromWriteBatch(writeBatch: WriteBatch) {
    }

    actual fun getCommitTimeWriteBatch(): WriteBatch {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLogNumber(logNumber: Long) {
    }

    actual fun getLogNumber(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setName(transactionName: String) {
    }

    actual fun getName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getID(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isDeadlockDetect(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getWaitingTxns(): WaitingTransactions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getState(): TransactionState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getId(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
