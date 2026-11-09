package maryk.rocksdb

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFalse

class MultiGetConsistencyNativeTest {
    @Test fun defaultFamilyReadsOneDatabaseState() = assertConsistent(false)
    @Test fun explicitFamilyReadsOneDatabaseState() = assertConsistent(true)

    private fun assertConsistent(explicitFamily: Boolean) = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("MultiGetConsistencyNativeTest")).use { db ->
                db.getDefaultColumnFamily().use { cf ->
                    val keys = listOf("a".encodeToByteArray(), "b".encodeToByteArray())
                    val value = byteArrayOf(1)
                    db.put(cf, keys[0], value)
                    val start = CompletableDeferred<Unit>()
                    val writer = launch(Dispatchers.Default) {
                        WriteOptions().use { writeOptions ->
                            WriteBatch().use { batch ->
                                start.await()
                                var index = 0
                                while (true) {
                                    coroutineContext.ensureActive()
                                    batch.clear()
                                    batch.put(cf, keys[index % 2], value)
                                    batch.delete(cf, keys[(index + 1) % 2])
                                    db.write(writeOptions, batch)
                                    index++
                                }
                            }
                        }
                    }
                    start.complete(Unit)
                    try {
                        repeat(10_000) {
                            val values = if (explicitFamily) db.multiGetAsList(listOf(cf, cf), keys)
                            else db.multiGetAsList(keys)
                            // Every committed batch contains exactly one of these two keys.
                            assertFalse(values.all { it != null }, "MultiGet combined values from different database states")
                        }
                    } finally {
                        writer.cancelAndJoin()
                    }
                }
            }
        }
    }
}
