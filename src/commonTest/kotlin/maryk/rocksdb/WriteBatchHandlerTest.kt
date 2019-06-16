package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.CapturingWriteBatchHandler
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.DELETE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.LOG
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MERGE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.PUT
import maryk.rocksdb.util.CapturingWriteBatchHandler.Event
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class WriteBatchHandlerTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun writeBatchHandler() {
        // setup test data
        val testEvents = listOf(
            Event(DELETE, "k0".encodeToByteArray(), null),
            Event(PUT, "k1".encodeToByteArray(), "v1".encodeToByteArray()),
            Event(PUT, "k2".encodeToByteArray(), "v2".encodeToByteArray()),
            Event(PUT, "k3".encodeToByteArray(), "v3".encodeToByteArray()),
            Event(LOG, null, "log1".encodeToByteArray()),
            Event(MERGE, "k2".encodeToByteArray(), "v22".encodeToByteArray()),
            Event(DELETE, "k3".encodeToByteArray(), null)
        )

        // load test data to the write batch
        WriteBatch().use { batch ->
            for (testEvent in testEvents) {
                when (testEvent.action) {
                    PUT -> batch.put(testEvent.key!!, testEvent.value!!)
                    MERGE -> batch.merge(testEvent.key!!, testEvent.value!!)
                    DELETE -> batch.delete(testEvent.key!!)
                    LOG -> batch.putLogData(testEvent.value!!)
                    else -> { /* Not needed */ }
                }
            }

            // attempt to read test data back from the WriteBatch by iterating
            // with a handler
            CapturingWriteBatchHandler().use { handler ->
                batch.iterate(handler)

                // compare the results to the test data
                val actualEvents = handler.getEvents()
                assertSame(testEvents.size, actualEvents.size)

                assertEquals(testEvents, actualEvents)
            }
        }
    }
}
