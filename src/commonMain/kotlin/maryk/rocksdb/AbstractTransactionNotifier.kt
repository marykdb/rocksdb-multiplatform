package maryk.rocksdb

expect abstract class AbstractTransactionNotifier protected constructor() : RocksCallbackObject {
    /**
     * Implement this method to receive notification when a snapshot is
     * requested via [Transaction.setSnapshotOnNextOperation].
     *
     * @param newSnapshot the snapshot that has been created.
     */
    abstract fun snapshotCreated(newSnapshot: Snapshot)
}
