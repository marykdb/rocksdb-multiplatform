package maryk.rocksdb

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
            assertEquals(StatusCode.NotSupported, e.getStatus()?.getCode())
            assertEquals(StatusSubCode.None, e.getStatus()?.getSubCode())
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
            assertEquals(StatusCode.NotSupported, e.getStatus()?.getCode())
            assertEquals(StatusSubCode.None, e.getStatus()?.getSubCode())
            assertNull(e.getStatus()?.getState())
            assertEquals(StatusCode.NotSupported.name, e.message)
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
            assertEquals(StatusCode.TimedOut, e.getStatus()?.getCode())
            assertEquals(StatusSubCode.LockTimeout, e.getStatus()?.getSubCode())
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
            assertEquals(StatusCode.TimedOut, e.getStatus()?.getCode())
            assertEquals(StatusSubCode.LockTimeout, e.getStatus()?.getSubCode())
            assertNull(e.getStatus()?.getState())
            assertEquals("${StatusCode.TimedOut.name}(${StatusSubCode.LockTimeout.name})", e.message)
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
            assertEquals(StatusCode.NotSupported, e.getStatus()?.getCode())
            assertEquals(StatusSubCode.None, e.getStatus()?.getSubCode())
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
        throw RocksDBException("test message", Status(StatusCode.NotSupported, StatusSubCode.None, null))
    }

    private fun raiseExceptionNoMsgWithStatusCode() {
        throw RocksDBException(Status(StatusCode.NotSupported, StatusSubCode.None, null))
    }

    private fun raiseExceptionWithStatusCodeSubCode() {
        throw RocksDBException("test message", Status(StatusCode.TimedOut, StatusSubCode.LockTimeout, null))
    }

    private fun raiseExceptionNoMsgWithStatusCodeSubCode() {
        throw RocksDBException(Status(StatusCode.TimedOut, StatusSubCode.LockTimeout, null))
    }

    private fun raiseExceptionWithStatusCodeState() {
        throw RocksDBException("test message", Status(StatusCode.NotSupported, StatusSubCode.None, "test state"))
    }
}
