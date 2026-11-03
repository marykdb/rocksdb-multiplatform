package maryk.rocksdb

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

actual fun RocksDB.effectiveDBOptions(): Map<String, String> {
    check(isOwningHandle()) { "Database handle is closed or borrowed" }
    return effectiveOptionSnapshot(Jni1181OptionStrings.database(nativeHandle), effectiveDBOptionKeys)
}

actual fun RocksDB.effectiveColumnFamilyOptions(columnFamilyHandle: ColumnFamilyHandle?): Map<String, String> {
    check(isOwningHandle()) { "Database handle is closed or borrowed" }
    columnFamilyHandle?.let { check(it.isOwningHandle()) { "Column family handle is closed" } }
    return effectiveOptionSnapshot(
        Jni1181OptionStrings.columnFamily(nativeHandle, columnFamilyHandle?.nativeHandle ?: 0L),
        effectiveColumnFamilyOptionKeys,
    )
}

/**
 * These are the existing serialized-option JNI entry points in the pinned 11.8.1
 * artifacts. Bypass Java's mutable-option utility parser: it rejects the engine's
 * unsigned representation of the supported -2 compaction-age sentinel. Handles
 * come from the public RocksObject getter; no private fields or new JNI are used.
 */
private object Jni1181OptionStrings {
    private val databaseMethod: Method by lazy {
        RocksDB::class.java.getDeclaredMethod("getDBOptions", java.lang.Long.TYPE).apply { isAccessible = true }
    }
    private val columnFamilyMethod: Method by lazy {
        RocksDB::class.java.getDeclaredMethod("getOptions", java.lang.Long.TYPE, java.lang.Long.TYPE)
            .apply { isAccessible = true }
    }

    fun database(databaseHandle: Long): String = invoke(databaseMethod, databaseHandle)
    fun columnFamily(databaseHandle: Long, familyHandle: Long): String =
        invoke(columnFamilyMethod, databaseHandle, familyHandle)

    private fun invoke(method: Method, vararg handles: Long): String = try {
        method.invoke(null, *handles.toTypedArray()) as String
    } catch (error: InvocationTargetException) {
        throw error.cause ?: error
    }
}
