package com.sensing_art.vital.trial.sensinghttp.engine


object EngineFactory {
    private var engineClass: Class<out IHttpEngine>? = null
    fun setEngineClass(engineClass: Class<out IHttpEngine>?) {
        EngineFactory.engineClass = engineClass
    }

    fun createEngine(): IHttpEngine {
        if (engineClass != null) {
            try {
                return engineClass!!.newInstance()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
        return NativeHttpEngine()
    }
}
