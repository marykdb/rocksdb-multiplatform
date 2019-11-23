package maryk.rocksdb

/**
 * Provides Checkpoint functionality. Checkpoints
 * provide persistent snapshots of RocksDB databases.
 */
expect class Checkpoint : RocksObject {
    /**
     * Builds an open-able snapshot of RocksDB on the same disk, which
     * accepts an output directory on the same disk, and under the directory
     * (1) hard-linked SST files pointing to existing live SST files
     * (2) a copied manifest files and other files
     *
     * @param checkpointPath path to the folder where the snapshot is going
     * to be stored.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun createCheckpoint(checkpointPath: String)
}

/**
 * Creates a Checkpoint object to be used for creating open-able
 * snapshots.
 *
 * @param db [RocksDB] instance.
 * @return a Checkpoint instance.
 *
 * @throws IllegalArgumentException if [RocksDB]
 * instance is null.
 * @throws IllegalStateException if [RocksDB]
 * instance is not initialized.
 */
expect fun createCheckpoint(db: RocksDB): Checkpoint
