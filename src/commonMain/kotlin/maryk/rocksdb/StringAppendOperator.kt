package maryk.rocksdb

/**
 * StringAppendOperator is a merge operator that concatenates
 * two strings.
 */
expect class StringAppendOperator() : MergeOperator {
    constructor(delim: Char)
}
