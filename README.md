[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://img.shields.io/maven-central/v/io.maryk.rocksdb/rocksdb-android)](https://search.maven.org/artifact/io.maryk.rocksdb/rocksdb-multiplatform)

# Kotlin Multi-platform RocksDB implementation

This project provides a multi-platform Kotlin implementation for RocksDB. 
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

The dependency is published in Maven Central.

```kotlin
implementation("io.maryk.rocksdb:rocksdb-multiplatform:6.20.4")
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
