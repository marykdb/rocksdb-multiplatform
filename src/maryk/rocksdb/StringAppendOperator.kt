package maryk.rocksdb

/**
 * `StringAppendOperator` is a merge operator that concatenates
 * two strings.
 *
 * This operator is useful for scenarios where merging values involves
 * simple string concatenation, potentially with a delimiter.
 *
 * **Example Usage:**
 *
 * ```kotlin
 * val mergeOperator = StringAppendOperator(",")
 * dbOptions.setMergeOperator(mergeOperator)
 * ```
 */
expect class StringAppendOperator : MergeOperator {

    /**
     * Constructs a `StringAppendOperator` with a default delimiter `,`.
     *
     * This constructor is useful when a simple comma-separated concatenation is desired.
     */
    constructor()

    /**
     * Constructs a `StringAppendOperator` with the specified character delimiter.
     *
     * @param delim The character delimiter to use between concatenated strings.
     *
     * **Example:**
     *
     * ```kotlin
     * val mergeOperator = StringAppendOperator(';')
     * ```
     */
    constructor(delim: Char)

    /**
     * Constructs a `StringAppendOperator` with the specified string delimiter.
     *
     * @param delim The string delimiter to use between concatenated strings.
     *
     * **Example:**
     *
     * ```kotlin
     * val mergeOperator = StringAppendOperator("||")
     * ```
     */
    constructor(delim: String)
}
