package maryk.rocksdb

import maryk.rocksdb.MemtableOption.write_buffer_size
import maryk.rocksdb.MiscOption.paranoid_file_checks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MutableColumnFamilyOptionsTest {
    @Test
    fun builder() {
        val builder = mutableColumnFamilyOptionsBuilder().apply {
            setWriteBufferSize(10)
            setInplaceUpdateNumLocks(5)
            setDisableAutoCompactions(true)
            setParanoidFileChecks(true)
        }

        assertEquals(10, builder.writeBufferSize())
        assertEquals(5, builder.inplaceUpdateNumLocks())
        assertEquals(true, builder.disableAutoCompactions())
        assertEquals(true, builder.paranoidFileChecks())
    }

    @Test
    fun builder_getWhenNotSet() {
        val builder = mutableColumnFamilyOptionsBuilder()

        assertFailsWith<NoSuchElementException> {
            builder.writeBufferSize()
        }
    }

    @Test
    fun builder_build() {
        val options = mutableColumnFamilyOptionsBuilder()
            .setWriteBufferSize(10)
            .setParanoidFileChecks(true)
            .build()

        assertEquals(2, options.getKeys().size)
        assertEquals(2, options.getValues().size)
        assertEquals(write_buffer_size.name, options.getKeys()[0])
        assertEquals("10", options.getValues()[0])
        assertEquals(paranoid_file_checks.name, options.getKeys()[1])
        assertEquals("true", options.getValues()[1])
    }

    @Test
    fun mutableColumnFamilyOptions_toString() {
        val str = mutableColumnFamilyOptionsBuilder()
            .setWriteBufferSize(10)
            .setInplaceUpdateNumLocks(5)
            .setDisableAutoCompactions(true)
            .setParanoidFileChecks(true)
            .build()
            .toString()

        assertEquals("write_buffer_size=10;inplace_update_num_locks=5;disable_auto_compactions=true;paranoid_file_checks=true", str)
    }

    @Test
    fun mutableColumnFamilyOptions_parse() {
        val str =
            "write_buffer_size=10;inplace_update_num_locks=5;disable_auto_compactions=true;paranoid_file_checks=true"

        val builder = mutableColumnFamilyOptionsParse(str)

        assertEquals(10, builder.writeBufferSize())
        assertEquals(5, builder.inplaceUpdateNumLocks())
        assertTrue(builder.disableAutoCompactions())
        assertTrue(builder.paranoidFileChecks())
    }
}
