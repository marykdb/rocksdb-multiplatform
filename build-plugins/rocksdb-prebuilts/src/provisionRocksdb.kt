import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.Output
import org.jetbrains.amper.plugins.TaskAction
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.Properties
import java.util.zip.ZipFile
import kotlin.io.path.*

@TaskAction
fun provisionRocksdb(
    target: String,
    archiveName: String,
    @Input(inferTaskDependency = false) propertiesFile: Path,
    @Input interopDir: Path,
    @Output outputDir: Path,
    @Output defFile: Path,
) {
    val properties = Properties().apply { propertiesFile.inputStream().use { load(it) } }
    val version = properties.getProperty("rocksdbPrebuiltVersion") ?: error("Missing RocksDB prebuilt version")
    val baseUrl = properties.getProperty("rocksdbPrebuiltBaseUrl").trimEnd('/')
    val expectedSha = properties.getProperty("rocksdbPrebuiltSha.$target") ?: error("Missing checksum for $target")
    require(expectedSha.matches(Regex("[0-9a-fA-F]{64}"))) { "Invalid checksum for $target" }
    outputDir.createDirectories()
    val archive = outputDir.resolve(archiveName)
    if (!archive.exists() || !archive.hasSha(expectedSha)) {
        archive.deleteIfExists()
        download("$baseUrl/$version/$archiveName", archive)
        check(archive.hasSha(expectedSha)) { "Checksum mismatch for $archiveName" }
    }
    // Remove obsolete headers/libraries when an archive changes.
    outputDir.resolve("include").toFile().deleteRecursively()
    outputDir.resolve("lib").toFile().deleteRecursively()
    ZipFile(archive.toFile()).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            val destination = outputDir.resolve(entry.name).normalize()
            check(destination.startsWith(outputDir.normalize())) { "Archive entry escapes destination: ${entry.name}" }
            if (entry.isDirectory) destination.createDirectories() else {
                destination.createParentDirectories()
                zip.getInputStream(entry).use { input -> destination.outputStream().use(input::copyTo) }
            }
        }
    }
    val include = outputDir.resolve("include")
    val headers = interopDir.listDirectoryEntries("*.h").sorted()
    headers.forEach { header ->
        Files.copy(header, include.resolve(header.fileName), StandardCopyOption.REPLACE_EXISTING)
    }
    val includes = listOf(include, include.resolve("rocksdb"), include.resolve("rocksdb/rocksdb"))
    val definition = interopDir.resolve("rocksdb.def").readLines().filterNot { it.startsWith("compilerOpts =") }
    defFile.writeText(buildString {
        // Cinterop tracks the definition, but not external library/header directories.
        // Changing either the pinned archive or a custom header must invalidate it.
        appendLine("# RocksDB archive SHA-256: $expectedSha")
        headers.forEach { appendLine("# ${it.fileName} SHA-256: ${it.sha256()}") }
        definition.forEach { appendLine(it) }
        appendLine("compilerOpts = " + includes.joinToString(" ") { "\"-I${it.portableAbsolutePath()}\"" })
        appendLine("libraryPaths = \"${outputDir.resolve("lib").portableAbsolutePath()}\"")
    })
}

private fun Path.portableAbsolutePath() = absolutePathString().replace('\\', '/')

private fun Path.hasSha(expected: String) = sha256().equals(expected, ignoreCase = true)

private fun Path.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    inputStream().use { input ->
        val buffer = ByteArray(8192)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            digest.update(buffer, 0, count)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

/** Explicit maintenance command: download every archive before updating any pins. */
@TaskAction
fun updateRocksdbShas(
    archives: Map<String, String>,
    @Output propertiesFile: Path,
    @Output downloadDir: Path,
) {
    val original = propertiesFile.readText()
    val properties = Properties().apply { original.reader().use { load(it) } }
    val baseUrl = properties.getProperty("rocksdbPrebuiltBaseUrl").trimEnd('/')
    val version = properties.getProperty("rocksdbPrebuiltVersion")
    downloadDir.createDirectories()
    val checksums = archives.mapValues { (_, archiveName) ->
        val archive = downloadDir.resolve(archiveName)
        download("$baseUrl/$version/$archiveName", archive)
        archive.sha256()
    }
    var updated = original
    checksums.forEach { (target, sha) ->
        val pattern = Regex("(?m)^rocksdbPrebuiltSha\\.${Regex.escape(target)}=.*$")
        require(pattern.containsMatchIn(updated)) { "Missing checksum property for $target" }
        updated = updated.replace(pattern, "rocksdbPrebuiltSha.$target=$sha")
    }
    val temporary = Files.createTempFile(propertiesFile.parent, "rocksdb-pins", ".tmp")
    try {
        temporary.writeText(updated)
        Files.move(temporary, propertiesFile, StandardCopyOption.REPLACE_EXISTING)
    } finally {
        temporary.deleteIfExists()
    }
}

private fun download(url: String, destination: Path) {
    repeat(3) { attempt ->
        val temporary = Files.createTempFile(destination.parent, "rocksdb", ".tmp")
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 30_000
            connection.readTimeout = 30_000
            check(connection.responseCode in 200..299) { "Download failed: HTTP ${connection.responseCode}: $url" }
            connection.inputStream.use { input -> temporary.outputStream().use(input::copyTo) }
            Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING)
            return
        } catch (failure: Exception) {
            if (attempt == 2) throw failure
            Thread.sleep(2_000)
        } finally {
            connection.disconnect()
            temporary.deleteIfExists()
        }
    }
}
