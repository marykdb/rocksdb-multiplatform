package maryk.rocksdb

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertNull

class NativeLifecycleGuardTest {
    @Test
    fun guardIsReentrant() {
        val guard = NativeLifecycleGuard()

        guard.withLock {
            guard.withLock { }
        }
    }

    @Test
    fun guardBlocksOtherThreadsUntilOwnerLeaves() = runBlocking {
        val guard = NativeLifecycleGuard()
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val acquired = CompletableDeferred<Unit>()

        val owner = launch(Dispatchers.Default) {
            guard.withLock {
                entered.complete(Unit)
                runBlocking { release.await() }
            }
        }
        entered.await()

        val contender = launch(Dispatchers.Default) {
            guard.withLock { acquired.complete(Unit) }
        }

        assertNull(withTimeoutOrNull(100) { acquired.await() })
        release.complete(Unit)
        withTimeout(5_000) { acquired.await() }
        owner.join()
        contender.join()
    }
}
