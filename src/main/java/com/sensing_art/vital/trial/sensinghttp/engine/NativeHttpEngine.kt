package com.sensing_art.vital.trial.sensinghttp.engine

import com.sensing_art.vital.trial.sensinghttp.HttpRequest
import com.sensing_art.vital.trial.sensinghttp.data.HttpMethod
import com.sensing_art.vital.trial.sensinghttp.data.ResponseData

import com.sensing_art.vital.trial.sensinghttp.utils.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class NativeHttpEngine : IHttpEngine {
    private var mConnection: HttpURLConnection? = null
    private lateinit var builder: HttpRequest.Builder
    private var responseCode =
        UNKNOWN_EXCEPTION_CODE
    private var responseMessage = ""
    private var connSuccess = false

    override fun initConfig(builder: HttpRequest.Builder) {
        this.builder = builder
    }

    override fun execute(uploadCallback: ((current: Long, total: Long) -> Unit)?): ResponseData {
        openConnection()
        commonConfig()
        headerConfig()
        return request()
    }

    private fun openConnection() {
        try {
            val url = URL(builder.url + getUrlParams())
            mConnection = url.openConnection() as HttpURLConnection?
            if (mConnection == null) {
                responseCode = CONNECTION_FAIL_CODE
            } else {
                connSuccess = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUrlParams(): String {
        return builder.urlParams?.map {
            "${it.key}=${it.value}"
        }?.fold("&", { first, second ->
            "$first&$second"
        }) ?: ""
    }

    private fun commonConfig() {
        if (connSuccess) {
            mConnection?.requestMethod = builder.method.method
            mConnection?.connectTimeout =
                RequestConfig.connectTimeout
            mConnection?.readTimeout =
                RequestConfig.readTimeout
        }
    }

    private fun headerConfig() {
        if (connSuccess) {
            builder.header.forEach { (k, v) ->
                mConnection?.setRequestProperty(k, v)
            }
            if(builder.isDownFile){
                mConnection?.setRequestProperty("Accept-Encoding", "identity")
            }
        }
    }

    private fun request(): ResponseData {
        if (!connSuccess) {
            //　接続失敗の場合
            return getResponseData(
                responseCode,
                ""
            )
        }
        var inputStream: InputStream? = null
        var inputStreamReader: InputStreamReader? = null
        var reader: BufferedReader? = null
        var tempLine: String?
        val resultBuffer = StringBuilder()
        try {
            mConnection?.apply {
                useCaches = false
                doInput = true
                setRequestProperty("Connection", "keep-alive")
            }
            when (builder.method) {
                HttpMethod.POST -> {
                    doPost(mConnection!!)
                }
                HttpMethod.GET -> {
                    doGet(mConnection!!)
                }
            }

            responseCode = mConnection!!.responseCode
            if (responseCode >= 300) {
                inputStream = mConnection!!.errorStream
                if (inputStream != null) {
                    inputStreamReader = InputStreamReader(inputStream,
                        CHARSET
                    )
                    reader = BufferedReader(inputStreamReader)
                    while (reader.readLine().also { tempLine = it } != null) {
                        resultBuffer.append(tempLine)
                    }
                } else {
                    resultBuffer.append(mConnection!!.responseMessage)
                }
            } else {
                inputStream = mConnection!!.inputStream
                inputStreamReader = InputStreamReader(inputStream,
                    CHARSET
                )
                reader = BufferedReader(inputStreamReader)
                while (reader.readLine().also { tempLine = it } != null) {
                    resultBuffer.append(tempLine)
                }
            }
            responseMessage = resultBuffer.toString()
        } catch (e: SocketTimeoutException) {
            responseCode = HttpURLConnection.HTTP_CLIENT_TIMEOUT
            responseMessage = e.message ?: ""
        } catch (e: IOException) {
            responseCode = FILE_IO_EXCEPTION_CODE
            responseMessage = e.message ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            responseMessage = e.message ?: ""
        } finally {
            close(mConnection, reader, inputStreamReader, inputStream)
        }
        return getResponseData(responseCode, responseMessage)
    }

    private fun doGet(connection: HttpURLConnection) {
        connection.apply {
            requestMethod = "GET"
            connect()
        }
    }

    private fun doPost(
        connection: HttpURLConnection
    ) {
        connection.apply {
            doOutput = true
            useCaches = false
            requestMethod = "POST"
            for (key in builder.header.keys) {
                setRequestProperty(key, builder.header[key])
            }
            connect()
        }
        // POST请求
        val outputStream = DataOutputStream(mConnection!!.outputStream)
        val obj = JSONObject()
        // パラメータ設定
        if (builder.body?.params != null){
            for (key in builder.body?.params!!.keys) {
                    obj.put(key, builder.body?.params!![key])
            }
        }

        outputStream.writeBytes(obj.toString())
        outputStream.flush()
        outputStream.close()
    }


    private fun close(
        connection: HttpURLConnection?,
        reader: BufferedReader?,
        inputStreamReader: InputStreamReader?,
        inputStream: InputStream?
    ) {
        try {
            connection?.disconnect()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            reader?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            inputStreamReader?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * ResponseData
     *
     * @param code
     * @param message
     * @return
     */
    private fun getResponseData(code: Int, message: String): ResponseData {
        return ResponseData(code, message,null)
    }

}