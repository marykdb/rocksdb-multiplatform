package maryk.rocksdb

import org.rocksdb.getAbstractMutableOptionsKeys
import org.rocksdb.getAbstractMutableOptionsValues

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
actual typealias AbstractMutableOptions = org.rocksdb.AbstractMutableOptions

actual fun AbstractMutableOptions.getKeys() =
    this.getAbstractMutableOptionsKeys()

actual fun AbstractMutableOptions.getValues() =
    this.getAbstractMutableOptionsValues()
