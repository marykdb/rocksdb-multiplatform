[ ![Download](https://api.bintray.com/packages/maryk/maven/rocksdb-multiplatform/images/download.svg) ](https://bintray.com/maryk/maven/rocksdb-multiplatform/_latestVersion)

# Kotlin Multi-platform RocksDB implementation

This projects provides a multi-platform Kotlin implemententation for RocksDB. 
It supports the JVM, Android and native macOS/iOS.

## RocksDB API Support

While the JVM and Android platforms support the full RocksDB Java interfaces
the iOS/macOS native platforms are not yet fully covered. The common library is
thus limited to what is supported on iOS and Mac. It contains most common operations 
and additions can always be requested in an issue or added with an MR.

## Native platforms

Currently, only Apple platforms are supported for Native compilations. This is
because the implementation depends on [ObjectiveRocks](https://github.com/marykdb/ObjectiveRocks)
which is an Objective-C implementation. Kotlin currently only supports 
Objective-C and C interop but the C API of RocksDB is too limited for
current use case. When a C++ interop is introduced a more native interop can
be created to support more platforms. 

## Reference

- [API reference](src/commonMain/kotlin/maryk/rocksdb)
- [RocksDB](https://rocksdb.org)

## Gradle Dependency

```kotlin
implementation("io.maryk.rocksdb:rocksdb-multiplatform:0.6.2")
```

## Usage Example

Opening a RocksDB, writing a key and value pair and getting a value by that key.
```kotlin
openRocksDB("path_to_store_on_disk").use { db ->
    val key = "test".encodeToByteArray()
    db.put(key, "value".encodeToByteArray())
    
    val value = db.get("test".encodeToByteArray())
    
    println(value?.decodeToString())
}
```

Check for more usage examples the [tests](src/commonTest/kotlin/maryk/rocksdb). 
