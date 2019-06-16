package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class WALRecoveryModeTest {
    @Test
    fun getWALRecoveryMode() {
        for (walRecoveryMode in WALRecoveryMode.values()) {
            assertEquals(walRecoveryMode, getWALRecoveryMode(walRecoveryMode.getValue()))
        }
    }
}
