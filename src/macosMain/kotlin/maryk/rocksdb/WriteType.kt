package maryk.rocksdb

import maryk.rocksdb.WriteType.DELETE
import maryk.rocksdb.WriteType.DELETE_RANGE
import maryk.rocksdb.WriteType.LOG
import maryk.rocksdb.WriteType.MERGE
import maryk.rocksdb.WriteType.PUT
import maryk.rocksdb.WriteType.SINGLE_DELETE
import maryk.rocksdb.WriteType.XID
import rocksdb.RocksDBWriteBatchEntryType
import rocksdb.RocksDBWriteBatchEntryType.RocksDBWriteBatchEntryTypeDeleteRangeRecord
import rocksdb.RocksDBWriteBatchEntryType.RocksDBWriteBatchEntryTypeDeleteRecord
import rocksdb.RocksDBWriteBatchEntryType.RocksDBWriteBatchEntryTypeLogDataRecord
import rocksdb.RocksDBWriteBatchEntryType.RocksDBWriteBatchEntryTypeMergeRecord
import rocksdb.RocksDBWriteBatchEntryType.RocksDBWriteBatchEntryTypePutRecord
import rocksdb.RocksDBWriteBatchEntryType.RocksDBWriteBatchEntryTypeSingleDeleteRecord
import rocksdb.RocksDBWriteBatchEntryType.RocksDBWriteBatchEntryTypeXIDRecord

actual enum class WriteType(
    internal val value: RocksDBWriteBatchEntryType
) {
    PUT(RocksDBWriteBatchEntryTypePutRecord),
    MERGE(RocksDBWriteBatchEntryTypeMergeRecord),
    DELETE(RocksDBWriteBatchEntryTypeDeleteRecord),
    SINGLE_DELETE(RocksDBWriteBatchEntryTypeSingleDeleteRecord),
    DELETE_RANGE(RocksDBWriteBatchEntryTypeDeleteRangeRecord),
    LOG(RocksDBWriteBatchEntryTypeLogDataRecord),
    XID(RocksDBWriteBatchEntryTypeXIDRecord);
}

internal fun getWriteTypeByValue(value: RocksDBWriteBatchEntryType): WriteType {
    return when (value) {
        RocksDBWriteBatchEntryTypePutRecord -> PUT
        RocksDBWriteBatchEntryTypeMergeRecord -> MERGE
        RocksDBWriteBatchEntryTypeDeleteRecord -> DELETE
        RocksDBWriteBatchEntryTypeSingleDeleteRecord -> SINGLE_DELETE
        RocksDBWriteBatchEntryTypeDeleteRangeRecord -> DELETE_RANGE
        RocksDBWriteBatchEntryTypeLogDataRecord -> LOG
        RocksDBWriteBatchEntryTypeXIDRecord -> XID
    }
}
