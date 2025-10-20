@file:Suppress("UnstableApiUsage")

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.Locale
import java.util.Properties
import java.util.zip.ZipFile
import kotlin.text.Charsets
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBinary
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.Family

repositories {
    google()
    mavenCentral()
}

plugins {
    id("com.android.library") version "8.13.0"
    kotlin("multiplatform") version "2.2.20"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.maryk.rocksdb"
version = "10.4.6"

val rocksDBJVMVersion = "10.4.2"
val rocksDBAndroidVersion = "10.4.2"
val rocksDBJvmRuntimeClassifiers = listOf(
    "osx",
    "linux64",
    "linux64-musl",
    "linux32",
    "win64"
)

val kotlinXDateTimeVersion = "0.7.1"
val kotlinXCoroutinesVersion = "1.10.2"
val rocksdbPrebuiltBaseUrlValue = providers.gradleProperty("rocksdbPrebuiltBaseUrl").orElse("https://github.com/marykdb/build-rocksdb/releases/download").get()
val rocksdbPrebuiltVersionValue = providers.gradleProperty("rocksdbPrebuiltVersion").get()
val rocksdbSupportedNativeTargets = setOf(
    "androidNativeArm32",
    "androidNativeArm64",
    "androidNativeX64",
    "androidNativeX86",
    "iosArm64",
    "iosSimulatorArm64",
    "linuxArm64",
    "linuxX64",
    "macosArm64",
    "macosX64",
    "mingwX64",
    "tvosArm64",
    "tvosSimulatorArm64",
    "watchosArm64",
    "watchosDeviceArm64",
    "watchosSimulatorArm64"
)

object RocksdbArtifacts {
    val names: Map<String, String> = mapOf(
        "androidNativeArm32" to "rocksdb-android-arm32.zip",
        "androidNativeArm64" to "rocksdb-android-arm64.zip",
        "androidNativeX64" to "rocksdb-android-x64.zip",
        "androidNativeX86" to "rocksdb-android-x86.zip",
        "iosArm64" to "rocksdb-ios-arm64.zip",
        "iosSimulatorArm64" to "rocksdb-ios-simulator-arm64.zip",
        "macosArm64" to "rocksdb-macos-arm64.zip",
        "macosX64" to "rocksdb-macos-x86_64.zip",
        "linuxArm64" to "rocksdb-linux-arm64.zip",
        "linuxX64" to "rocksdb-linux-x86_64.zip",
        "mingwX64" to "rocksdb-mingw-x86_64.zip",
        "tvosArm64" to "rocksdb-tvos-arm64.zip",
        "tvosSimulatorArm64" to "rocksdb-tvos-simulator-arm64.zip",
        "watchosArm64" to "rocksdb-watchos-arm64.zip",
        "watchosDeviceArm64" to "rocksdb-watchos-device-arm64.zip",
        "watchosSimulatorArm64" to "rocksdb-watchos-simulator-arm64.zip"
    )
}


@CacheableTask
abstract class DownloadRocksdbTask : DefaultTask() {
    @get:Input
    abstract val kmpTarget: Property<String>

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val baseUrl: Property<String>

    @get:Input
    @get:Optional
    abstract val sha256: Property<String?>

    @get:OutputDirectory
    abstract val destinationDir: DirectoryProperty

    @Suppress("unused")
    @TaskAction
    fun download() {
        val target = kmpTarget.get()
        val artifactName = RocksdbArtifacts.names[target]
            ?: throw GradleException("No artifact mapping found for KMP target '$target'.")

        val destination = destinationDir.get().asFile
        val shaValue = sha256.orNull?.takeIf { it.isNotBlank() }
        val expectedMarker = buildString {
            append("version=${version.get()}")
            if (shaValue != null) {
                append(" sha256=$shaValue")
            }
        }

        val metadataFile = File(destination, ".rocksdb-prebuilt")
        val metadataMatches = metadataFile.isFile &&
            metadataFile.readText(Charsets.UTF_8).trim() == expectedMarker
        val destinationComplete =
            destination.resolve("lib").isDirectory && destination.resolve("include").isDirectory

        if (metadataMatches && destinationComplete) {
            return
        }

        val downloadDir = project.layout.buildDirectory.dir("rocksdb-downloads").get().asFile
        downloadDir.mkdirs()

        val zipFile = File(downloadDir, artifactName)
        val url = "${baseUrl.get().trimEnd('/')}/${version.get()}/$artifactName"

        if (!metadataMatches && shaValue == null && zipFile.exists()) {
            zipFile.delete()
        }

        if (!zipFile.exists()) {
            downloadArchive(url, zipFile)
        }

        if (shaValue != null) {
            if (!zipFile.hasExpectedSha(shaValue)) {
                project.logger.warn("Checksum mismatch for RocksDB archive '$artifactName'. Re-downloading.")
                zipFile.delete()
                downloadArchive(url, zipFile)
                if (!zipFile.hasExpectedSha(shaValue)) {
                    zipFile.delete()
                    throw GradleException("Checksum mismatch for RocksDB archive '$artifactName'.")
                }
            }
        }

        if (destination.exists()) {
            destination.deleteRecursively()
        }
        destination.mkdirs()

        val tempDir = Files.createTempDirectory("rocksdb-download").toFile()
        try {
            unzip(zipFile, tempDir)
            tempDir.listFiles()?.forEach { extracted ->
                val targetFile = File(destination, extracted.name)
                if (targetFile.exists()) {
                    targetFile.deleteRecursively()
                }
                extracted.copyRecursively(targetFile, overwrite = true)
            }
        } finally {
            tempDir.deleteRecursively()
        }

        metadataFile.writeText(expectedMarker, Charsets.UTF_8)
    }

    private fun downloadArchive(url: String, targetFile: File) {
        targetFile.parentFile?.mkdirs()
        var lastFailure: Exception? = null

        repeat(3) { attempt ->
            var connection: HttpURLConnection? = null
            val tempFile = Files.createTempFile(targetFile.parentFile.toPath(), "rocksdb", ".tmp").toFile()
            try {
                connection = URI.create(url).toURL().openConnection() as HttpURLConnection
                connection.connectTimeout = 30_000
                connection.readTimeout = 30_000
                connection.instanceFollowRedirects = true

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    val message = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    throw GradleException(
                        buildString {
                            append("Failed to download RocksDB archive from $url. HTTP $responseCode")
                            if (!message.isNullOrBlank()) {
                                append(':').append(' ')
                                append(message.trim())
                            }
                        }
                    )
                }

                connection.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                return
            } catch (ex: Exception) {
                lastFailure = ex
                tempFile.delete()
                if (attempt == 2) {
                    throw GradleException("Unable to download RocksDB archive from $url", ex)
                }
                project.logger.warn("Attempt ${attempt + 1} to download RocksDB archive failed: ${ex.message}")
                try {
                    Thread.sleep(2_000)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            } finally {
                connection?.disconnect()
                if (tempFile.exists()) {
                    tempFile.delete()
                }
            }
        }

        lastFailure?.let { throw GradleException("Unable to download RocksDB archive from $url", it) }
    }

    private fun File.hasExpectedSha(expected: String): Boolean {
        val digest = MessageDigest.getInstance("SHA-256")
        inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        val actual = digest.digest().joinToString(separator = "") { byte -> "%02x".format(byte) }
        return actual.equals(expected, ignoreCase = true)
    }

    private fun unzip(zipFile: File, destination: File) {
        ZipFile(zipFile).use { archive ->
            val entries = archive.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val outputFile = File(destination, entry.name)
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    archive.getInputStream(entry).use { input ->
                        FileOutputStream(outputFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
    }
}

fun rocksdbShaFor(targetName: String): String? =
    findProperty("rocksdbPrebuiltSha.$targetName")?.toString()?.takeIf { it.isNotBlank() }

fun KotlinNativeTarget.configureRocksdbPrebuilt(version: String, baseUrl: String) {
    val targetName = this.name
    if (!rocksdbSupportedNativeTargets.contains(targetName)) return

    val project = this.project
    val capitalizedName = targetName.replaceFirstChar { character ->
        if (character.isLowerCase()) character.titlecase(Locale.ROOT) else character.toString()
    }
    val prebuiltDirProvider = project.layout.buildDirectory.dir("rocksdb/$targetName")
    val prebuiltDir = prebuiltDirProvider.get().asFile
    val includeDir = prebuiltDir.resolve("include")
    val includeRocksdbDir = includeDir.resolve("rocksdb")
    val includeNestedRocksdbDir = includeRocksdbDir.resolve("rocksdb")
    val libDir = prebuiltDir.resolve("lib")
    val sha = rocksdbShaFor(targetName)

    val downloadTask = project.tasks.register<DownloadRocksdbTask>("downloadRocksdb$capitalizedName") {
        group = "rocksdb"
        description = "Download RocksDB prebuilt bundle for $targetName"
        kmpTarget.set(targetName)
        this.version.set(version)
        this.baseUrl.set(baseUrl)
        destinationDir.set(prebuiltDirProvider)
        if (sha != null) {
            sha256.set(sha)
        }
    }

    val interop = compilations.getByName("main").cinterops.maybeCreate("rocksdb").apply {
        defFile(project.file("src/nativeInterop/cinterop/rocksdb.def"))
        includeDirs(project.files(includeDir, includeRocksdbDir, includeNestedRocksdbDir))
        compilerOpts(
            "-I${includeDir.absolutePath}",
            "-I${includeRocksdbDir.absolutePath}",
            "-I${includeNestedRocksdbDir.absolutePath}"
        )
        extraOpts("-libraryPath", libDir.absolutePath)
    }

    project.tasks.named(interop.interopProcessingTaskName).configure {
        dependsOn(downloadTask)
    }

    binaries.withType<TestExecutable>().configureEach {
        linkerOpts("-L${libDir.absolutePath}")
    }

    if (konanTarget.family == Family.MINGW) {
        binaries.all {
            linkerOpts("-lrpcrt4")
        }
    }

    binaries.withType<NativeBinary>().configureEach {
        linkTaskProvider.configure {
            dependsOn(downloadTask)
        }
    }
}

private fun fetchSha256WithRetry(url: String): String {
    var lastFailure: Exception? = null
    repeat(3) { attempt ->
        var connection: HttpURLConnection? = null
        try {
            val httpConnection = URI.create(url).toURL().openConnection() as HttpURLConnection
            connection = httpConnection
            httpConnection.connectTimeout = 30_000
            httpConnection.readTimeout = 30_000
            httpConnection.instanceFollowRedirects = true

            val responseCode = httpConnection.responseCode
            if (responseCode !in 200..299) {
                val message = httpConnection.errorStream?.bufferedReader()?.use { it.readText() }?.trim()
                throw GradleException(
                    buildString {
                        append("Failed to download RocksDB archive from $url. HTTP $responseCode")
                        if (!message.isNullOrBlank()) {
                            append(':').append(' ')
                            append(message)
                        }
                    }
                )
            }

            val digest = MessageDigest.getInstance("SHA-256")
            httpConnection.inputStream.use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    digest.update(buffer, 0, read)
                }
            }

            return digest.digest().joinToString(separator = "") { byte -> "%02x".format(byte) }
        } catch (ex: Exception) {
            lastFailure = ex
            if (attempt == 2) {
                throw GradleException("Unable to download RocksDB archive from $url", ex)
            }
            try {
                Thread.sleep(2_000)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        } finally {
            connection?.disconnect()
        }
    }

    throw GradleException("Unable to download RocksDB archive from $url", lastFailure ?: IllegalStateException("Unknown failure"))
}

android {
    namespace = "io.maryk.rocksdb"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    val toolchainVersion = JavaVersion.current().majorVersion.toInt()
    jvmToolchain(toolchainVersion)

    // configure all Kotlin/JVM Tests to use JUnit Jupiter
    targets.withType<KotlinJvmTarget>().configureEach {
        testRuns.configureEach {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xjdk-release=11")
        }
    }
    androidTarget {
        publishLibraryVariants("release")
        publishLibraryVariantsGroupedByFlavor = true
    }

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()

    iosArm64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    tvosArm64()
    tvosSimulatorArm64()

    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "2.2"
                apiVersion = "2.2"
                progressiveMode = true
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.cinterop.BetaInteropApi")
            }
        }
        nativeMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinXCoroutinesVersion")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinXDateTimeVersion")
            }
        }
        jvmMain {
            dependencies {
                api("org.rocksdb:rocksdbjni:$rocksDBJVMVersion")
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                api("io.maryk.rocksdb:rocksdb-android:$rocksDBAndroidVersion")
            }
        }
        androidUnitTest {
            kotlin.srcDir("src/jvmTest/kotlin")
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        configureRocksdbPrebuilt(rocksdbPrebuiltVersionValue, rocksdbPrebuiltBaseUrlValue)
    }
}

listOf(
    "jvmRuntimeElements",
    "jvmRuntimeClasspath",
    "jvmTestRuntimeOnly"
).forEach { configurationName ->
    configurations.findByName(configurationName)?.withDependencies {
        // Ensure native classifier jars remain runtime-only, but are still published and
        // available on the local runtime/test classpaths so RocksDB can load its native libs.
        rocksDBJvmRuntimeClassifiers.forEach { classifier ->
            add(project.dependencies.create("org.rocksdb:rocksdbjni:$rocksDBJVMVersion:$classifier"))
        }
    }
}

tasks.register("updateRocksdbShas") {
    group = "rocksdb"
    description = "Download RocksDB prebuilts for the configured version and refresh SHA entries in gradle.properties."

    doLast {
        val propertiesFile = project.rootProject.file("gradle.properties")
        if (!propertiesFile.exists()) {
            throw GradleException("Unable to locate gradle.properties at ${propertiesFile.absolutePath}")
        }

        val properties = Properties().apply {
            propertiesFile.inputStream().use { input -> load(input) }
        }

        val baseUrlFromFile = properties.getProperty("rocksdbPrebuiltBaseUrl")?.trim()
        val baseUrl = (baseUrlFromFile?.takeIf { it.isNotEmpty() } ?: rocksdbPrebuiltBaseUrlValue).trimEnd('/')
        val version = properties.getProperty("rocksdbPrebuiltVersion")?.trim()?.takeIf { it.isNotEmpty() }
            ?: throw GradleException("rocksdbPrebuiltVersion is not set in gradle.properties")

        val shaRegex = Regex("^rocksdbPrebuiltSha\\.([A-Za-z0-9]+)=.*")
        val lines = propertiesFile.readLines(Charsets.UTF_8)
        val targets = lines.mapNotNull { line -> shaRegex.find(line)?.groupValues?.get(1) }.distinct()

        if (targets.isEmpty()) {
            logger.lifecycle("No rocksdbPrebuiltSha entries found in ${propertiesFile.toRelativeString(project.rootProject.projectDir)}")
            return@doLast
        }

        val updatedShas = mutableMapOf<String, String>()
        targets.forEach { target ->
            val artifactName = RocksdbArtifacts.names[target]
                ?: throw GradleException("No artifact mapping found for target '$target'.")
            val url = "$baseUrl/$version/$artifactName"
            logger.lifecycle("Calculating SHA-256 for $artifactName")
            val sha = fetchSha256WithRetry(url)
            updatedShas[target] = sha
            val previous = properties.getProperty("rocksdbPrebuiltSha.$target")
            if (previous == null || !previous.equals(sha, ignoreCase = true)) {
                logger.lifecycle("rocksdbPrebuiltSha.$target = $sha")
            }
        }

        val updatedContent = lines.map { line ->
            val match = shaRegex.find(line)
            if (match != null) {
                val target = match.groupValues[1]
                val sha = updatedShas[target]
                if (sha != null) {
                    "rocksdbPrebuiltSha.$target=$sha"
                } else {
                    line
                }
            } else {
                line
            }
        }

        propertiesFile.writeText(buildString {
            updatedContent.joinTo(this, "\n")
            append('\n')
        }, Charsets.UTF_8)

        logger.lifecycle(
            "Updated ${updatedShas.size} RocksDB SHA entries in ${propertiesFile.toRelativeString(project.rootProject.projectDir)}"
        )
    }
}

// Creates the folders for the database
val createOrEraseDBFolders = tasks.register("createOrEraseDBFolders") {
    group = "verification"

    doLast {
        val subdir = project.layout.buildDirectory.dir("test-database").get().asFile

        if (!subdir.exists()) {
            subdir.deleteOnExit()
            subdir.mkdirs()
        } else {
            subdir.deleteRecursively()
            subdir.mkdirs()
        }
    }
}

tasks.getByName("clean", Delete::class) {
    delete("xcodeBuild")
}

tasks.withType<Test> {
    this.dependsOn(createOrEraseDBFolders)
    this.doLast {
        project.layout.buildDirectory.dir("test-database").get().asFile.deleteRecursively()
    }
}

kotlin.targets.withType<KotlinNativeTarget>().configureEach {
    binaries.withType<TestExecutable>().all {
        tasks.findByName("${this.target.name}Test")?.apply {
            dependsOn(createOrEraseDBFolders)
            doLast {
                project.layout.buildDirectory.dir("test-database").get().asFile.deleteRecursively()
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
}

mavenPublishing {
    coordinates(artifactId = "rocksdb-multiplatform")

    pom {
        name.set("rocksdb-multiplatform")
        description.set("Kotlin Multiplatform RocksDB interface")
        inceptionYear.set("2019")
        url.set("https://github.com/marykdb/rocksdb-multiplatform")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("jurmous")
                name.set("Jurriaan Mous")
            }
        }

        scm {
            url.set("https://github.com/marykdb/rocksdb-multiplatform")
            connection.set("scm:git:git://github.com/marykdb/rocksdb-multiplatform.git")
            developerConnection.set("scm:git:ssh://git@github.com/marykdb/rocksdb-multiplatform.git")
        }
    }
}

tasks {
    "wrapper"(Wrapper::class) {
        distributionType = Wrapper.DistributionType.ALL
    }
}
