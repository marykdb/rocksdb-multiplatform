[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://img.shields.io/maven-central/v/io.maryk.rocksdb/rocksdb-multiplatform)](https://central.sonatype.com/artifact/io.maryk.rocksdb/rocksdb-multiplatform)

# Kotlin Multiplatform bindings for RocksDB

This project provides Kotlin Multiplatform bindings for RocksDB, a high-performance embedded key-value store for
storage of data on disk. It uses the official native RocksDB
implementation and exposes it through a Kotlin API, with prebuilt native binaries for the supported targets. Those
native binaries are built separately in [build-rocksdb](https://github.com/marykdb/build-rocksdb).

The aim of this project is to make RocksDB usable from shared Kotlin code across JVM, Android, and native
Linux/macOS/iOS/tvOS/watchOS/Windows targets. On JVM and Android it bridges the RocksDB Java API. On Kotlin/Native
targets it calls the native RocksDB library through Kotlin/Native interop.

This project is useful for developers who want to build multiplatform applications that require high-performance disk-based
storage. Because the storage engine is still upstream RocksDB, performance should be close to RocksDB itself, apart from
the expected JNI or native interop overhead depending on the target and usage pattern.

## RocksDB API Support

The project supports the full RocksDB Java interfaces on the JVM and Android platforms.
The common Kotlin API includes most common operations, but if you need additional features, you can request
them by creating an issue or submitting a merge request.

## Supported platforms 

- **JVM** - linux32, linux32-musl, linux64, linux64-musl, macOS, win64 (Same as RocksDBJava) 
- **Android** - arm64-v8a, armeabi-v7a, x86, x86_64 (Default Android native build targets)

Kotlin Native:
- **macOS**: macosArm64 & macosX64
- **iOS**: iosArm64 & iosSimulatorArm64
- **watchOS**: watchosArm64, watchosDeviceArm64 & watchosSimulatorArm64
- **tvOS**: tvosArm64 & tvosSimulatorArm64
- **Linux**: linuxX64 & linuxArm64
- **Windows**: mingwX64
- **Android**: androidNativeArm32, androidNativeArm64, androidNativeX86 & androidNativeX64

## Reference

You can refer to the [API reference](src/commonMain/kotlin/maryk/rocksdb) or the official [RocksDB website](https://rocksdb.org) for more information.

## Gradle Dependency

The dependency is published in Maven Central, so you can easily add it to your project:

```kotlin
implementation("io.maryk.rocksdb:rocksdb-multiplatform:10.10.1.3")
```

## Usage Example

Here's an example of how to open a RocksDB database, write a key-value pair and retrieve the value by key:
```kotlin
openRocksDB("path_to_store_on_disk").use { db ->
    val key = "test".encodeToByteArray()
    db.put(key, "value".encodeToByteArray())
    
    val value = db.get("test".encodeToByteArray())
    
    println(value?.decodeToString())
}
```

Check out the [tests](src/commonTest/kotlin/maryk/rocksdb) for more examples on how to use this library.

## Contributing

We welcome contributions to the project! If you find a bug or want to suggest a new feature, please submit an issue or 
submit a pull request.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE file](LICENSE) for details.
