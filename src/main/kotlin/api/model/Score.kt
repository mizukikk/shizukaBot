package api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Score(
    @Expose
    @SerializedName("score")
    val score: Double,
    @Expose
    @SerializedName("summaryTime")
    val summaryTime: String
)