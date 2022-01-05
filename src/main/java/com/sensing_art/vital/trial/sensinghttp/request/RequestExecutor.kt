package com.sensing_art.vital.trial.sensinghttp.request

import com.sensing_art.vital.trial.sensinghttp.data.ResponseData

interface RequestExecutor {
    fun <T : ResponseData> executeAsync(httpCallBack: HttpCallBack<T>, uploadCallback:((current:Long, total:Long)->Unit)? = null)
}

inline fun <reified T : ResponseData> RequestExecutor.executeAsync(httpCallBack: HttpCallBack<T>, noinline uploadCallback:((current:Long, total:Long)->Unit)? = null){
    return executeAsync(httpCallBack,uploadCallback)
}

interface HttpCallBack<T>{
    fun onReceivedData(result:ResponseData)
}