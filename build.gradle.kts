@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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

val objectiveRocksHome = "./xcodeBuild/Build/Products/Release"

val buildMacOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksMac.sh")
}

val buildIOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksiOS.sh", "iphoneos")
}

val buildIOSSimulator by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksiOS.sh", "iphonesimulator")
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

    compilerOptions {
        allWarningsAsErrors = true
    }

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "2.1"
                apiVersion = "2.1"
                progressiveMode = true
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
        val darwinMain by creating {
            kotlin.apply {
                srcDir("src/appleMain/kotlin")
            }
        }
        val darwinTest by creating {
            kotlin.apply {
                srcDir("src/appleTest/kotlin")
            }
        }
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
            artifact(emptyJavadocJar.get())
            pom {
                name.set(project.name)
                description.set("Kotlin multiplatform RocksDB interface")
                url.set("https://github.com/marykdb/rocksdb-multiplatform")

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
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
                }
            }
        }
    }

    signing {
        sign(publishing.publications)
    }
}

tasks {
    "wrapper"(Wrapper::class) {
        distributionType = Wrapper.DistributionType.ALL
    }
}
