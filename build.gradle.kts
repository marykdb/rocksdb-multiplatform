import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

repositories {
    google()
    jcenter()
}

plugins {
    id("maven-publish")
    id("com.android.library") version "3.5.3"
    kotlin("multiplatform") version "1.3.61"
    id("com.jfrog.bintray").version("1.8.4")
}

group = "io.maryk.rocksdb"
version = "0.5.0"

val rocksDBVersion = "6.5.2"
val rocksDBAndroidVersion = "0.6.0"

val kotlinNativeDataPath = System.getenv("KONAN_DATA_DIR")?.let { File(it) }
    ?: File(System.getProperty("user.home")).resolve(".konan")

val objectiveRocksHome = "./xcodeBuild/Build/Products/Release"

val buildMacOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksMac.sh")
}

val buildIOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksiOS.sh")
}

val buildIOSSimulator by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksiOS.sh", "iphonesimulator")
}

android {
    buildToolsVersion = "29.0.0"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        multiDexEnabled = true
        versionCode = 1
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    val appleMain by sourceSets.creating {
        dependsOn(sourceSets["commonMain"])
        dependencies {
            // Added so dependencies are resolved in IDE
            compileOnly(files("build/libs/macosX64/main/rocksdb-multiplatform-cinterop-rocksdbMacOS.klib"))
        }
    }
    val appleTest by sourceSets.creating {
        dependsOn(sourceSets["commonTest"])
    }

    val jvmSharedMain by sourceSets.creating {
        dependsOn(sourceSets["commonMain"])
        dependencies {
            // Added so dependencies are resolved in IDE
            compileOnly("io.maryk.rocksdb:rocksdbjni:$rocksDBVersion")
            implementation(kotlin("stdlib"))
        }
    }
    val jvmSharedTest by sourceSets.creating {
        dependsOn(sourceSets["commonTest"])
        dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-junit"))
            implementation("org.assertj:assertj-core:1.7.1")
        }
    }

    fun KotlinNativeTarget.setupAppleTarget(definitionName: String, buildTask: Exec) {
        binaries {
            getTest("DEBUG").linkerOpts = mutableListOf(
                "-L${objectiveRocksHome}", "-lobjectiveRocks-$definitionName"
            )
        }

        compilations["main"].apply {
            cinterops {
                this.create("rocksdb${definitionName.capitalize()}") {
                    tasks[interopProcessingTaskName].dependsOn(buildTask)
                    includeDirs("${objectiveRocksHome}/usr/local/include", "../ObjectiveRocks/Code")
                }
            }

            source(appleMain)
        }
        compilations["test"].apply {
            source(appleTest)
        }
    }

    ios {
        if (this.name == "iosX64") {
            setupAppleTarget("iOSSimulator", buildIOSSimulator)
        } else {
            setupAppleTarget("iOS", buildIOS)
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
        compilations["main"].source(jvmSharedMain)
        compilations["test"].source(jvmSharedTest)
    }
    android {
        compilations.all {
            when (this.name) {
                "release", "debug" -> {
                    source(jvmSharedMain)
                }
                "releaseTest", "debugTest" ->{
                    source(jvmSharedTest)
                }
            }
        }
        setupJvmTarget()
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "1.3"
                apiVersion = "1.3"
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                progressiveMode = true
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("io.maryk.rocksdb:rocksdb-android:$rocksDBAndroidVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                api("io.maryk.rocksdb:rocksdbjni:$rocksDBVersion")
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

afterEvaluate {
    fun findProperty(s: String) = project.findProperty(s) as String?
    bintray {
        user = findProperty("bintrayUser")
        key = findProperty("bintrayApiKey")
        publish = true
        pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            repo = "maven"
            name = "rocksdb-multiplatform"
            userOrg = "maryk"
            setLicenses("Apache-2.0")
            setPublications(*project.publishing.publications.names.toTypedArray())
            vcsUrl = "https://github.com/marykdb/rocksdb-multiplatform.git"
        })
    }

    // https://github.com/bintray/gradle-bintray-plugin/issues/229
    tasks.withType<BintrayUploadTask> {
        doFirst {
            publishing.publications
                .filterIsInstance<MavenPublication>()
                .forEach { publication ->
                    val moduleFile = buildDir.resolve("publications/${publication.name}/module.json")
                    if (moduleFile.exists()) {
                        publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
                            override fun getDefaultExtension() = "module"
                        })
                    }
                }
        }
    }

    project.publishing.publications.withType<MavenPublication>().forEach { publication ->
        publication.pom.withXml {
            asNode().apply {
                appendNode("name", project.name)
                appendNode("description", "Kotlin multiplatform RocksDB interface")
                appendNode("url", "https://github.com/marykdb/rocksdb-multiplatform")
                appendNode("licenses").apply {
                    appendNode("license").apply {
                        appendNode("name", "The Apache Software License, Version 2.0")
                        appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                        appendNode("distribution", "repo")
                    }
                }
                appendNode("developers").apply {
                    appendNode("developer").apply {
                        appendNode("id", "jurmous")
                        appendNode("name", "Jurriaan Mous")
                    }
                }
                appendNode("scm").apply {
                    appendNode("url", "https://github.com/marykdb/rocksdb-multiplatform")
                }
            }
        }
    }
}
