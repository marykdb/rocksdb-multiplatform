import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.*
import kotlin.test.*

class ProvisionRocksdbTest {
    private fun fixture(block: (Path, String) -> Unit) {
        val root = Files.createTempDirectory("rocksdb-plugin-test")
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/release/archive.zip") { exchange ->
            val bytes = root.resolve("archive.zip").readBytes()
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
        try {
            block(root, "http://127.0.0.1:${server.address.port}")
        } finally {
            server.stop(0)
            root.toFile().deleteRecursively()
        }
    }

    private fun archive(root: Path, entry: String = "include/c.h"): String {
        ZipOutputStream(root.resolve("archive.zip").outputStream()).use { zip ->
            zip.putNextEntry(ZipEntry(entry))
            zip.write("header".toByteArray())
            zip.closeEntry()
        }
        return MessageDigest.getInstance("SHA-256").digest(root.resolve("archive.zip").readBytes())
            .joinToString("") { "%02x".format(it) }
    }

    private fun properties(root: Path, url: String, sha: String): Path = root.resolve("pins.properties").apply {
        writeText("rocksdbPrebuiltBaseUrl=$url\nrocksdbPrebuiltVersion=release\nrocksdbPrebuiltSha.test=$sha\n")
    }

    private fun provision(root: Path, properties: Path) {
        val interop = root.resolve("interop").createDirectories()
        interop.resolve("rocksdb.def").writeText("headers = c.h custom.h\ncompilerOpts = -Iold\n")
        interop.resolve("custom.h").writeText("custom header")
        provisionRocksdb("test", "archive.zip", properties, interop, root.resolve("bundle"), root.resolve("rocksdb.def"))
    }

    @Test fun provisionsVerifiedArchiveAndInvalidatesStaleHeaders() = fixture { root, url ->
        val sha = archive(root)
        root.resolve("bundle/include").createDirectories().resolve("obsolete.h").writeText("old")
        provision(root, properties(root, url, sha))
        assertFalse(root.resolve("bundle/include/obsolete.h").exists())
        assertEquals("header", root.resolve("bundle/include/c.h").readText())
        assertEquals("custom header", root.resolve("bundle/include/custom.h").readText())
        val definition = root.resolve("rocksdb.def").readText()
        assertContains(definition, "# RocksDB archive SHA-256: $sha")
        assertContains(definition, "# custom.h SHA-256:")
        assertFalse(definition.contains("-Iold"))
    }

    @Test fun rejectsMismatchedChecksumBeforeExtraction() = fixture { root, url ->
        archive(root)
        assertFailsWith<IllegalStateException> { provision(root, properties(root, url, "0".repeat(64))) }
        assertFalse(root.resolve("bundle/include").exists())
    }

    @Test fun rejectsArchivePathTraversal() = fixture { root, url ->
        val sha = archive(root, "../escaped.h")
        assertFailsWith<IllegalStateException> { provision(root, properties(root, url, sha)) }
        assertFalse(root.resolve("escaped.h").exists())
    }

    @Test fun updatesPinsOnlyAfterEveryPropertyCanBeUpdated() = fixture { root, url ->
        val sha = archive(root)
        val pins = properties(root, url, "0".repeat(64))
        val original = pins.readText()
        assertFailsWith<IllegalArgumentException> {
            updateRocksdbShas(mapOf("test" to "archive.zip", "missing" to "archive.zip"), pins, root.resolve("downloads"))
        }
        assertEquals(original, pins.readText())
        updateRocksdbShas(mapOf("test" to "archive.zip"), pins, root.resolve("downloads"))
        assertContains(pins.readText(), "rocksdbPrebuiltSha.test=$sha")
    }
}
