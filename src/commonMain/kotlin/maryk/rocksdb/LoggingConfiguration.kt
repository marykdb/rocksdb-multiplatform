package maryk.rocksdb

/** Attach a shared logger. Close databases and options before closing the logger. */
expect fun Options.setLogger(logger: Logger): Options

/** Attach a shared logger. Close databases and options before closing the logger. */
expect fun DBOptions.setLogger(logger: Logger): DBOptions
