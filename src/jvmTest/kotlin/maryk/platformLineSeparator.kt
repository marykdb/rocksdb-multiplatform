package maryk

import org.rocksdb.util.Environment

actual val systemLineSeparator: String = if (Environment.isWindows()) "\n" else System.getProperty("line.separator")
