package com.sensing_art.vital.trial.sensinghttp.engine


import com.sensing_art.vital.trial.sensinghttp.HttpRequest
import com.sensing_art.vital.trial.sensinghttp.data.ResponseData


interface IHttpEngine  {
    fun initConfig(builder: HttpRequest.Builder)
    fun execute(uploadCallback:((current:Long,total:Long)->Unit)? = null): ResponseData
}