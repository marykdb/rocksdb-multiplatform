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

internal fun consumeRocksDBError(errorRef: CPointerVar<ByteVar>): RocksDBException? {
    val errorPtr = errorRef.value
    val error = errorPtr?.let {
        try {
            it.toKString()
        } finally {
            rocksdb_free(it)
            errorRef.value = null
        }
    }

    return error?.let { RocksDBException(it, convertToStatus(it)) }
}

internal fun checkAndThrowRocksDBError(errorRef: CPointerVar<ByteVar>) {
    consumeRocksDBError(errorRef)?.let { throw it }
}

fun <T: Any, R: Any> T.wrapWithErrorThrower(runnable: T.(CValuesRef<CPointerVar<ByteVar>>) -> R): R {
    memScoped {
        val errorRef = alloc<CPointerVar<ByteVar>>()
        errorRef.value = null
        try {
            val result = runnable(errorRef.ptr)
            checkAndThrowRocksDBError(errorRef)
            return result
        } catch (e: RocksDBException) {
            checkAndThrowRocksDBError(errorRef)
            throw e
        } catch (e: Throwable) {
            throw consumeRocksDBError(errorRef) ?: e
        }
    }
}

fun <T: Any, R: Any> T.wrapWithNullErrorThrower(runnable: T.(CValuesRef<CPointerVar<ByteVar>>) -> R?): R? {
    memScoped {
        val errorRef = alloc<CPointerVar<ByteVar>>()
        errorRef.value = null
        try {
            val result = runnable(errorRef.ptr)
            checkAndThrowRocksDBError(errorRef)
            return result
        } catch (e: RocksDBException) {
            checkAndThrowRocksDBError(errorRef)
            throw e
        } catch (e: Throwable) {
            throw consumeRocksDBError(errorRef) ?: e
        }
    }
}
