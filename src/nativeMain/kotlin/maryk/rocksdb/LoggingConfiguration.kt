package maryk.rocksdb

actual fun Options.setLogger(logger: Logger): Options {
    checkOwningHandle()
    logger.checkOwningHandle()
    rocksdb.rocksdb_options_set_info_log(native, logger.native)
    return this
}

actual fun DBOptions.setLogger(logger: Logger): DBOptions {
    checkOwningHandle()
    logger.checkOwningHandle()
    rocksdb.rocksdb_options_set_info_log(native, logger.native)
    return this
}
