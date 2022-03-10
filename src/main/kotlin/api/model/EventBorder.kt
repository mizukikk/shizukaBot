package api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class EventBorder(
    @Expose
    @SerializedName("eventPoint")
    val eventPoint: List<Int>?,
    @Expose
    @SerializedName("highScore")
    val highScore: List<Int>?,
    @Expose
    @SerializedName("loungePoint")
    val loungePoint: List<Int>?
) {
    val ranks
        get() = try {
            eventPoint!!.joinToString(",")
        } catch (e: NullPointerException) {
            "100,2500,5000,10000,25000,50000"
        }
}