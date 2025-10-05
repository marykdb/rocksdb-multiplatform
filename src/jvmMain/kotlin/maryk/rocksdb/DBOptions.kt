package maryk.rocksdb

actual typealias DBOptions = org.rocksdb.DBOptions

actual fun DBOptions.addEventListener(listener: EventListener): DBOptions {
    val existing: MutableList<org.rocksdb.AbstractEventListener> =
        listeners()?.toMutableList() ?: mutableListOf()
    existing += listener
    return setListeners(existing)
}

actual fun Options.addEventListener(listener: EventListener): Options {
    val existing: MutableList<org.rocksdb.AbstractEventListener> =
        listeners()?.toMutableList() ?: mutableListOf()
    existing += listener
    setListeners(existing)
    return this
}
