package maryk.rocksdb

import kotlin.test.Test

class CompressionTypesTest {
    @Test
    fun getCompressionType() {
        for (compressionType in CompressionType.values()) {
            val libraryName = compressionType.getLibraryName()
            compressionType == getCompressionType(
                libraryName
            )
        }
    }
}
