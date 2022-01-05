package com.sensing_art.vital.trial.sensinghttp

import com.sensing_art.vital.trial.sensinghttp.engine.EngineFactory
import com.sensing_art.vital.trial.sensinghttp.engine.IHttpEngine
import com.sensing_art.vital.trial.sensinghttp.utils.RequestConfig


class HttpAgentClient private constructor(){
    internal constructor(builder: Builder):this(){
        EngineFactory.setEngineClass(builder.engineClass)
        RequestConfig.connectTimeout = builder.connectTimeOut
        RequestConfig.readTimeout = builder.readTimeOut
    }

    companion object{
        private var builder: Builder?=null
        fun newBuilder(): Builder {
            if(builder == null){
                builder = Builder()
            }
            return builder!!
        }
    }
    class Builder{
        internal var engineClass: Class<out IHttpEngine>?=null
        internal var connectTimeOut:Int = RequestConfig.connectTimeout
        internal var readTimeOut:Int = RequestConfig.readTimeout

        fun setAgent(engineClass: Class<out IHttpEngine>): Builder {
            this.engineClass = engineClass
            return this
        }

        fun setConnectTimeOut(time:Int): Builder {
            connectTimeOut = time
            return this
        }

        fun setReadTimeOut(time: Int): Builder {
            readTimeOut = time
            return this
        }

        fun build(): HttpAgentClient {
            return HttpAgentClient(this)
        }
    }
}