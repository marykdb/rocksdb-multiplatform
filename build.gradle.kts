@file:Suppress("UnstableApiUsage")

import java.util.Locale
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBinary

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
version = "10.4.2"

val rocksDBJVMVersion = "10.4.2"
val rocksDBAndroidVersion = "10.4.2"

val kotlinXDateTimeVersion = "0.7.1"
val rocksdbPrebuiltBaseUrlValue = providers.gradleProperty("rocksdbPrebuiltBaseUrl").orElse("https://github.com/marykdb/build-rocksdb/releases/download").get()
val rocksdbPrebuiltVersionValue = providers.gradleProperty("rocksdbPrebuiltVersion").orElse("rocksdb-10.4.2-20250921T085231Z").get()
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

fun org.gradle.api.Project.rocksdbShaFor(targetName: String): String? =
    findProperty("rocksdbPrebuiltSha.$targetName")?.toString()?.takeIf { it.isNotBlank() }

fun KotlinNativeTarget.configureRocksdbPrebuilt(version: String, baseUrl: String) {
    val targetName = this.name
    if (!rocksdbSupportedNativeTargets.contains(targetName)) return

    val project = this.project
    val capitalizedName = targetName.replaceFirstChar { character ->
        if (character.isLowerCase()) character.titlecase(Locale.ROOT) else character.toString()
    }
    val prebuiltDir = project.layout.buildDirectory.dir("rocksdb/$targetName").get().asFile
    val includeDir = prebuiltDir.resolve("include")
    val includeRocksdbDir = includeDir.resolve("rocksdb")
    val includeNestedRocksdbDir = includeRocksdbDir.resolve("rocksdb")
    val libDir = prebuiltDir.resolve("lib")
    val sha = project.rocksdbShaFor(targetName)

    val downloadTask = project.tasks.register("downloadRocksdb$capitalizedName", Exec::class.java) {
        group = "rocksdb"
        description = "Download RocksDB prebuilt bundle for $targetName"
        workingDir = project.projectDir

        val command = mutableListOf(
            project.layout.projectDirectory.file("download-rocksdb.sh").asFile.absolutePath,
            "--kmp-target", targetName,
            "--version", version,
            "--destination", prebuiltDir.absolutePath,
            "--base-url", baseUrl
        )
        if (sha != null) {
            command += listOf("--sha256", sha)
        }
        commandLine(command)

        inputs.property("rocksdbVersion", version)
        if (sha != null) {
            inputs.property("rocksdbSha", sha)
        }
        outputs.dir(prebuiltDir)
    }

    val interop = compilations["main"].cinterops.create("rocksdb") {
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

    binaries.withType<NativeBinary>().configureEach {
        linkTaskProvider.configure {
            dependsOn(downloadTask)
        }
    }
}

android {
    namespace = "io.maryk.rocksdb"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    jvmToolchain(17)

    // configure all Kotlin/JVM Tests to use JUnit Jupiter
    targets.withType<KotlinJvmTarget>().configureEach {
        testRuns.configureEach {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    jvm()
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
        commonMain {}
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinXDateTimeVersion")
            }
        }
        jvmMain {
            dependencies {
                api("org.rocksdb:rocksdbjni:$rocksDBJVMVersion")
                // Temp because 10.4.2 does not contain the needed JNI files within root jar
                // https://github.com/facebook/rocksdb/issues/13893#issuecomment-3240464232
                api("org.rocksdb:rocksdbjni:$rocksDBJVMVersion:osx")
                api("org.rocksdb:rocksdbjni:$rocksDBJVMVersion:linux64")
                api("org.rocksdb:rocksdbjni:$rocksDBJVMVersion:linux32")
                api("org.rocksdb:rocksdbjni:$rocksDBJVMVersion:win64")
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

// Creates the folders for the database
val createOrEraseDBFolders = task("createOrEraseDBFolders") {
    group = "verification"

    val subdir = project.layout.buildDirectory.dir("test-database").get().asFile

    if (!subdir.exists()) {
        subdir.deleteOnExit()
        subdir.mkdirs()
    } else {
        subdir.deleteRecursively()
        subdir.mkdirs()
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
