@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.*
import maryk.asSizeT
import platform.posix.size_t
import platform.posix.size_tVar

actual class StringAppendOperator actual constructor(
    delim: String
) : MergeOperator() {
    private val delimiter: String = delim

    actual constructor(delim: Char) : this(delim.toString())
    actual constructor() : this(",")

    override fun fullMerge(
        key: CPointer<ByteVarOf<Byte>>?, keyLen: size_t,
        existingValue: CPointer<ByteVarOf<Byte>>?, existingValueLen: size_t,
        operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
        operandsLengths: CPointer<size_tVar>?, numOperands: Int
    ): Pair<Boolean, Pair<CPointer<ByteVarOf<Byte>>?, size_t>> {
        val parts = mutableListOf<String>()
        // If an existing value is present, decode it as UTF-8.
        if (existingValue != null && existingValueLen != 0.asSizeT()) {
            val existingBytes = existingValue.readBytes(existingValueLen.toInt())
            parts.add(existingBytes.decodeToString())
        }
        // Decode each operand in order.
        if (operands != null && operandsLengths != null) {
            for (i in 0 until numOperands) {
                val operandPtr = operands[i]
                val operandLen = operandsLengths[i]
                if (operandPtr != null && operandLen != 0.asSizeT()) {
                    val operandBytes = operandPtr.readBytes(operandLen.toInt())
                    parts.add(operandBytes.decodeToString())
                }
            }
        }
        // Join all parts with the delimiter.
        val resultString = parts.joinToString(separator = delimiter)
        val resultBytes = resultString.encodeToByteArray()
        val resultLen = resultBytes.size.asSizeT()
        val merged = nativeHeap.allocArray<ByteVar>(resultBytes.size)
        for (i in resultBytes.indices) {
            merged[i] = resultBytes[i]
        }
        return Pair(true, Pair(merged, resultLen))
    }

    override fun partialMerge(
        key: CPointer<ByteVarOf<Byte>>?, keyLen: size_t,
        operands: CPointer<CPointerVarOf<CPointer<ByteVarOf<Byte>>>>?,
        operandsLengths: CPointer<size_tVar>?, numOperands: Int
    ): Pair<Boolean, Pair<CPointer<ByteVarOf<Byte>>?, size_t>> {
        val parts = mutableListOf<String>()
        if (operands != null && operandsLengths != null) {
            for (i in 0 until numOperands) {
                val operandPtr = operands[i]
                val operandLen = operandsLengths[i]
                if (operandPtr != null && operandLen != 0.asSizeT()) {
                    val operandBytes = operandPtr.readBytes(operandLen.toInt())
                    parts.add(operandBytes.decodeToString())
                }
            }
        }
        if (parts.isEmpty()) {
            return Pair(false, Pair(null, 0.asSizeT()))
        }
        val resultString = parts.joinToString(separator = delimiter)
        val resultBytes = resultString.encodeToByteArray()
        val resultLen = resultBytes.size.asSizeT()
        val merged = nativeHeap.allocArray<ByteVar>(resultBytes.size)

        for (i in resultBytes.indices) {
            merged[i] = resultBytes[i]
        }
        return Pair(true, Pair(merged, resultLen))
    }

    override fun name(): String {
        return "StringAppendOperator($delimiter)"
    }
}
