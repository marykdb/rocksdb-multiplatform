package maryk.rocksdb

actual enum class IndexShorteningMode {
    kNoShortening,
    kShortenSeparators,
    kShortenSeparatorsAndSuccessor
}
