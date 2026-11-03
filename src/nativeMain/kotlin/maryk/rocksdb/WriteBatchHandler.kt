@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import maryk.asSizeT
import maryk.usePointer
import maryk.toByteArray
import maryk.wrapWithErrorThrower
import platform.posix.size_t

private class WriteBatchIteration(val handler: WriteBatchHandler) {
    var failure: Throwable? = null
}

actual fun WriteBatch.iterate(handler: WriteBatchHandler) {
    check(isOwningHandle()) { "Write batch is closed or borrowed" }
    val iteration = WriteBatchIteration(handler)
    val state = StableRef.create(iteration)
    try {
        try {
            wrapWithErrorThrower { error ->
                rocksdb.rocksdb_writebatch_iterate_full(
                    native, state.asCPointer(), staticCFunction(::onBatchRecord),
                    staticCFunction(::onBatchContinue), error,
                )
            }
        } catch (error: Throwable) {
            iteration.failure?.let { throw it }
            throw error
        }
        iteration.failure?.let { throw it }
    } finally {
        state.dispose()
    }
}

private fun onBatchContinue(state: COpaquePointer?): UByte {
    val iteration = state!!.asStableRef<WriteBatchIteration>().get()
    if (iteration.failure != null) return 0u
    return try {
        if (iteration.handler.shouldContinue()) 1u else 0u
    } catch (error: Throwable) {
        iteration.failure = error
        0u
    }
}

private fun onBatchRecord(
    state: COpaquePointer?, tag: UByte, columnFamilyId: UInt,
    key: CPointer<ByteVar>?, keyLength: size_t,
    value: CPointer<ByteVar>?, valueLength: size_t, flag: UByte,
): Int {
    val iteration = state!!.asStableRef<WriteBatchIteration>().get()
    if (iteration.failure != null) return 0
    return try {
        val k = key?.toByteArray(keyLength) ?: ByteArray(0)
        val v = value?.toByteArray(valueLength) ?: ByteArray(0)
        val handler = iteration.handler
        when (tag.toInt()) {
            0 -> handler.put(columnFamilyId.toInt(), k, v)
            1 -> handler.delete(columnFamilyId.toInt(), k)
            2 -> handler.singleDelete(columnFamilyId.toInt(), k)
            3 -> handler.deleteRange(columnFamilyId.toInt(), k, v)
            4 -> handler.merge(columnFamilyId.toInt(), k, v)
            5 -> handler.logData(k)
            6 -> handler.putBlobIndex(columnFamilyId.toInt(), k, v)
            7 -> handler.markBeginPrepare()
            8 -> handler.markEndPrepare(k)
            9 -> handler.markNoop(flag != 0.toUByte())
            10 -> handler.markRollback(k)
            11 -> handler.markCommit(k)
            12 -> handler.markCommitWithTimestamp(k, v)
            else -> error("Unknown RocksDB write batch record tag: $tag")
        }
        1
    } catch (error: Throwable) {
        iteration.failure = error
        0
    }
}

actual fun writeBatchFromData(serialized: ByteArray): WriteBatch {
    require(serialized.size >= 12) { "A serialized write batch needs its 12-byte header" }
    return serialized.usePointer { pointer ->
        WriteBatch(requireNotNull(rocksdb.rocksdb_writebatch_create_from(pointer, serialized.size.asSizeT())) {
            "Unable to reconstruct RocksDB write batch"
        })
    }
}
