package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import maryk.rocksdb.RocksDBException
import rocksdb.rocksdb_free

fun <T : Any, R : Any> T.wrapWithMultiErrorThrower(
    numKeys: Int,
    runnable: T.(CArrayPointer<CPointerVar<ByteVar>>) -> R?
): R? = memScoped {
    // Allocate an array of pointers for `errs`
    val errsArray = allocArray<CPointerVar<ByteVar>>(numKeys)

    val result = runnable(errsArray)

    for (i in 0 until numKeys) {
        val singleErrorPtr = errsArray[i]
        if (singleErrorPtr != null) {
            val errMsg = singleErrorPtr.toKString()
            rocksdb_free(singleErrorPtr)

            throw RocksDBException(
                errMsg,
                convertToStatus(errMsg)
            )
        }
    }

    return@memScoped result
}
