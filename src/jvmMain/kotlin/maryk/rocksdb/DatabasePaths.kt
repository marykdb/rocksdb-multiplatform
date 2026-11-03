@file:Suppress("NewApi") // Every Path call is guarded by requireDatabasePathApi().

package maryk.rocksdb

import java.nio.file.Paths

actual fun Options.configureDbPaths(paths: List<DatabasePath>): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    requireDatabasePathApi()
    return setDbPaths(paths.map { org.rocksdb.DbPath(Paths.get(it.path), it.targetSize) })
}

actual fun DBOptions.configureDbPaths(paths: List<DatabasePath>): DBOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    requireDatabasePathApi()
    return setDbPaths(paths.map { org.rocksdb.DbPath(Paths.get(it.path), it.targetSize) })
}

actual fun Options.configureCfPaths(paths: List<DatabasePath>): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    requireDatabasePathApi()
    return setCfPaths(paths.map { org.rocksdb.DbPath(Paths.get(it.path), it.targetSize) })
}

actual fun ColumnFamilyOptions.configureCfPaths(paths: List<DatabasePath>): ColumnFamilyOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    requireDatabasePathApi()
    return setCfPaths(paths.map { org.rocksdb.DbPath(Paths.get(it.path), it.targetSize) })
}

/** Keep the JVM/Android shared source usable below the Java Path API's Android introduction. */
private fun requireDatabasePathApi() {
    if (System.getProperty("java.vm.name") == "Dalvik") {
        val version = Class.forName("android.os.Build\$VERSION").getField("SDK_INT").getInt(null)
        require(version >= 26) { "Database path configuration requires Android API 26 or newer" }
    }
}
