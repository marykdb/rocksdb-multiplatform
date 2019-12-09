package org.rocksdb

fun AbstractMutableOptions.getAbstractMutableOptionsKeys(): Array<String> =
    this.keys

fun AbstractMutableOptions.getAbstractMutableOptionsValues(): Array<String> =
    this.values
