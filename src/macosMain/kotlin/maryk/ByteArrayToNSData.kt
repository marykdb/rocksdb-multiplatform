package maryk

import platform.Foundation.NSData
import platform.Foundation.NSMakeRange
import platform.Foundation.create
import platform.Foundation.subdataWithRange

fun ByteArray.toNSData() = NSData.create(
    bytesNoCopy = this.toCPointer(),
    length = this.size.toULong()
)

fun ByteArray.toNSData(
    offset: Int,
    length: Int
) = this.toNSData().subdataWithRange(NSMakeRange(offset.toULong(), length.toULong()))
