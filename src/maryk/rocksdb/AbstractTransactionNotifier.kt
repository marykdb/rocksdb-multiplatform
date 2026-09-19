package maryk.rocksdb

expect abstract class AbstractTransactionNotifier: RocksCallbackObject {
    /**
     * Protected constructor to initialize the notifier.
     */
    protected constructor()

    /**
     * Called when a snapshot is created.
     *
     * @param newSnapshot The snapshot that has been created.
     *
     * @throws RocksDBException When an error occurs during snapshot creation.
     */
    abstract fun snapshotCreated(newSnapshot: Snapshot)
}
