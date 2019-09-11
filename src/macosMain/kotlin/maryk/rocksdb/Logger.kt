package maryk.rocksdb

import kotlinx.cinterop.CPointer
import maryk.rocksdb.OptionsType.WITH_DBOPTIONS
import maryk.rocksdb.OptionsType.WITH_OPTIONS

private enum class OptionsType {
    WITH_DBOPTIONS, WITH_OPTIONS
}

actual abstract class Logger private constructor(
    vararg nativeParameterHandles: CPointer<*>
) : RocksCallbackObject(*nativeParameterHandles) {
    private lateinit var optionsType: OptionsType

    actual constructor(options: Options) : this(options.nativeHandle) {
        this.optionsType = WITH_OPTIONS
    }

    actual constructor(dboptions: DBOptions) : this(dboptions.nativeHandle) {
        this.optionsType = WITH_DBOPTIONS
    }

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel) {
    }

    actual fun infoLogLevel(): InfoLogLevel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    protected actual abstract fun log(infoLogLevel: InfoLogLevel, logMsg: String)

    override fun initializeNative(vararg nativeParameterHandles: CPointer<*>) = when(this.optionsType) {
        WITH_OPTIONS -> createNewLoggerOptions(nativeParameterHandles[0])
        WITH_DBOPTIONS -> createNewLoggerDbOptions(nativeParameterHandles[0])
    }
}

private fun createNewLoggerDbOptions(cPointer: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun createNewLoggerOptions(cPointer: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
