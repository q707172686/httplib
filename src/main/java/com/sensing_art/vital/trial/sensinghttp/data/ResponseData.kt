package com.sensing_art.vital.trial.sensinghttp.data



open class ResponseData(open var code:Int, open var data:String, open var messageResponse:MessageResponse?){
    override fun toString(): String {
        return "ResponseData(code=$code, data='$data')"
    }
}