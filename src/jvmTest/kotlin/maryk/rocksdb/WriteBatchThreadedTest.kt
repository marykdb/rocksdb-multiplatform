package maryk.rocksdb

import maryk.allocateByteBuffer
import org.rocksdb.WriteBatch
import org.rocksdb.WriteOptions
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class WriteBatchThreadedTest {
    private var db: RocksDB? = null
    private lateinit var dbFolder: File

    @BeforeTest
    fun setUp() {
        loadRocksDBLibrary()
        dbFolder = createTempDirectory(prefix = "rocksdbTest").toFile()
        val options = maryk.rocksdb.Options().apply {
            setCreateIfMissing(true)
            setIncreaseParallelism(32)
        }
        db = openRocksDB(options, dbFolder.absolutePath)
        assertNotNull(db)
    }

    @AfterTest
    fun tearDown() {
        db?.close()
        dbFolder.deleteRecursively()
    }

    @Test
    fun threadedWrites() {
        val threadCounts = listOf(1, 10, 50, 100)
        for (threadCount in threadCounts) {
            val callables = mutableListOf<Callable<Void>>()
            for (i in 0..99) {
                val offset = i * 100
                callables.add(Callable<Void> {
                    WriteBatch().use { wb ->
                        WriteOptions().use { wOpt ->
                            for (j in offset until offset + 100) {
                                allocateByteBuffer(4) { buffer ->
                                    wb.put(
                                        buffer.putInt(j).array(),
                                        "parallel rocks test".encodeToByteArray()
                                    )
                                }
                            }
                            db!!.write(wOpt, wb)
                        }
                    }
                    null
                })
            }

            val executorService = Executors.newFixedThreadPool(threadCount)
            try {
                val completionService = ExecutorCompletionService<Void>(executorService)
                val futures = HashSet<Future<Void>>()
                for (callable in callables) {
                    futures.add(completionService.submit(callable))
                }

                while (futures.isNotEmpty()) {
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
    }
}
