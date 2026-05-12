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
import rocksdb.rocksdb_free

private fun consumeError(errorRef: CPointerVar<ByteVar>): RocksDBException? {
    val errorPtr = errorRef.value
    val error = errorPtr?.toKString()
    if (errorPtr != null) {
        rocksdb_free(errorPtr)
        errorRef.value = null
    }

    return error?.let { RocksDBException(it, convertToStatus(it)) }
}

private fun checkAndThrowError(errorRef: CPointerVar<ByteVar>) {
    consumeError(errorRef)?.let { throw it }
}

fun <T: Any, R: Any> T.wrapWithErrorThrower(runnable: T.(CValuesRef<CPointerVar<ByteVar>>) -> R): R {
    memScoped {
        val errorRef = alloc<CPointerVar<ByteVar>>()
        try {
            val result = runnable(errorRef.ptr)
            checkAndThrowError(errorRef)
            return result
        } catch (e: RocksDBException) {
            checkAndThrowError(errorRef)
            throw e
        } catch (e: Throwable) {
            throw consumeError(errorRef) ?: e
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
        } catch (e: RocksDBException) {
            checkAndThrowError(errorRef)
            throw e
        } catch (e: Throwable) {
            throw consumeError(errorRef) ?: e
        }
    }
}
