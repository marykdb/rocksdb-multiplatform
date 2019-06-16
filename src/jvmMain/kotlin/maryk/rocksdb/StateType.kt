package maryk.rocksdb

actual typealias StateType = org.rocksdb.StateType

actual fun getStateName(stateType: StateType): String = ThreadStatus.getStateName(stateType)
