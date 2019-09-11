package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class Env
    protected constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle) {
    actual fun getBackgroundThreads(priority: Priority): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBackgroundThreads(number: Int, priority: Priority): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getThreadPoolQueueLen(priority: Priority): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun incBackgroundThreadsIfNeeded(number: Int, priority: Priority): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun lowerThreadPoolIOPriority(priority: Priority): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun lowerThreadPoolCPUPriority(priority: Priority): Env {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getThreadList(): List<ThreadStatus> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual fun getDefaultEnv(): Env {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
