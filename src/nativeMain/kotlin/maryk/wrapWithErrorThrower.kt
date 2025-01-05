package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import maryk.rocksdb.RocksDBException

fun <T: Any, R: Any> T.wrapWithErrorThrower(runnable: T.(CValuesRef<CPointerVar<ByteVar>>) -> R): R {
    memScoped {
        val errorRef = alloc<CPointerVar<ByteVar>>()
        val result = runnable(errorRef.ptr)
        val error = errorRef.value?.toKString()

        if (error != null) {
            throw RocksDBException(error, convertToStatus(error))
        }

        return result
    }
}

fun <T: Any, R: Any> T.wrapWithNullErrorThrower(runnable: T.(CValuesRef<CPointerVar<ByteVar>>) -> R?): R? {
    memScoped {
        val errorRef = alloc<CPointerVar<ByteVar>>()
        val result = runnable(errorRef.ptr)
        val error = errorRef.value?.toKString()

        if (error != null) {
            throw RocksDBException(error, convertToStatus(error))
        }

        return result
    }
}

