package com.sensing_art.vital.trial.sensinghttp

import com.sensing_art.vital.trial.sensinghttp.data.HttpMethod
import com.sensing_art.vital.trial.sensinghttp.data.ResponseData
import com.sensing_art.vital.trial.sensinghttp.engine.NativeHttpEngine
import com.sensing_art.vital.trial.sensinghttp.request.HttpBody
import com.sensing_art.vital.trial.sensinghttp.request.HttpCallBack
import com.sensing_art.vital.trial.sensinghttp.request.HttpProxy
import com.sensing_art.vital.trial.sensinghttp.request.RequestExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 * 共通通信API
 */

typealias ProgressCallback = (current: Long, total: Long) -> Unit

class HttpRequest internal constructor(var builder: Builder) :
    RequestExecutor {

    override fun <T : ResponseData> executeAsync(
        httpCallBack: HttpCallBack<T>,
        progressCallback: ProgressCallback?
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            val result = HttpProxy(builder)
                .execute(uploadCallback = { current, total ->
                CoroutineScope(Dispatchers.Main).launch {
                    progressCallback?.invoke(current, total)
                }
            })
           CoroutineScope(Dispatchers.Main).launch {
                httpCallBack.onReceivedData(result)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newBuilder(): Builder {
            HttpAgentClient.newBuilder()
                .setAgent(NativeHttpEngine::class.java)
                .setConnectTimeOut(10000)
                .setReadTimeOut(10000)
                .build()
            return Builder()
        }
    }

    class Builder {
        internal var url: String? = null
        internal var method: HttpMethod = HttpMethod.GET
        internal var header: Map<String, String> = mapOf()
        internal var body: HttpBody? = null
        internal var urlParams: Map<String, Any>? = null
        internal var isDownFile = false

        fun setUrl(url: String): Builder {
            this.url = url
            return this
        }

        fun setMethod(method: HttpMethod): Builder {
            this.method = method
            return this
        }

        fun setHeader(header: Map<String, String>): Builder {
            this.header = header
            return this
        }

        fun setBody(body: HttpBody): Builder {
            this.body = body
            return this
        }


        fun build(): HttpRequest {
            return HttpRequest(this)
        }
    }
}