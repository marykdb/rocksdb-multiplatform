import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    `maven-publish`
    kotlin("multiplatform") version "1.3.50"
    id("com.jfrog.bintray").version("1.8.4")
}

repositories {
    mavenCentral()
}

group = "io.maryk.rocksdb"
version = "0.1.4"

val rocksDBVersion = "6.2.2"

val kotlinNativeDataPath = System.getenv("KONAN_DATA_DIR")?.let { File(it) }
    ?: File(System.getProperty("user.home")).resolve(".konan")

val snappyHome = kotlinNativeDataPath.resolve("third-party/snappy/snappy-1.1.7")
val bzip2Home = kotlinNativeDataPath.resolve("third-party/bzip2/bzip2-1.0.8")
val lz4Home = kotlinNativeDataPath.resolve("third-party/lz4/lz4-1.9.2")
val zlibHome = kotlinNativeDataPath.resolve("third-party/zlib/zlib-1.2.11")
val rocksdbHome = kotlinNativeDataPath.resolve("third-party/rocksdb/rocksdb-6.2.2")

kotlin {
    macosX64("macos") {
        binaries.getTest("DEBUG").linkerOpts = mutableListOf(
            "-L${snappyHome}/build", "-lsnappy",
            "-L${bzip2Home}", "-lbz2",
            "-L${lz4Home}/lib", "-llz4",
            "-L${zlibHome}", "-lz",
            "-L${rocksdbHome}", "-lrocksdb"
        )

        compilations["main"].cinterops {
            val rocksdb by creating {
                includeDirs("${rocksdbHome}/include/rocksdb")
            }
        }
    }
    jvm {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                jvmTarget = "1.8"
            }
        }
    }
    sourceSets {
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
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                api("org.rocksdb:rocksdbjni:$rocksDBVersion")
                implementation(kotlin("stdlib"))
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.assertj:assertj-core:1.7.1")
            }
        }
    }
}

val downloadSnappy by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./downloadSnappy.sh")
}

val downloadBzip2 by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./downloadBzip2.sh")
}

val downloadLz4 by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./downloadLz4.sh")
}

val downloadZlib by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./downloadZlib.sh")
}

val downloadRocksdb by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./downloadRocksdb.sh")
}

val macos: KotlinNativeTarget by kotlin.targets
tasks[macos.compilations["main"].cinterops["rocksdb"].interopProcessingTaskName]
    .dependsOn(downloadSnappy)
    .dependsOn(downloadBzip2)
    .dependsOn(downloadLz4)
    .dependsOn(downloadZlib)
    .dependsOn(downloadRocksdb)

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

tasks.withType<Test> {
    this.dependsOn(createOrEraseDBFolders)
    this.doLast {
        File(project.buildDir, "test-database").deleteRecursively()
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
    user = findProperty("bintrayUser")
    key = findProperty("bintrayApiKey")
    publish = true
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "RocksDB"
        userOrg = "maryk"
        setLicenses("Apache-2.0")
        setPublications(*project.publishing.publications.names.toTypedArray())
        vcsUrl = "https://github.com/maryk-io/rocksdb.git"
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

afterEvaluate {
    project.publishing.publications.withType<MavenPublication>().forEach { publication ->
        publication.pom.withXml {
            asNode().apply {
                appendNode("name", project.name)
                appendNode("description", "Kotlin multiplatform RocksDB interface")
                appendNode("url", "https://github.com/maryk-io/rocksdb")
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
                    appendNode("url", "https://github.com/maryk-io/rocksdb")
                }
            }
        }
    }
}
