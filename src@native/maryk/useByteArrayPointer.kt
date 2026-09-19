@file:OptIn(UnsafeNumber::class)

package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned

internal inline fun <R> ByteArray.usePointer(block: (CPointer<ByteVar>) -> R): R = memScoped {
    if (isEmpty()) {
        block(allocArray<ByteVar>(1))
    } else {
        this@usePointer.usePinned { pinned ->
            block(pinned.addressOf(0))
        }
    }
}

internal inline fun <R> usePointers(
    first: ByteArray,
    second: ByteArray,
    block: (firstPointer: CPointer<ByteVar>, secondPointer: CPointer<ByteVar>) -> R,
): R = first.usePointer { firstPointer ->
    second.usePointer { secondPointer ->
        block(firstPointer, secondPointer)
    }
}
