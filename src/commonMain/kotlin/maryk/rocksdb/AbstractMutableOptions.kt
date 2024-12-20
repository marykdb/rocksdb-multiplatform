package maryk.rocksdb

expect open class AbstractMutableOptions

expect fun AbstractMutableOptions.getKeys(): Array<String>

expect fun AbstractMutableOptions.getValues(): Array<String>
