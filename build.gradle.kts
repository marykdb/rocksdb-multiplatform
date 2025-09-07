@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

repositories {
    google()
    mavenCentral()
}

plugins {
    id("com.android.library") version "8.13.0"
    kotlin("multiplatform") version "2.2.10"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.maryk.rocksdb"
version = "10.4.2"

val rocksDBJVMVersion = "10.4.2"
val rocksDBAndroidVersion = "10.4.2"

val kotlinXDateTimeVersion = "0.7.1"

val rocksdbBuildPath = "./rocksdb/build"

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

    val isMacOs = System.getProperty("os.name").contains("Mac", ignoreCase = true)

    fun KotlinNativeTarget.setupTarget(buildName: String, buildTask: Exec, extraCFlags: String = "", extraCmakeFlags: String = "") {
        binaries {
            executable {
                freeCompilerArgs += listOf("-g")
            }
            getTest("DEBUG").linkerOpts = mutableListOf(
                "-L$rocksdbBuildPath/${buildName}"
            )
        }

        val dependencyTask = tasks.create("buildDependencies_$buildName", Exec::class) {
            workingDir = projectDir
            val options = buildList {
                if (buildName.startsWith("linux") && isMacOs) {
                    addAll(listOf("docker", "run", "--rm", "--platform"))
                    if (extraCFlags == "-march=x86-64") {
                        add("linux/amd64")
                    } else {
                        add("linux/arm64/v8")
                    }
                    addAll(listOf("-v", "${project.projectDir}:/rocks", "-w", "/rocks", "buildpack-deps"))
                }
                addAll(listOf("./buildDependencies.sh", "--extra-cflags", extraCFlags, "--output-dir", "rocksdb/build/$buildName", "--extra-cmakeflags", extraCmakeFlags))
            }
            commandLine(*options.toTypedArray())
        }

        compilations["main"].apply {
            cinterops {
                create("rocksdb") {
                    defFile("src/nativeInterop/cinterop/rocksdb.def")
                    includeDirs("rocksdb/include/rocksdb")
                    tasks[interopProcessingTaskName].dependsOn(buildTask)
                    tasks[interopProcessingTaskName].dependsOn(dependencyTask)
                }
            }
        }
    }

    iosArm64 {
        val sdkPathProvider = providers.exec {
            commandLine("xcrun", "--sdk", "iphoneos", "--show-sdk-path")
        }.standardOutput.asText
        val sdkPath: String by lazy {
            if (isMacOs) {
                sdkPathProvider.get().trim()
            } else ""
        }
        val buildTask = tasks.create("buildLib-"+this.name, Exec::class) {
            workingDir = projectDir
            commandLine("./buildRocksdbApple.sh", "--platform=ios", "--arch=arm64")
        }
        setupTarget("ios_arm64", buildTask, "-arch arm64 -target arm64-apple-ios13.0 -isysroot $sdkPath", "-DPLATFORM=OS64")
    }
    iosSimulatorArm64 {
        val sdkPathProvider = providers.exec {
            commandLine("xcrun", "--sdk", "iphonesimulator", "--show-sdk-path")
        }.standardOutput.asText
        val sdkPath: String by lazy {
            if (isMacOs) {
                sdkPathProvider.get().trim()
            } else ""
        }
        val buildTask = tasks.create("buildLib-"+this.name, Exec::class) {
            workingDir = projectDir
            commandLine("./buildRocksdbApple.sh", "--platform=ios", "--simulator", "--arch=arm64")
        }
        setupTarget("ios_simulator_arm64", buildTask, "-arch arm64 -target arm64-apple-ios13.0-simulator -isysroot $sdkPath", "-DPLATFORM=SIMULATORARM64")
    }
    macosX64 {
        val buildTask = tasks.create("buildLib-"+this.name, Exec::class) {
            workingDir = projectDir
            commandLine("./buildRocksdbApple.sh", "--platform=macos", "--arch=x86_64")
        }
        setupTarget("macos_x86_64", buildTask, "-arch x86_64 -target x86_64-apple-macos11.0", "-DPLATFORM=OS64")
    }
    macosArm64 {
        val buildTask = tasks.create("buildLib-"+this.name, Exec::class) {
            workingDir = projectDir
            commandLine("./buildRocksdbApple.sh", "--platform=macos", "--arch=arm64")
        }
        setupTarget("macos_arm64", buildTask, "-arch arm64 -target arm64-apple-macos11.0", "-DPLATFORM=MAC")
    }
    linuxX64 {
        val buildTask = tasks.create("buildLib-"+this.name, Exec::class) {
            workingDir = projectDir
            commandLine("./buildRocksdbLinux.sh", "--arch=x86-64")
        }
        setupTarget("linux_x86_64", buildTask, "-march=x86-64")
    }
    linuxArm64 {
        val buildTask = tasks.create("buildLib-"+this.name, Exec::class) {
            workingDir = projectDir
            commandLine("./buildRocksdbLinux.sh", "--arch=arm64")
        }
        setupTarget("linux_arm64", buildTask, "-march=armv8-a")
    }
    mingwX64 {
        val buildTask = tasks.create("buildLib-"+this.name, Exec::class) {
            workingDir = projectDir
            commandLine("./buildRocksdbMinGW.sh", "--arch=x86_64")
        }
        setupTarget("mingw_x86_64", buildTask, "")
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
