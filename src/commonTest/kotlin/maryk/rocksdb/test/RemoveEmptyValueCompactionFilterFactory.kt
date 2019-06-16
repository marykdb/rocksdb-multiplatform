package maryk.rocksdb.test

import maryk.rocksdb.AbstractCompactionFilterContext
import maryk.rocksdb.AbstractCompactionFilterFactory
import maryk.rocksdb.RemoveEmptyValueCompactionFilter

/**
 * Simple CompactionFilterFactory class used in tests. Generates RemoveEmptyValueCompactionFilters.
 */
class RemoveEmptyValueCompactionFilterFactory : AbstractCompactionFilterFactory<RemoveEmptyValueCompactionFilter>() {
    override fun createCompactionFilter(context: AbstractCompactionFilterContext): RemoveEmptyValueCompactionFilter {
        return RemoveEmptyValueCompactionFilter()
    }

    override fun name(): String {
        return "RemoveEmptyValueCompactionFilterFactory"
    }
}
