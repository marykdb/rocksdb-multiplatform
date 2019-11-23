package maryk

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.rocksdb.RocksDBException
import maryk.rocksdb.Status
import maryk.rocksdb.StatusCode.NotFound
import maryk.rocksdb.getStatusCode
import maryk.rocksdb.getStatusSubCode
import platform.Foundation.NSError

fun <T: Any, R: Any> T.wrapWithErrorThrower(runnable: T.(CPointer<ObjCObjectVar<NSError?>>) -> R): R {
    val errorRef = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
    val result = runnable(errorRef.ptr)
    val error = errorRef.value

    if (error != null) {
        if (error.domain == "co.braincookie.objectiverocks.error") {
            val status = convertStatus(error)
            throw RocksDBException(error.localizedDescription +": "+error.localizedFailureReason, status)
        } else {
            throw RocksDBException(error.localizedDescription)
        }
    }

    return result
}

fun <T: Any, R: Any> T.wrapWithNullErrorThrower(runnable: T.(CPointer<ObjCObjectVar<NSError?>>) -> R?): R? {
    val errorRef = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
    val result = runnable(errorRef.ptr)
    val error = errorRef.value

    if (error != null) {
        if (error.domain == "co.braincookie.objectiverocks.error") {
            val status = convertStatus(error)
            if (status.getCode() == NotFound) {
                return null
            }

            throw RocksDBException(error.localizedDescription +": "+error.localizedFailureReason, status)
        } else {
            throw RocksDBException(error.localizedDescription)
        }
    }

    return result
}

private fun convertStatus(error: NSError) = Status(
    getStatusCode(error.code.toByte()),
    error.userInfo["rocksdb.subcode"]?.let { getStatusSubCode((it as Short).toByte()) },
    error.localizedFailureReason
)
