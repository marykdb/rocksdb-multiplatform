package maryk.rocksdb

actual enum class CompactionReason(
    internal val value: Byte
) {
    kUnknown(0x0),
    kLevelL0FilesNum(0x1),
    kLevelMaxLevelSize(0x2),
    kUniversalSizeAmplification(0x3),
    kUniversalSizeRatio(0x4),
    kUniversalSortedRunNum(0x5),
    kFIFOMaxSize(0x6),
    kFIFOReduceNumFiles(0x7),
    kFIFOTtl(0x8),
    kManualCompaction(0x9),
    kFilesMarkedForCompaction(0x10),
    kBottommostFiles(0x0A),
    kTtl(0x0B),
    kFlush(0x0C),
    kExternalSstIngestion(0x0D);
}
