package maryk.rocksdb

import cnames.structs.rocksdb_sstfilewriter_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.cinterop.usePinned
import maryk.wrapWithErrorThrower
import rocksdb.rocksdb_sstfilewriter_create
import rocksdb.rocksdb_sstfilewriter_delete
import rocksdb.rocksdb_sstfilewriter_destroy
import rocksdb.rocksdb_sstfilewriter_finish
import rocksdb.rocksdb_sstfilewriter_file_size
import rocksdb.rocksdb_sstfilewriter_merge
import rocksdb.rocksdb_sstfilewriter_open
import rocksdb.rocksdb_sstfilewriter_put

actual class SstFileWriter actual constructor(
    private val envOptions: EnvOptions,
    private val options: Options
) : RocksObject() {
    internal val native: CPointer<rocksdb_sstfilewriter_t> =
        rocksdb_sstfilewriter_create(envOptions.native, options.native)!!

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_sstfilewriter_destroy(native)
            super.close()
        }
    }

    actual fun open(filePath: String) {
        memScoped {
            wrapWithErrorThrower { error ->
                rocksdb_sstfilewriter_open(native, filePath, error)
            }
        }
    }

    actual fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            key.usePinned { keyPinned ->
                value.usePinned { valuePinned ->
                    rocksdb_sstfilewriter_put(
                        native,
                        keyPinned.addressOf(0),
                        key.size.toULong(),
                        valuePinned.addressOf(0),
                        value.size.toULong(),
                        error
                    )
                }
            }
        }
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            key.usePinned { keyPinned ->
                value.usePinned { valuePinned ->
                    rocksdb_sstfilewriter_merge(
                        native,
                        keyPinned.addressOf(0),
                        key.size.toULong(),
                        valuePinned.addressOf(0),
                        value.size.toULong(),
                        error
                    )
                }
            }
        }
    }

    actual fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            key.usePinned { keyPinned ->
                rocksdb_sstfilewriter_delete(
                    native,
                    keyPinned.addressOf(0),
                    key.size.toULong(),
                    error
                )
            }
        }
    }

    actual fun finish() {
        wrapWithErrorThrower { error ->
            rocksdb_sstfilewriter_finish(native, error)
        }
    }

    actual fun fileSize(): Long {
        memScoped {
            val sizeVar = alloc<ULongVar>()
            rocksdb_sstfilewriter_file_size(native, sizeVar.ptr)
            return sizeVar.value.toLong()
        }
    }
}
