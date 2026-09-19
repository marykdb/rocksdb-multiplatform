package maryk.rocksdb

/**
 * Enum CompressionType
 *
 * DB contents are stored in a set of blocks, each of which holds a
 * sequence of key,value pairs. Each block may be compressed before
 * being stored in a file. The following enum describes which
 * compression method (if any) is used to compress a block.
 */
expect enum class CompressionType {
  NO_COMPRESSION,
  SNAPPY_COMPRESSION,
  ZLIB_COMPRESSION,
  BZLIB2_COMPRESSION,
  LZ4_COMPRESSION,
  LZ4HC_COMPRESSION,
  XPRESS_COMPRESSION,
  ZSTD_COMPRESSION,
  DISABLE_COMPRESSION_OPTION;

  /**
   * Returns the library name of the compression type
   * identified by the enumeration value.
   */
  fun getLibraryName(): String?
}

/**
 * Get the CompressionType enumeration value by
 * passing the library name to this method.
 *
 * If library cannot be found the enumeration
 * value `NO_COMPRESSION` will be returned.
 *
 * @param libraryName compression library name.
 *
 * @return CompressionType instance.
 */
expect fun getCompressionType(libraryName: String?): CompressionType

/**
 * Get the CompressionType enumeration value by
 * passing the byte identifier to this method.
 *
 * @param byteIdentifier of CompressionType.
 *
 * @return CompressionType instance.
 *
 * @throws IllegalArgumentException If CompressionType cannot be found for the
 * provided byteIdentifier
 */
expect fun getCompressionType(byteIdentifier: Byte): CompressionType
