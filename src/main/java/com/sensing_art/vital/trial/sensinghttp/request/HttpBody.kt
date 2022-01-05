package com.sensing_art.vital.trial.sensinghttp.request

import java.io.File


class HttpBody() {
    internal var file: Map<String,File>? = null
    internal var params : Map<String,String>? = null


    constructor(params:Map<String,String>?=null,file: Map<String,File>?=null):this() {
        this.params = params
        this.file = file
    }
}


