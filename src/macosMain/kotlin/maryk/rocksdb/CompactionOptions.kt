package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class CompactionOptions actual constructor() : RocksObject(newCompactionOptions()) {
    actual fun compression(): CompressionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompression(compression: CompressionType): CompactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun outputFileSizeLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setOutputFileSizeLimit(outputFileSizeLimit: Long): CompactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSubcompactions(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSubcompactions(maxSubcompactions: Int): CompactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newCompactionOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
