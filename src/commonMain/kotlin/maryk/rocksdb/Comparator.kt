package maryk.rocksdb

/**
 * Base class for comparators which will receive
 * byte[] based access via org.rocksdb.Slice in their
 * compare method implementation.
 *
 * byte[] based slices perform better when small keys
 * are involved. When using larger keys consider
 * using @see [DirectComparator]
 */
expect abstract class Comparator(copt: ComparatorOptions) : AbstractComparator<Slice>
