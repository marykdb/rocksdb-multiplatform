package maryk.rocksdb

expect class LiveFiles {
    /**
     * The valid size of the manifest file. The manifest file is an ever growing
     * file, but only the portion specified here is valid for this snapshot.
     */
    val manifestFileSize: Long

    /**
     * The files are relative to the [.getName] and are not
     * absolute paths. Despite being relative paths, the file names begin
     * with "/".
     */
    val files: List<String>
}
