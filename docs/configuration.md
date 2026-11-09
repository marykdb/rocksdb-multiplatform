# Common configuration examples

This guide covers options structure, resource ownership, and selected configuration patterns in the
common Kotlin API. See the [API source](../src/commonMain/kotlin/maryk/rocksdb) for individual settings.
Examples assume imports from `maryk.rocksdb`.

## Choosing options

| Type | Purpose |
| --- | --- |
| `Options` | Database and default column-family settings for `openRocksDB(options, path)`. |
| `DBOptions` | Database-wide settings when opening with explicit column-family descriptors. |
| `ColumnFamilyOptions` | Settings for one column family, supplied through `ColumnFamilyDescriptor(name, options)`. |
| `BlockBasedTableConfig` | SST table settings, attached to `Options` or `ColumnFamilyOptions` with `setTableFormatConfig(...)`. |

Configure options before opening the database. For a new database, explicitly call
`setCreateIfMissing(true)` when supplying your own options. When opening with descriptors,
`DBOptions.setCreateMissingColumnFamilies(true)` also allows missing column families to be created.
Read-write opens must include descriptors for every existing column family, including the default.
See [ResourceConfigurationTest](../src/commonTest/kotlin/maryk/rocksdb/ResourceConfigurationTest.kt)
for an example using `DBOptions`, descriptors, and column-family handles.

A table configuration is applied when `setTableFormatConfig(...)` creates a table factory. Changing
the configuration object afterward does not update factories already configured from it. Configure
it first, then attach it to the relevant options.

## Resource ownership

Use nested `use` blocks to close the database before its options, and the options before any
caller-owned resources they reference. If a resource is shared across several databases, keep it
open until all those databases and their options have closed.

| Resource | Lifetime and ownership |
| --- | --- |
| Block/row caches, Bloom filters, loggers | Keep open until the databases and options using them have closed. |
| Rate limiters, compaction limiters, SST partitioner factories | Caller-owned resources; keep them outside the options and database `use` blocks. |
| Persistent caches | Factories retain shared ownership of the cache. Its environment and logger must outlive all using databases, options, and caches, even if the caller's cache handle has already closed. |
| Comparators on Native | Options and database/SST/index owners retain separate references. Reusing options preserves the comparator; the native callback is freed after the final owner closes. |
| Merge operators on Native | Attaching an operator transfers ownership to RocksDB. Use a fresh operator instance for each options instance; do not reattach it to other options. |

`BlockBasedTableConfig` is a configuration wrapper and does not need a `use` block. Its filters and
caches have their own lifetimes, as shown below.

## Block cache

Attach an in-memory block cache through `BlockBasedTableConfig`. The capacity is in bytes; the
64 MiB value below is an example, not a workload-specific recommendation.

```kotlin
LRUCache(64L * 1024 * 1024).use { cache ->
    val tableConfig = BlockBasedTableConfig().setBlockCache(cache)
    Options().setCreateIfMissing(true).setTableFormatConfig(tableConfig).use { options ->
        openRocksDB(options, "path_to_store_on_disk").use { db ->
            val key = "key".encodeToByteArray()
            db.put(key, "value".encodeToByteArray())
            println(db.get(key)?.decodeToString())
        }
    }
}
```

The cache can be shared across table configurations. For a specific column family, attach its
`BlockBasedTableConfig` through `ColumnFamilyOptions.setTableFormatConfig(...)` instead.

`Options.setRowCache(...)` and `DBOptions.setRowCache(...)` configure a separate lookup cache.
`BlockBasedTableConfig.setPersistentCache(...)` configures caching of SST pages on persistent media.
Create a persistent cache with `PersistentCache(env, path, size, logger, optimizedForNvm)` and keep
its environment and logger open according to the ownership rules above. Passing `null` to
`setPersistentCache(...)` detaches it from subsequently configured factories; existing factories
retain their cache. See [PersistentCacheTest](../src/commonTest/kotlin/maryk/rocksdb/PersistentCacheTest.kt)
for a complete setup and factory replacement example.

## Bloom filters

Bloom filters for block-based SST files can be configured from common Kotlin code:

```kotlin
BloomFilter(9.9).use { filter ->
    val tableConfig = BlockBasedTableConfig().setFilterPolicy(filter)
    Options().setCreateIfMissing(true).setTableFormatConfig(tableConfig).use { options ->
        openRocksDB(options, "path_to_store_on_disk").use { db ->
            // Reads automatically consult filters in SST files written with this configuration.
        }
    }
}
```

The same filter can be reused across options. Keep it open until those options and their databases
have closed. Use `setFilterPolicy(null)` to disable filters for subsequently configured table factories;
existing SST files keep their stored filters.

## Merge operators, compression, and index search

- `Options.setMergeOperator(...)` configures the default column family's merge operator;
  `ColumnFamilyOptions.setMergeOperator(...)` configures an explicit column family. Both also expose
  `setMergeOperatorName(...)` to select an operator by name. Observe the Native ownership rule above
  when passing an operator instance.
- `Options.setCompressionPerLevel(...)` and `ColumnFamilyOptions.setCompressionPerLevel(...)`
  configure compression by level. Pass an empty list to restore the global compression setting.
- `BlockBasedTableConfig.setIndexSearchType(...)` accepts `IndexSearchType.kBinary`,
  `IndexSearchType.kInterpolation`, or `IndexSearchType.kAuto`. Interpolation and automatic selection
  require the bytewise comparator.

## Shared resource controls

These controls let databases or column families share a resource budget or partitioning policy:

| Control | Attach with | Purpose |
| --- | --- | --- |
| `RateLimiter` | `Options.setRateLimiter(...)` or `DBOptions.setRateLimiter(...)` | Share an I/O rate budget. |
| `ConcurrentTaskLimiterImpl` | `Options.setCompactionThreadLimiter(...)` or `ColumnFamilyOptions.setCompactionThreadLimiter(...)` | Share a compaction concurrency budget. |
| `SstPartitionerFixedPrefixFactory` | `Options.setSstPartitionerFactory(...)` or `ColumnFamilyOptions.setSstPartitionerFactory(...)` | Keep keys with the same byte prefix together when choosing SST boundaries during compaction. |

Keep these resources open until all using databases and options have closed.
[ResourceConfigurationTest](../src/commonTest/kotlin/maryk/rocksdb/ResourceConfigurationTest.kt)
shows their construction and use, including changing a rate limit and a compaction concurrency limit.

## Logging

Use `Options.setLogger(...)` or `DBOptions.setLogger(...)` for a shared callback logger:

```kotlin
val logger = createLogger(InfoLogLevel.INFO_LEVEL) { level, message ->
    println("$level: $message")
}
logger.use {
    Options().setCreateIfMissing(true).use { options ->
        options.setLogger(logger)
        openRocksDB(options, "path_to_store_on_disk").use { db ->
            // Logger callbacks can run on background threads.
        }
    }
}
```

Callbacks passed to `createLogger` must be thread-safe because RocksDB can call them on background
threads. Callback exceptions are contained before returning to RocksDB. Keep the logger open until
all using databases and options have closed, and until any persistent caches using it have closed.
