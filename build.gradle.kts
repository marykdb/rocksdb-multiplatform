import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.gradle.api.publish.maven.MavenPublication
import java.util.*

repositories {
    google()
    mavenCentral()
}

plugins {
    id("maven-publish")
    id("signing")
    id("com.android.library") version "4.2.2"
    kotlin("multiplatform") version "1.6.0-RC2"
}

group = "io.maryk.rocksdb"
version = "6.20.4-2"

val rocksDBVersion = "6.20.3"
val rocksDBAndroidVersion = "6.20.4-2"

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
    buildToolsVersion = "30.0.3"
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        multiDexEnabled = true
        versionCode = 1
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    fun KotlinNativeTarget.setupAppleTarget(definitionName: String, buildTask: Exec, folderExtension: String = "") {
        binaries {
            getTest("DEBUG").linkerOpts = mutableListOf(
                "-L$objectiveRocksHome${folderExtension}", "-lobjectiveRocks-$definitionName"
            )
        }

        compilations["main"].apply {
            cinterops {
                this.create("rocksdb${definitionName.capitalize()}$folderExtension") {
                    tasks[interopProcessingTaskName].dependsOn(buildTask)
                    includeDirs("./xcodeBuild/Build/Products/Release/usr/local/include")
                }
            }
            defaultSourceSet {
                kotlin.apply {
                    srcDirs("src/appleMain/kotlin")
                }
            }
        }

        compilations["test"].apply {
            defaultSourceSet {
                kotlin.apply {
                    srcDirs("src/appleTest/kotlin")
                }
            }
        }
    }

    ios {
        if (this.name == "iosX64") {
            setupAppleTarget("iOS", buildIOSSimulator, "-iphonesimulator")
        } else {
            setupAppleTarget("iOS", buildIOS, "-iphoneos")
        }
    }

    macosX64 {
        setupAppleTarget("macOS", buildMacOS)
    }

    fun KotlinTarget.setupJvmTarget() {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                if (this is KotlinJvmOptions) {
                    jvmTarget = "1.8"
                }
            }
        }
    }
    jvm {
        setupJvmTarget()
    }
    android {
        setupJvmTarget()
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "1.6"
                apiVersion = "1.6"
                progressiveMode = true
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                api("org.rocksdb:rocksdbjni:$rocksDBVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val androidMain by getting {
            kotlin.apply {
                setSrcDirs(jvmMain.kotlin.srcDirs)
            }
            dependencies {
                implementation(kotlin("stdlib"))
                api("io.maryk.rocksdb:rocksdb-android:$rocksDBAndroidVersion")
            }
        }
    }
}

// Creates the folders for the database
val createOrEraseDBFolders = task("createOrEraseDBFolders") {
    group = "verification"

    val subdir = File(project.buildDir, "test-database")

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
        File(project.buildDir, "test-database").deleteRecursively()
    }
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun getExtraString(name: String) = ext[name]?.toString()

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "sonatype"
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
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
