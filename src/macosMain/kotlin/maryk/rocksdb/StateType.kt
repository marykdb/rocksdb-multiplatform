package maryk.rocksdb

actual enum class StateType(
    private val value: Byte
) {
    STATE_UNKNOWN(0),
    STATE_MUTEX_WAIT(1);
}

actual fun getStateName(stateType: StateType): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
