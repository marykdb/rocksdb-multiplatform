[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://img.shields.io/maven-central/v/io.maryk.rocksdb/rocksdb-multiplatform)](https://central.sonatype.com/artifact/io.maryk.rocksdb/rocksdb-multiplatform)

# Kotlin Multiplatform bindings for RocksDB

Kotlin Multiplatform bindings for [RocksDB](https://rocksdb.org), an embedded key-value store for disk-based storage.
Use a shared Kotlin API across JVM, Android, and Kotlin/Native targets.

JVM and Android use the RocksDB Java API. Kotlin/Native uses prebuilt native binaries from
[build-rocksdb](https://github.com/marykdb/build-rocksdb).

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

## Gradle Dependency

The dependency is published in Maven Central. Set `rocksdbVersion` to the version you want to use:

```kotlin
implementation("io.maryk.rocksdb:rocksdb-multiplatform:$rocksdbVersion")
```

## Usage Example

Here's an example of how to open a RocksDB database, write a key-value pair and retrieve the value by key:
```kotlin
openRocksDB("path_to_store_on_disk").use { db ->
    val key = "test".encodeToByteArray()
    db.put(key, "value".encodeToByteArray())

    val value = db.get(key)

    println(value?.decodeToString())
}
```

## Documentation

- [API source](src/commonMain/kotlin/maryk/rocksdb)
- [Configuration guide](docs/configuration.md) — filters, caches, merge operators, compression, and logging
- [Tests and examples](src/commonTest/kotlin/maryk/rocksdb)

## Contributing

We welcome contributions to the project! If you find a bug or want to suggest a new feature, please submit an issue or
submit a pull request.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE file](LICENSE) for details.
