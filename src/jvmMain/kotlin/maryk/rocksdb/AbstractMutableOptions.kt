package maryk.rocksdb

import org.rocksdb.getAbstractMutableOptionsKeys
import org.rocksdb.getAbstractMutableOptionsValues

actual typealias AbstractMutableOptions = org.rocksdb.AbstractMutableOptions

actual fun AbstractMutableOptions.getKeys() =
    this.getAbstractMutableOptionsKeys()

actual fun AbstractMutableOptions.getValues() =
    this.getAbstractMutableOptionsValues()
