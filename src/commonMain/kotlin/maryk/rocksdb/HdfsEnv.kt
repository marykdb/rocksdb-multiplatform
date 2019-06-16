package maryk.rocksdb

/**
 * HDFS environment.
 */
expect class HdfsEnv
    /**
     * Creates a new environment that is used for HDFS environment.
     *
     * The caller must delete the result when it is
     * no longer needed.
     *
     * @param fsName the HDFS as a string in the form "hdfs://hostname:port/"
     */
    constructor(fsName: String)
: Env
