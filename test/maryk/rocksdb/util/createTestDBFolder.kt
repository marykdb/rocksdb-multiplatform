package maryk.rocksdb.util

import maryk.createFolder
import kotlin.random.Random
import kotlin.random.nextUInt

private const val TEST_DB_ROOT = "build/test-database"

@Suppress("EXPERIMENTAL_API_USAGE")
fun createTestDBFolder(name: String?): String {
    createFolder(TEST_DB_ROOT)
    return "$TEST_DB_ROOT/${name!!}_" + Random.nextUInt()
}
