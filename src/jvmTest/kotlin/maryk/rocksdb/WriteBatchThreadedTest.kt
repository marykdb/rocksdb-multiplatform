package maryk.rocksdb

import maryk.allocateByteBuffer
import maryk.encodeToByteArray
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

@RunWith(Parameterized::class)
class WriteBatchThreadedTest {
    @Parameter @JvmField
    var threadCount: Int = 0

    @Rule @JvmField
    var dbFolder = TemporaryFolder()

    @JvmField
    var db: RocksDB? = null

    @BeforeTest
    fun setUp() {
        loadRocksDBLibrary()
        val options = Options().apply {
            setCreateIfMissing(true)
            setIncreaseParallelism(32)
        }

        db = openRocksDB(options, dbFolder.getRoot().getAbsolutePath())
        assertNotNull(db)
    }

    @AfterTest
    fun tearDown() {
        db?.close()
    }

    @Test
    fun threadedWrites() {
        val callables = mutableListOf<Callable<Void>>()
        for (i in 0..99) {
            val offset = i * 100
            callables.add(Callable<Void> {
                WriteBatch().use { wb ->
                    WriteOptions().use { w_opt ->
                        for (j in offset until offset + 100) {
                            wb.put(allocateByteBuffer(4).putInt(j).array(), "parallel rocks test".encodeToByteArray())
                        }
                        db!!.write(w_opt, wb)
                    }
                }
                null
            })
        }

        //submit the callables
        val executorService = Executors.newFixedThreadPool(threadCount)
        try {
            val completionService = ExecutorCompletionService<Void>(executorService)
            val futures = HashSet<Future<Void>>()
            for (callable in callables) {
                futures.add(completionService.submit(callable))
            }

            while (futures.size > 0) {
                val future = completionService.take()
                futures.remove(future)

                try {
                    future.get()
                } catch (e: ExecutionException) {
                    for (f in futures) {
                        f.cancel(true)
                    }

                    throw e
                }

            }
        } finally {
            executorService.shutdown()
            executorService.awaitTermination(10, TimeUnit.SECONDS)
        }
    }

    companion object {
        @JvmStatic
        @Parameters(name = "WriteBatchThreadedTest(threadCount={0})")
        fun data(): Iterable<Int> {
            return listOf(1, 10, 50, 100)
        }
    }
}
