package maryk.rocksdb

/** A wrapper around EmptyValueCompactionFilter implemented in C++ */
expect class RemoveEmptyValueCompactionFilter() : AbstractCompactionFilter<Slice>
