package maryk.rocksdb

/**
 * The type used to refer to a thread state.
 *
 * A state describes lower-level action of a thread
 * such as reading / writing a file or waiting for a mutex.
 */
expect enum class StateType {
    STATE_UNKNOWN,
    STATE_MUTEX_WAIT;
}

/**
 * Obtain the name of a state given its type.
 *
 * @param stateType the state type.
 *
 * @return the name of the state.
 */
expect fun getStateName(stateType: StateType): String
