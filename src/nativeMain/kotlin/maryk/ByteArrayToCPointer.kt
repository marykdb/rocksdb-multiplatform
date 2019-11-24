package maryk

import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.toCValues

fun ByteArray.toCPointer() = this.toCValues().let {
    it.place(interpretCPointer(nativeHeap.alloc(it.size, it.align).rawPtr)!!)
}
