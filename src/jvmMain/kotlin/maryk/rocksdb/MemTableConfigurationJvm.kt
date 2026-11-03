package maryk.rocksdb

actual fun Options.configureMemTable(config: MemTableConfig): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    validateBuiltinMemTable(config)
    return setMemTableConfig(config)
}

actual fun ColumnFamilyOptions.configureMemTable(config: MemTableConfig): ColumnFamilyOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    validateBuiltinMemTable(config)
    return setMemTableConfig(config)
}
