package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class CompactionOptionsUniversal
    actual constructor()
: RocksObject(newCompactionOptionsUniversal()) {
    actual fun setSizeRatio(sizeRatio: Int): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun sizeRatio(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMinMergeWidth(minMergeWidth: Int): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun minMergeWidth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxMergeWidth(maxMergeWidth: Int): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxMergeWidth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSizeAmplificationPercent(maxSizeAmplificationPercent: Int): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSizeAmplificationPercent(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionSizePercent(compressionSizePercent: Int): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionSizePercent(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStopStyle(compactionStopStyle: CompactionStopStyle): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun stopStyle(): CompactionStopStyle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowTrivialMove(allowTrivialMove: Boolean): CompactionOptionsUniversal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowTrivialMove(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newCompactionOptionsUniversal(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
