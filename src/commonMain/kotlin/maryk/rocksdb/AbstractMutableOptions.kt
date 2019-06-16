package maryk.rocksdb

expect abstract class AbstractMutableOptions

expect fun AbstractMutableOptions.getKeys(): Array<String>

expect fun AbstractMutableOptions.getValues(): Array<String>
