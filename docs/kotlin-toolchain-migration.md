# Kotlin Toolchain migration

The project now builds with **Kotlin Toolchain 0.12.2**, using Kotlin **2.4.20**.
The initial migration gaps were resolved with project-level workarounds and
verified tests. No library implementation or test assertions were changed.

The official distribution is checksum-pinned in `scripts/toolchain/kotlin` and
`kotlin.bat`. The root launchers add the compatibility behavior below. Python
3.9+ is required. Run `./kotlin show tasks` once before importing the project
into an IDE, because native test settings contain generated absolute paths.

## Preserved configuration

- All 18 targets: JVM, Android, four Android Native targets, two iOS targets,
  two macOS targets, two Linux targets, Windows, two tvOS targets, and three
  watchOS targets.
- Existing library/dependency versions, JVM release 11, Kotlin language/API 2.4,
  progressive mode, native opt-ins, static libraries, and platform linker options.
- The RocksDB prebuilt release and all 16 SHA-256 pins. The local Kotlin plugin
  downloads and checks archives, rejects extraction outside its output directory,
  removes obsolete headers/libraries, and generates target-specific cinterop
  definitions. Archive and custom-header hashes invalidate cinterop outputs.
- Publication coordinates `io.maryk.rocksdb:rocksdb-multiplatform:10.10.1.5-SNAPSHOT`,
  exported platform dependencies, sources, native bindings, and mandatory POM
  metadata. Toolchain's POM schema does not expose the former optional
  `inceptionYear` or license `distribution` fields.

Source sets use Toolchain's `src`, `src@<platform>`, `test`, and
`test@<platform>` layout. The `jvmAndAndroid` alias replaces the absolute Android
source symlink. Of 552 relocated Kotlin source/test files, only the Apple test
folder helper changed, to configure the test process's file limit.

## Workarounds implemented

### Native test linking

Toolchain 0.12.2 passes friend-module paths when compiling native tests, but
[omits them when linking](https://github.com/JetBrains/kotlin-toolchain/blob/82a15324c05a4f1dc55b4e994863c665817f4729/sources/amper-cli/src/org/jetbrains/amper/tasks/native/NativeLinkTask.kt).
This caused three internal compaction-filter override tests to fail, even though
the same sources passed with Gradle.

The launcher generates `.toolchain/local.module-template.yaml` with the main
Debug klib's absolute friend path for each native test target. Absolute paths
are necessary because the compiler runs from its distribution directory.
The three failing tests and the full native suites pass with this change.
`check` and `link-tests` select Debug test binaries.

### Simulator execution and test resources

The upstream 0.12.2 runner launches tvOS/watchOS binaries as host processes.
`./kotlin check <targets>` instead links simulator tests, selects an available
matching simulator, waits for boot, runs through `xcrun simctl spawn`, and
propagates failure. It shuts down only a simulator it started, including after
a failed test. Database directories are recreated and removed between targets.

Simulator launchd's file-descriptor limit was too low for the existing race test
that opens 1,024 WAL iterators. The Apple test folder helper raises only the test
process's soft limit to 4,096, within its hard limit. The stress assertions and
library code remain intact.

### Android

Toolchain's delegated Android build requested `android-36.0` for API 36 with
its default minor version. Explicit compile SDK **36.1** resolves that mismatch.
Minimum SDK remains **21**. Android host tests and AAR packaging were verified.

### Checksum maintenance and publishing

`./kotlin do updateRocksdbShas` downloads every configured archive and updates
`rocksdb-prebuilts.properties` only after all checksums and property names have
been checked. It is an explicit maintenance command, not a build dependency.

`./kotlin publish mavenLocal` publishes only the library module, without signing.
`./kotlin publish mavenCentral` enables signing and Maven Central configuration;
Central's manual approval mode is retained. Credentials use the Toolchain's
standard environment variables or the ignored `local.properties` documented in
the README. The launcher adapts the previous keyring/key-ID configuration through
an isolated GPG export, or reads an ASCII-armored `signing.keyFile`; environment
variables take precedence. Credentials are never written into generated YAML.
The build plugin is excluded from the default publication command.

## Verification on 2026-09-19

| Check | Result |
| --- | --- |
| Final checkout: JVM | 502 passed |
| Final checkout: Android host | 502 passed |
| Final checkout: macOS ARM64 | 535 passed |
| iOS, tvOS, watchOS ARM64 simulators | 535 passed on each in the isolated migration checkout |
| Final project launcher: tvOS simulator | 535 passed; simulator and database cleanup completed |
| Final checkout: Apple device links | iOS ARM64, tvOS ARM64, watchOS ARM64, watchOS device ARM64 all linked |
| Archive plugin tests | 4 passed: extraction/invalidations, checksum rejection, traversal rejection, all-or-nothing checksum update |
| Python launcher and credential tests | 10 passed: launcher lifecycle, properties parsing, environment precedence, secret isolation, and encrypted GPG keyring export/signing |
| Existing local credentials | Loaded successfully; original properties/key files unchanged; no publication invoked |
| Local publication | All 18 platforms plus root metadata published to an isolated temporary Maven repository |
| Published artifact inspection | 19 module metadata files, referenced artifact sizes, all 16 embedded native RocksDB libraries, Android classes, and coordinates checked |
| Independent Gradle 9.7.1 / Kotlin 2.4.20 consumer | JVM/macOS database round-trips passed; common and native metadata compiled, including direct C bindings |
| Gradle Android KMP 9.3.2 consumer | Compiled against the published AAR and its dependencies |
| Maven Central configuration | Signed publication model validated without running publication |

The migration probe initially contained stale cinterop output from before
publication coordinates were configured. A fresh build of the final checkout
successfully commonized and published the bindings; no commonizer workaround
was required.

Local Maven verification used:

```sh
KOTLIN_CLI_JAVA_OPTIONS=-Dmaven.repo.local=/tmp/rocksdb-ktc-maven \
  ./kotlin publish mavenLocal
```

The independent consumer depended only on that repository and public dependencies;
it did not reference this project's sources. No remote artifacts were published.
Signing with release credentials and the hosted Linux, Windows, macOS Intel,
and Android Native CI executions remain unverified locally. The CI workflow
retains every previous target and now also executes Android host tests.

When updating Toolchain, keep the pins in both root launchers and both official
wrappers aligned. Recheck the native override regressions and simulator suites
before removing these workarounds.

## Upstream references

- [Pinned Toolchain release](https://github.com/JetBrains/kotlin-toolchain/releases/tag/v0.12.2)
- [Native test runner](https://github.com/JetBrains/kotlin-toolchain/blob/82a15324c05a4f1dc55b4e994863c665817f4729/sources/amper-cli/src/org/jetbrains/amper/tasks/native/NativeTestTask.kt)
- [Native interoperability](https://kotlin-toolchain.org/latest/user-guide/advanced/native-interop/)
- [Publishing](https://kotlin-toolchain.org/latest/user-guide/publishing/)
