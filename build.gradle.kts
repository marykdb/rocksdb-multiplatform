@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import java.util.*

repositories {
    google()
    mavenCentral()
}

plugins {
    id("maven-publish")
    id("signing")
    id("com.android.library") version "8.7.0"
    kotlin("multiplatform") version "2.1.0"
}

group = "io.maryk.rocksdb"
version = "9.6.1"

val rocksDBJVMVersion = "9.6.1"
val rocksDBAndroidVersion = "9.6.1"


val kotlinNativeDataPath = System.getenv("KONAN_DATA_DIR")?.let { File(it) }
    ?: File(System.getProperty("user.home")).resolve(".konan")

val rocksdbBuildPath = "./rocksdb/build"

val buildMacOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildRocksdbMac.sh")
}

val buildLinux by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildRocksdbLinux.sh")
}

val buildIOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildRocksdbiOS.sh")
}

val buildIOSSimulator by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildRocksdbiOS.sh")
}

android {
    namespace = "io.maryk.rocksdb"
    buildToolsVersion = "34.0.0"
    compileSdk = 34

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
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }

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
                languageVersion = "2.1"
                apiVersion = "2.1"
                progressiveMode = true
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.cinterop.BetaInteropApi")
            }
        }
        val commonMain by getting {}
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jvmMain {
            dependencies {
                api("org.rocksdb:rocksdbjni:$rocksDBJVMVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                api("io.maryk.rocksdb:rocksdb-android:$rocksDBAndroidVersion")
            }
        }
        val androidUnitTest by getting {
            kotlin.srcDir("src/jvmTest/kotlin")
        }
    }

    fun KotlinNativeTarget.setupTarget(buildName: String, buildTask: Exec) {
        binaries {
            executable {
                freeCompilerArgs += listOf("-g")
            }
            getTest("DEBUG").linkerOpts = mutableListOf(
                "-L$rocksdbBuildPath/${buildName}", "-lrocksdb"
            )
        }

        compilations["main"].apply {
            cinterops {
                create("rocksdb") {
                    defFile("src/nativeInterop/cinterop/rocksdbC.def")
                    includeDirs("./rocksdb/include/rocksdb")
                    defFile("src/nativeInterop/cinterop/rocksdb_${buildName}.def")
                    tasks[interopProcessingTaskName].dependsOn(buildTask)
                }
            }
        }
    }

//    iosX64 {
//        setupAppleTarget("ios_simulator_x86_64", buildIOSSimulator)
//    }
    iosArm64 {
        setupTarget("ios_arm64", buildIOS)
    }
    iosSimulatorArm64 {
        setupTarget("ios_simulator_arm64", buildIOSSimulator)
    }

    macosX64 {
        setupTarget("macos_x86_64", buildMacOS)
    }
    macosArm64 {
        setupTarget("macos_arm64", buildMacOS)
    }
    linuxArm64 {
        setupTarget("linux_arm64", buildLinux)
    }
    linuxX64 {
        setupTarget("linux_x86_64", buildLinux)
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

// Placeholder secrets to allow project sync and build without publication setup
ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")

// Load secrets from local.properties if available, otherwise fallback to environment variables (useful for CI/CD)
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use { reader ->
        Properties().apply {
            load(reader)
            forEach { name, value ->
                ext[name.toString()] = value.toString()
            }
        }
    }
}

fun getExtraString(name: String): String? = ext[name]?.toString()

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "sonatype"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = getExtraString("ossrhUsername")
                    password = getExtraString("ossrhPassword")
                }
            }
        }

        publications.withType<MavenPublication> {
            if (this.name == "jvm") {
                artifact(emptyJavadocJar.get())
            }
            pom {
                name = project.name
                description = "Kotlin multiplatform RocksDB interface"
                url = "https://github.com/marykdb/rocksdb-multiplatform"

                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                    }
                }
                developers {
                    developer {
                        id = "jurmous"
                        name = "Jurriaan Mous"
                    }
                }
                scm {
                    url = "https://github.com/marykdb/rocksdb-multiplatform"
                }
            }
        }
    }

    signing {
        println(publishing.publications)
        sign(publishing.publications)
    }
}

tasks {
    "wrapper"(Wrapper::class) {
        distributionType = Wrapper.DistributionType.ALL
    }
}
