package maryk.rocksdb

import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.intoByteArray
import maryk.toByteArray
import maryk.toNSData
import maryk.wrapWithErrorThrower
import maryk.wrapWithNullErrorThrower
import platform.Foundation.NSData
import rocksdb.RocksDBCompactRangeOptions
import rocksdb.RocksDBDefaultColumnFamilyName
import rocksdb.RocksDBIterator
import rocksdb.RocksDBKeyRange
import rocksdb.RocksDBOptions
import rocksdb.applyWriteBatch
import rocksdb.columnFamilyMetaData
import rocksdb.compactRange
import rocksdb.continueBackgroundWork
import rocksdb.createColumnFamilyWithName
import rocksdb.dataForKey
import rocksdb.defaultColumnFamily
import rocksdb.deleteDataForKey
import rocksdb.deleteFile
import rocksdb.deleteRange
import rocksdb.disableFileDeletions
import rocksdb.dropColumnFamilies
import rocksdb.dropColumnFamily
import rocksdb.enableAutoCompaction
import rocksdb.enableFileDelections
import rocksdb.env
import rocksdb.flushWal
import rocksdb.iterator
import rocksdb.iteratorOverColumnFamily
import rocksdb.iteratorWithReadOptions
import rocksdb.iteratorsOverColumnFamilies
import rocksdb.iteratorsWithReadOptions
import rocksdb.keyMayExist
import rocksdb.latestSequenceNumber
import rocksdb.level0StopWriteTrigger
import rocksdb.level0StopWriteTriggerInColumnFamily
import rocksdb.listColumnFamiliesInDatabaseAtPath
import rocksdb.maxMemCompactionLevel
import rocksdb.maxMemCompactionLevelInColumnFamily
import rocksdb.mergeData
import rocksdb.multiGet
import rocksdb.name
import rocksdb.numberLevels
import rocksdb.numberLevelsInColumnFamily
import rocksdb.pauseBackgroundWork
import rocksdb.promoteL0
import rocksdb.resetStats
import rocksdb.setData
import rocksdb.setPreserveDeletesSequenceNumber
import rocksdb.snapshot
import rocksdb.syncWal
import rocksdb.valueForIntProperty
import rocksdb.valueForMapProperty
import rocksdb.valueForProperty
import rocksdb.verifyChecksum

actual val defaultColumnFamily = RocksDBDefaultColumnFamilyName.encodeToByteArray()
actual val rocksDBNotFound = -1

actual open class RocksDB
    internal constructor(internal val native: rocksdb.RocksDB = rocksdb.RocksDB())
: RocksObject() {
    actual override fun close() {
        native.close()
        super.close()
    }

    actual fun closeE() = wrapWithErrorThrower { error ->
        native.close(error)
        super.close()
    }

    actual fun createColumnFamily(columnFamilyDescriptor: ColumnFamilyDescriptor): ColumnFamilyHandle {
        return wrapWithErrorThrower { error ->
            val columnFamilyHandle = native.createColumnFamilyWithName(
                columnFamilyDescriptor.getName().decodeToString(),
                columnFamilyDescriptor.getOptions().native,
                error
            ) ?: throw RocksDBException("Column Family ${columnFamilyDescriptor.getName()} could not be created")

            ColumnFamilyHandle(columnFamilyHandle)
        }
    }

    actual fun createColumnFamilies(
        columnFamilyOptions: ColumnFamilyOptions,
        columnFamilyNames: List<ByteArray>
    ): List<ColumnFamilyHandle> {
        return wrapWithErrorThrower { error ->
            val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()

            for (name in columnFamilyNames) {
                native.createColumnFamilyWithName(
                    name.decodeToString(),
                    columnFamilyOptions.native,
                    error
                )?.let {
                    columnFamilyHandles.add(
                        ColumnFamilyHandle(it)
                    )
                }
            }

            columnFamilyHandles
        }
    }

    actual fun createColumnFamilies(columnFamilyDescriptors: List<ColumnFamilyDescriptor>): List<ColumnFamilyHandle> {
        return wrapWithErrorThrower { error ->
            val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()

            for (descriptor in columnFamilyDescriptors) {
                native.createColumnFamilyWithName(
                    descriptor.getName().decodeToString(),
                    descriptor.getOptions().native,
                    error
                )?.let {
                    columnFamilyHandles.add(
                        ColumnFamilyHandle(it)
                    )
                }
            }

            columnFamilyHandles
        }
    }

    actual fun dropColumnFamily(columnFamilyHandle: ColumnFamilyHandle) {
        wrapWithErrorThrower { error ->
            native.dropColumnFamily(columnFamilyHandle.native, error)
        }
    }

    actual fun dropColumnFamilies(columnFamilies: List<ColumnFamilyHandle>) {
        wrapWithErrorThrower { error ->
            native.dropColumnFamilies(columnFamilies.map { it.native }, error)
        }
    }

    actual fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            native.setData(value.toNSData(), key.toNSData(), error)
        }
    }

    actual fun put(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.setData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                error
            )
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            native.setData(
                value.toNSData(),
                key.toNSData(),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.setData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun put(writeOpts: WriteOptions, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            native.setData(
                value.toNSData(),
                key.toNSData(),
                writeOpts.native,
                error
            )
        }
    }

    actual fun put(
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.setData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                writeOpts.native,
                error
            )
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        value: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            native.setData(
                value.toNSData(),
                key.toNSData(),
                columnFamilyHandle.native,
                writeOpts.native,
                error
            )
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.setData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                columnFamilyHandle.native,
                writeOpts.native,
                error
            )
        }
    }

    actual fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(),
                error
            )
        }
    }

    actual fun delete(key: ByteArray, offset: Int, len: Int) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(offset, len),
                error
            )
        }
    }

    actual fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(offset, len),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun delete(writeOpt: WriteOptions, key: ByteArray) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(),
                writeOpt.native,
                error
            )
        }
    }

    actual fun delete(
        writeOpt: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(offset, len),
                writeOpt.native,
                error
            )
        }
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        key: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(),
                columnFamilyHandle.native,
                writeOpt.native,
                error
            )
        }
    }

    actual fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ) {
        wrapWithErrorThrower { error ->
            native.deleteDataForKey(
                key.toNSData(offset, len),
                columnFamilyHandle.native,
                writeOpt.native,
                error
            )
        }
    }

    actual fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            native.deleteRange(
                range,
                error
            )
        }
    }

    actual fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        beginKey: ByteArray,
        endKey: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            native.deleteRange(
                range,
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun deleteRange(writeOpt: WriteOptions, beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            native.deleteRange(
                range,
                writeOpt.native,
                error
            )
        }
    }

    actual fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions,
        beginKey: ByteArray,
        endKey: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            native.deleteRange(
                range,
                writeOpt.native,
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(),
                key.toNSData(),
                error
            )
        }
    }

    actual fun merge(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                error
            )
        }
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(),
                key.toNSData(),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun merge(writeOpts: WriteOptions, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(),
                key.toNSData(),
                writeOpts.native,
                error
            )
        }
    }

    actual fun merge(
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                writeOpts.native,
                error
            )
        }
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        value: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(),
                key.toNSData(),
                columnFamilyHandle.native,
                writeOpts.native,
                error
            )
        }
    }

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) {
        wrapWithErrorThrower { error ->
            native.mergeData(
                value.toNSData(vOffset, vLen),
                key.toNSData(offset, len),
                columnFamilyHandle.native,
                writeOpts.native,
                error
            )
        }
    }

    actual fun write(writeOpts: WriteOptions, updates: WriteBatch) {
        wrapWithErrorThrower { error ->
            native.applyWriteBatch(
                updates.native,
                writeOpts.native,
                error
            )
        }
    }

    actual fun write(writeOpts: WriteOptions, updates: WriteBatchWithIndex) {
        wrapWithErrorThrower { error ->
            native.applyWriteBatch(
                updates.native,
                writeOpts.native,
                error
            )
        }
    }

    actual fun get(key: ByteArray, value: ByteArray) = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(),
            error
        )?.intoByteArray(value)
    } ?: rocksDBNotFound

    actual fun get(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ) = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            error
        )?.intoByteArray(value, vOffset, vLen)
    } ?: rocksDBNotFound

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ): Int = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(),
            columnFamilyHandle.native,
            error
        )?.intoByteArray(value)
    } ?: rocksDBNotFound

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            columnFamilyHandle.native,
            error
        )?.intoByteArray(value, vOffset, vLen)
    } ?: rocksDBNotFound

    actual fun get(opt: ReadOptions, key: ByteArray, value: ByteArray): Int = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(),
            opt.native,
            error
        )?.intoByteArray(value)
    } ?: rocksDBNotFound

    actual fun get(
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            opt.native,
            error
        )?.intoByteArray(value, vOffset, vLen)
    } ?: rocksDBNotFound

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        value: ByteArray
    ): Int = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(),
            columnFamilyHandle.native,
            opt.native,
            error
        )?.intoByteArray(value)
    } ?: rocksDBNotFound

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: ByteArray,
        vOffset: Int,
        vLen: Int
    ): Int = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            columnFamilyHandle.native,
            opt.native,
            error
        )?.intoByteArray(value, vOffset, vLen)
    } ?: rocksDBNotFound

    actual operator fun get(key: ByteArray) = wrapWithNullErrorThrower { error ->
        native.dataForKey(key.toNSData(), error)?.toByteArray()
    }

    actual fun get(key: ByteArray, offset: Int, len: Int) = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            error
        )?.toByteArray()
    }

    actual fun get(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray): ByteArray? = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(),
            columnFamilyHandle.native,
            error
        )?.toByteArray()
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            columnFamilyHandle.native,
            error
        )?.toByteArray()
    }

    actual fun get(opt: ReadOptions, key: ByteArray) = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(),
            opt.native,
            error
        )?.toByteArray()
    }

    actual fun get(
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ) = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            opt.native,
            error
        )?.toByteArray()
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray
    ): ByteArray? = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(),
            columnFamilyHandle.native,
            opt.native,
            error
        )?.toByteArray()
    }

    actual fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int
    ): ByteArray? = wrapWithNullErrorThrower { error ->
        native.dataForKey(
            key.toNSData(offset, len),
            columnFamilyHandle.native,
            opt.native,
            error
        )?.toByteArray()
    }

    actual fun multiGetAsList(keys: List<ByteArray>): List<ByteArray?> {
        assert(keys.isNotEmpty())
        @Suppress("UNCHECKED_CAST")
        return (native.multiGet(keys.map { it.toNSData() }) as List<NSData>).map {
            if (it.length == 0uL) null else it.toByteArray()
        }
    }

    actual fun multiGetAsList(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> {
        assert(keys.isNotEmpty())
        // Check if key size equals cfList size. If not a exception must be thrown. If not a Segmentation fault happens.
        if (keys.size != columnFamilyHandleList.size) {
            throw IllegalArgumentException("For each key there must be a ColumnFamilyHandle.")
        }
        @Suppress("UNCHECKED_CAST")
        return (native.multiGet(
            keys.map { it.toNSData() },
            columnFamilyHandleList.map { it.native }
        ) as List<NSData>).map {
            if (it.length == 0uL) null else it.toByteArray()
        }
    }

    actual fun multiGetAsList(
        opt: ReadOptions,
        keys: List<ByteArray>
    ): List<ByteArray?> {
        @Suppress("UNCHECKED_CAST")
        return (native.multiGet(
            keys.map { it.toNSData() },
            opt.native
        ) as List<NSData>).map {
            if (it.length == 0uL) null else it.toByteArray()
        }
    }

    actual fun multiGetAsList(
        opt: ReadOptions,
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> {
        @Suppress("UNCHECKED_CAST")
        return (native.multiGet(
            keys.map { it.toNSData() },
            columnFamilyHandleList.map { it.native },
            opt.native
        ) as List<NSData>).map {
            if (it.length == 0uL) null else it.toByteArray()
        }
    }

    actual fun keyMayExist(key: ByteArray, value: StringBuilder): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(key.toNSData(), valueStringRef.ptr).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun keyMayExist(
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(
            key.toNSData(offset, len),
            valueStringRef.ptr
        ).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: StringBuilder
    ): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(
            key.toNSData(),
            columnFamilyHandle.native,
            valueStringRef.ptr
        ).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(
            key.toNSData(offset, len),
            columnFamilyHandle.native,
            valueStringRef.ptr
        ).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray,
        value: StringBuilder
    ): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(
            key.toNSData(),
            readOptions.native,
            valueStringRef.ptr
        ).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(
            key.toNSData(offset, len),
            readOptions.native,
            valueStringRef.ptr
        ).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: StringBuilder
    ): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(
            key.toNSData(),
            columnFamilyHandle.native,
            readOptions.native,
            valueStringRef.ptr
        ).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun keyMayExist(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        offset: Int,
        len: Int,
        value: StringBuilder
    ): Boolean {
        val valueStringRef = nativeHeap.alloc<ObjCObjectVar<String?>>()
        return native.keyMayExist(
            key.toNSData(offset, len),
            columnFamilyHandle.native,
            readOptions.native,
            valueStringRef.ptr
        ).also {
            value.append(valueStringRef.value)
        }
    }

    actual fun newIterator() = RocksIterator(native.iterator())

    actual fun newIterator(readOptions: ReadOptions) = RocksIterator(
        native.iteratorWithReadOptions(readOptions.native)
    )

    actual fun newIterator(columnFamilyHandle: ColumnFamilyHandle) = RocksIterator(
        native.iteratorOverColumnFamily(columnFamilyHandle.native)
    )

    actual fun newIterator(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions
    ) = RocksIterator(
        native.iteratorWithReadOptions(readOptions.native, columnFamilyHandle.native)
    )

    actual fun newIterators(columnFamilyHandleList: List<ColumnFamilyHandle>) = wrapWithErrorThrower { error ->
        @Suppress("UNCHECKED_CAST")
        val iterators = native.iteratorsOverColumnFamilies(
            columnFamilyHandleList.map { it.native },
            error
        ) as List<RocksDBIterator>
        iterators.map {
            RocksIterator(it)
        }
    }

    actual fun newIterators(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        readOptions: ReadOptions
    ) = wrapWithErrorThrower { error ->
        @Suppress("UNCHECKED_CAST")
        val iterators = native.iteratorsWithReadOptions(
            readOptions.native,
            columnFamilyHandleList.map { it.native },
            error
        ) as List<RocksDBIterator>
        iterators.map {
            RocksIterator(it)
        }
    }

    actual fun getSnapshot(): Snapshot? = Snapshot(native.snapshot())

    actual fun releaseSnapshot(snapshot: Snapshot) {
        snapshot.native.close()
    }

    actual fun getProperty(
        columnFamilyHandle: ColumnFamilyHandle,
        property: String
    ): String? {
        return native.valueForProperty(property, columnFamilyHandle.native)
    }

    actual fun getProperty(property: String) = native.valueForProperty(property)

    actual fun getMapProperty(property: String): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        return native.valueForMapProperty(property) as Map<String, String>
    }

    actual fun getMapProperty(
        columnFamilyHandle: ColumnFamilyHandle,
        property: String
    ): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        return native.valueForMapProperty(property, columnFamilyHandle.native) as Map<String, String>
    }

    actual fun getLongProperty(property: String) =
        native.valueForIntProperty(property).toLong()

    actual fun getLongProperty(
        columnFamilyHandle: ColumnFamilyHandle,
        property: String
    ): Long {
        return native.valueForIntProperty(
            property,
            columnFamilyHandle.native
        ).toLong()
    }

    actual fun resetStats() {
        wrapWithErrorThrower { error ->
            native.resetStats(error)
        }
    }

    actual fun compactRange() {
        wrapWithErrorThrower { error ->
            native.compactRange(
                RocksDBKeyRange(),
                RocksDBCompactRangeOptions(),
                error
            )
        }
    }

    actual fun compactRange(columnFamilyHandle: ColumnFamilyHandle) {
        wrapWithErrorThrower { error ->
            native.compactRange(
                RocksDBKeyRange(),
                RocksDBCompactRangeOptions(),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun compactRange(begin: ByteArray, end: ByteArray) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = begin.toNSData()
            range.end = end.toNSData()
            native.compactRange(range, RocksDBCompactRangeOptions(), error)
        }
    }

    actual fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle,
        begin: ByteArray,
        end: ByteArray
    ) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = begin.toNSData()
            range.end = end.toNSData()
            native.compactRange(
                range,
                RocksDBCompactRangeOptions(),
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle,
        begin: ByteArray,
        end: ByteArray,
        compactRangeOptions: CompactRangeOptions
    ) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = begin.toNSData()
            range.end = end.toNSData()
            native.compactRange(
                range,
                compactRangeOptions.native,
                columnFamilyHandle.native,
                error
            )
        }
    }

    actual fun pauseBackgroundWork() {
        wrapWithErrorThrower { error ->
            native.pauseBackgroundWork(error)
        }
    }

    actual fun continueBackgroundWork() {
        wrapWithErrorThrower { error ->
            native.continueBackgroundWork(error)
        }
    }

    actual fun enableAutoCompaction(columnFamilyHandles: List<ColumnFamilyHandle>) {
        wrapWithErrorThrower { error ->
            native.enableAutoCompaction(columnFamilyHandles.map { it.native }, error)
        }
    }

    actual fun numberLevels(): Int {
        return native.numberLevels()
    }

    actual fun numberLevels(columnFamilyHandle: ColumnFamilyHandle): Int {
        return native.numberLevelsInColumnFamily(columnFamilyHandle.native)
    }

    actual fun maxMemCompactionLevel(): Int {
        return native.maxMemCompactionLevel()
    }

    actual fun maxMemCompactionLevel(columnFamilyHandle: ColumnFamilyHandle): Int {
        return native.maxMemCompactionLevelInColumnFamily(columnFamilyHandle.native)
    }

    actual fun level0StopWriteTrigger(): Int {
        return native.level0StopWriteTrigger()
    }

    actual fun level0StopWriteTrigger(columnFamilyHandle: ColumnFamilyHandle): Int {
        return native.level0StopWriteTriggerInColumnFamily(columnFamilyHandle.native)
    }

    actual fun getName(): String {
        return native.name
    }

    actual fun getEnv(): Env {
        return RocksEnv(native.env)
    }

    actual fun flushWal(sync: Boolean) {
        wrapWithErrorThrower { error ->
            native.flushWal(sync, error)
        }
    }

    actual fun syncWal() {
        wrapWithErrorThrower { error ->
            native.syncWal(error)
        }
    }

    actual fun getLatestSequenceNumber(): Long {
        return native.latestSequenceNumber.toLong()
    }

    actual fun setPreserveDeletesSequenceNumber(sequenceNumber: Long) =
        native.setPreserveDeletesSequenceNumber(sequenceNumber.toULong())

    actual fun disableFileDeletions() {
        wrapWithErrorThrower { error ->
            native.disableFileDeletions(error)
        }
    }

    actual fun enableFileDeletions(force: Boolean) {
        wrapWithErrorThrower { error ->
            native.enableFileDelections(force, error)
        }
    }

    actual fun deleteFile(name: String) {
        wrapWithErrorThrower { error ->
            native.deleteFile(name, error)
        }
    }

    actual fun getColumnFamilyMetaData(columnFamilyHandle: ColumnFamilyHandle): ColumnFamilyMetaData {
        return ColumnFamilyMetaData(native.columnFamilyMetaData(columnFamilyHandle.native))
    }

    actual fun GetColumnFamilyMetaData(): ColumnFamilyMetaData {
        return ColumnFamilyMetaData(native.columnFamilyMetaData())
    }

    actual fun verifyChecksum() {
        wrapWithErrorThrower { error ->
            native.verifyChecksum(error)
        }
    }

    actual fun getDefaultColumnFamily(): ColumnFamilyHandle {
        return ColumnFamilyHandle(native.defaultColumnFamily)
    }

    actual fun promoteL0(columnFamilyHandle: ColumnFamilyHandle, targetLevel: Int) {
        wrapWithErrorThrower { error ->
            native.promoteL0(targetLevel, columnFamilyHandle.native, error)
        }
    }

    actual fun promoteL0(targetLevel: Int) {
        wrapWithErrorThrower { error ->
            native.promoteL0(targetLevel, error)
        }
    }
}

actual fun destroyRocksDB(path: String, options: Options) {
    Unit.wrapWithErrorThrower { error ->
        rocksdb.RocksDB.destroyDatabaseAtPath(
            path,
            options.native,
            error
        )
    }
}

actual fun listColumnFamilies(
    options: Options,
    path: String
): List<ByteArray> {
    return Unit.wrapWithErrorThrower { error ->
        rocksdb.RocksDB.listColumnFamiliesInDatabaseAtPath(
            path,
            RocksDBOptions(),
            error
        ).map { (it as String).encodeToByteArray() }
    }
}
