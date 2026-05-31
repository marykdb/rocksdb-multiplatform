package maryk.rocksdb

import cnames.structs.rocksdb_sstfilereader_t
import kotlinx.cinterop.CPointer
import maryk.wrapWithErrorThrower
import rocksdb.rocksdb_sstfilereader_create
import rocksdb.rocksdb_sstfilereader_destroy
import rocksdb.rocksdb_sstfilereader_open
import rocksdb.rocksdb_sstfilereader_verify_checksum

actual class SstFileReader actual constructor(
    options: Options
) : RocksObject() {
    private val native: CPointer<rocksdb_sstfilereader_t>
    private var ownedComparator: AbstractComparator? = null
    private var retainedReferences: List<Any> = emptyList()

    init {
        options.checkOwningHandle()
        retainedReferences = options.retainedNativeReferences()
        val created = requireNotNull(rocksdb_sstfilereader_create(options.native)) {
            "Unable to create SST file reader"
        }
        native = created
        ownedComparator = try {
            options.releaseOwnedComparator()
        } catch (throwable: Throwable) {
            rocksdb_sstfilereader_destroy(created)
            throw throwable
        }
    }

    actual fun open(filePath: String) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_sstfilereader_open(native, filePath, error)
        }
    }

    actual fun verifyChecksum() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_sstfilereader_verify_checksum(native, error)
        }
    }

    override fun close() {
        if (tryClose()) {
            rocksdb_sstfilereader_destroy(native)
            ownedComparator?.closeFromOptions()
            ownedComparator = null
            retainedReferences = emptyList()
            super.close()
        }
    }
}
