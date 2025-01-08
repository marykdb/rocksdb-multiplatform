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
import maryk.rocksdb.Status
import maryk.rocksdb.StatusCode
import maryk.rocksdb.StatusSubCode

private fun checkAndThrowError(errorRef: CPointerVar<ByteVar>) {
    val error = errorRef.value?.toKString()

    if (error != null) {
        throw RocksDBException(error, convertToStatus(error))
    }
}

fun <T: Any, R: Any> T.wrapWithErrorThrower(runnable: T.(CValuesRef<CPointerVar<ByteVar>>) -> R): R {
    memScoped {
        val errorRef = alloc<CPointerVar<ByteVar>>()
        try {
            val result = runnable(errorRef.ptr)
            checkAndThrowError(errorRef)
            return result
        } catch (e: Throwable) {
            checkAndThrowError(errorRef)
            throw RocksDBException(e.toString(), Status(StatusCode.Undefined, StatusSubCode.None, e.toString()))
        }
    }
}

fun <T: Any, R: Any> T.wrapWithNullErrorThrower(runnable: T.(CValuesRef<CPointerVar<ByteVar>>) -> R?): R? {
    memScoped {
        val errorRef = alloc<CPointerVar<ByteVar>>()
        try {
            val result = runnable(errorRef.ptr)
            checkAndThrowError(errorRef)
            return result
        } catch (e: Throwable) {
            checkAndThrowError(errorRef)
            throw RocksDBException(e.toString(), Status(StatusCode.Undefined, StatusSubCode.None, e.toString()))
        }
    }
}

