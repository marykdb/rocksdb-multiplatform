package maryk.rocksdb

import maryk.rocksdb.StatusCode.NotSupported
import maryk.rocksdb.StatusCode.TimedOut
import maryk.rocksdb.StatusSubCode.LockTimeout
import maryk.rocksdb.StatusSubCode.None
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class RocksDBExceptionTest {
    @Test
    fun exception() {
        try {
            raiseException()
        } catch (e: RocksDBException) {
            assertNull(e.getStatus())
            assertEquals("test message", e.message)
            return
        }

        fail()
    }

    @Test
    fun exceptionWithStatusCode() {
        try {
            raiseExceptionWithStatusCode()
        } catch (e: RocksDBException) {
            assertNotNull(e.getStatus())
            assertEquals(NotSupported, e.getStatus()?.getCode())
            assertEquals(None, e.getStatus()?.getSubCode())
            assertNull(e.getStatus()?.getState())
            assertEquals("test message", e.message)
            return
        }

        fail()
    }

    @Test
    fun exceptionNoMsgWithStatusCode() {
        try {
            raiseExceptionNoMsgWithStatusCode()
        } catch (e: RocksDBException) {
            assertNotNull(e.getStatus())
            assertEquals(NotSupported, e.getStatus()?.getCode())
            assertEquals(None, e.getStatus()?.getSubCode())
            assertNull(e.getStatus()?.getState())
            assertEquals(NotSupported.name, e.message)
            return
        }

        fail()
    }

    @Test
    fun exceptionWithStatusCodeSubCode() {
        try {
            raiseExceptionWithStatusCodeSubCode()
        } catch (e: RocksDBException) {
            assertNotNull(e.getStatus())
            assertEquals(TimedOut, e.getStatus()?.getCode())
            assertEquals(LockTimeout, e.getStatus()?.getSubCode())
            assertNull(e.getStatus()?.getState())
            assertEquals("test message", e.message)
            return
        }

        fail()
    }

    @Test
    fun exceptionNoMsgWithStatusCodeSubCode() {
        try {
            raiseExceptionNoMsgWithStatusCodeSubCode()
        } catch (e: RocksDBException) {
            assertNotNull(e.getStatus())
            assertEquals(TimedOut, e.getStatus()?.getCode())
            assertEquals(LockTimeout, e.getStatus()?.getSubCode())
            assertNull(e.getStatus()?.getState())
            assertEquals("${TimedOut.name}(${LockTimeout.name})", e.message)
            return
        }

        fail()
    }

    @Test
    fun exceptionWithStatusCodeState() {
        try {
            raiseExceptionWithStatusCodeState()
        } catch (e: RocksDBException) {
            assertNotNull(e.getStatus())
            assertEquals(NotSupported, e.getStatus()?.getCode())
            assertEquals(None, e.getStatus()?.getSubCode())
            assertNotNull(e.getStatus()?.getState())
            assertEquals("test message", e.message)
            return
        }

        fail()
    }

    private fun raiseException() {
        throw RocksDBException("test message")
    }

    private fun raiseExceptionWithStatusCode() {
        throw RocksDBException("test message", Status(NotSupported, None, null))
    }

    private fun raiseExceptionNoMsgWithStatusCode() {
        throw RocksDBException(Status(NotSupported, None, null))
    }

    private fun raiseExceptionWithStatusCodeSubCode() {
        throw RocksDBException("test message", Status(TimedOut, LockTimeout, null))
    }

    private fun raiseExceptionNoMsgWithStatusCodeSubCode() {
        throw RocksDBException(Status(TimedOut, LockTimeout, null))
    }

    private fun raiseExceptionWithStatusCodeState() {
        throw RocksDBException("test message", Status(NotSupported, None, "test state"))
    }
}
