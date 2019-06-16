package maryk.rocksdb

/**
 * Base class for comparators which will receive
 * ByteBuffer based access via org.rocksdb.DirectSlice
 * in their compare method implementation.
 *
 * ByteBuffer based slices perform better when large keys
 * are involved. When using smaller keys consider
 * using @see org.rocksdb.Comparator
 */
expect abstract class DirectComparator(copt: ComparatorOptions) : AbstractComparator<DirectSlice>
