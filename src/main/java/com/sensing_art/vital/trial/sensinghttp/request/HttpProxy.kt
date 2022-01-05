package com.sensing_art.vital.trial.sensinghttp.request

import com.sensing_art.vital.trial.sensinghttp.HttpRequest
import com.sensing_art.vital.trial.sensinghttp.data.MessageResponse
import com.sensing_art.vital.trial.sensinghttp.utils.UNKNOWN_HOST_CODE
import com.sensing_art.vital.trial.sensinghttp.data.ResponseData
import com.sensing_art.vital.trial.sensinghttp.engine.EngineFactory
import com.sensing_art.vital.trial.sensinghttp.engine.IHttpEngine
import kotlinx.serialization.json.Json


class HttpProxy(var builder: HttpRequest.Builder) {
    private var engine: IHttpEngine = EngineFactory.createEngine()

    init {
        engine.initConfig(builder)
    }

    fun  execute(
        uploadCallback: ((current: Long, total: Long) -> Unit)? = null
    ): ResponseData {
        if (builder.url.isNullOrEmpty()) {
            return getResponseData(
                UNKNOWN_HOST_CODE, "url is null or empty")
        } else if (!builder.url!!.startsWith("http://") and !builder.url!!.startsWith("https://")) {
            return getResponseData(
                UNKNOWN_HOST_CODE, "url is error")
        }
        val responseData = engine.execute(uploadCallback)
        return getResponseData(responseData.code, responseData.data)
    }

    /**
     * getResponseData
     *
     * @param code
     * @param message
     * @return
    </T> */
    private fun getResponseData(
        code: Int,
        message: String
    ): ResponseData {
        val parsedJson = Json { isLenient = true; ignoreUnknownKeys = true }.decodeFromString(
            MessageResponse.serializer(),
            message
        )
        return ResponseData(code, message, parsedJson)
    }
}