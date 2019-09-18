package maryk.rocksdb

import kotlinx.cinterop.CPointer
import rocksdb.rocksdb_envoptions_create

actual class EnvOptions private constructor(nativeHandle: CPointer<*>) : RocksObject(nativeHandle) {
    actual constructor() : this(newEnvOptions())

    actual constructor(dbOptions: DBOptions): this(newEnvOptions(dbOptions.nativeHandle)) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseMmapReads(useMmapReads: Boolean): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useMmapReads(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseMmapWrites(useMmapWrites: Boolean): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useMmapWrites(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseDirectReads(useDirectReads: Boolean): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useDirectReads(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setUseDirectWrites(useDirectWrites: Boolean): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun useDirectWrites(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAllowFallocate(allowFallocate: Boolean): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun allowFallocate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSetFdCloexec(setFdCloexec: Boolean): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setFdCloexec(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBytesPerSync(bytesPerSync: Long): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bytesPerSync(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setFallocateWithKeepSize(fallocateWithKeepSize: Boolean): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun fallocateWithKeepSize(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionReadaheadSize(compactionReadaheadSize: Long): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionReadaheadSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRandomAccessMaxBufferSize(randomAccessMaxBufferSize: Long): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun randomAccessMaxBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writableFileMaxBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRateLimiter(rateLimiter: RateLimiter): EnvOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun rateLimiter(): RateLimiter? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

private fun newEnvOptions() = rocksdb_envoptions_create()!!

private fun newEnvOptions(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
