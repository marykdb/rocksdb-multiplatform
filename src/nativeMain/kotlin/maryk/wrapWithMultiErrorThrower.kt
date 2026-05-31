package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set
import kotlinx.cinterop.toKString
import maryk.rocksdb.RocksDBException
import rocksdb.rocksdb_free

fun <T : Any, R : Any> T.wrapWithMultiErrorThrower(
    numKeys: Int,
    runnable: T.(CArrayPointer<CPointerVar<ByteVar>>) -> R?
): R? = memScoped {
    // Allocate an array of pointers for `errs`
    val errsArray = allocArray<CPointerVar<ByteVar>>(numKeys)
    for (i in 0 until numKeys) {
        errsArray[i] = null
    }

    var thrown: Throwable? = null
    val result = try {
        runnable(errsArray)
    } catch (throwable: Throwable) {
        thrown = throwable
        null
    }

    var firstError: RocksDBException? = null
    for (i in 0 until numKeys) {
        val singleErrorPtr = errsArray[i]
        if (singleErrorPtr != null) {
            val errMsg = try {
                singleErrorPtr.toKString()
            } finally {
                rocksdb_free(singleErrorPtr)
                errsArray[i] = null
            }

            if (firstError == null) {
                firstError = RocksDBException(
                    errMsg,
                    convertToStatus(errMsg)
                )
            }
        }
    }

    firstError?.let { throw it }
    thrown?.let { throw it }

    return@memScoped result
}
