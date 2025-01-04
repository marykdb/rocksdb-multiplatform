package maryk.rocksdb.util

import maryk.createFolder
import kotlin.random.Random
import kotlin.random.nextUInt

@Suppress("EXPERIMENTAL_API_USAGE")
fun createTestDBFolder(name: String?) =
    ("build/test-database/${name!!}_" + Random.nextUInt()).also { createFolder(it) }
