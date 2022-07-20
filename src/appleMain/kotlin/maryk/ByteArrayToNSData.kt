package maryk

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.Foundation.NSData
import platform.Foundation.NSMakeRange
import platform.Foundation.create
import platform.Foundation.subdataWithRange

fun ByteArray.toNSData() = memScoped {
    NSData.create(
        bytes = toCValues().getPointer(this),
        length = size.toULong()
    )
}

fun ByteArray.toNSData(
    offset: Int,
    length: Int
) = this.toNSData().subdataWithRange(NSMakeRange(offset.toULong(), length.toULong()))
