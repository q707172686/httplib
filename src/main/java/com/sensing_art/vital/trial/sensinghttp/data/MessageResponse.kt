package com.sensing_art.vital.trial.sensinghttp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse (
    val status: Int,
    val message: String,
    val datas: Array<Datas>?,
    @SerialName("message_during")
    val messageDuring: Array<String>?,
    @SerialName("message_after")
    val messageAfter: Array<MessageAfter>?
   // , override var code: Int, override var data: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageResponse

        if (status != other.status) return false
        if (message != other.message) return false
        if (datas != null) {
            if (other.datas == null) return false
            if (!datas.contentEquals(other.datas)) return false
        } else if (other.datas != null) return false
        if (messageDuring != null) {
            if (other.messageDuring == null) return false
            if (!messageDuring.contentEquals(other.messageDuring)) return false
        } else if (other.messageDuring != null) return false
        if (messageAfter != null) {
            if (other.messageAfter == null) return false
            if (!messageAfter.contentEquals(other.messageAfter)) return false
        } else if (other.messageAfter != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status
        result = 31 * result + message.hashCode()
        result = 31 * result + (datas?.contentHashCode() ?: 0)
        result = 31 * result + (messageDuring?.contentHashCode() ?: 0)
        result = 31 * result + (messageAfter?.contentHashCode() ?: 0)
        return result
    }
}

@Serializable
data class Datas(
    @SerialName("company_id")
     val companyId: String,
    @SerialName("format_id")
    val formatId: Int,
    @SerialName("company_name")
    val companyName: String,
    @SerialName("message_flag")
     val messageFlag: Boolean,
    @SerialName("measured_time")
     val measuredTime: Int,
    @SerialName("message_start_time")
     val messageStartTime: Int,
    @SerialName("is_bootable")
    val isBootable: Boolean
)

@Serializable
data class MessageAfter(
    @SerialName("message")
    val message: String,
    @SerialName("lf_threshold")
    val threshold: Double
)
