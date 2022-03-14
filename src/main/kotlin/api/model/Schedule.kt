package api.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import extension.dateToMillis

data class Schedule(
    @Expose
    @SerializedName("beginDate")
    val beginDate: String,
    @Expose
    @SerializedName("boostBeginDate")
    val boostBeginDate: String?,
    @Expose
    @SerializedName("boostEndDate")
    val boostEndDate: String?,
    @Expose
    @SerializedName("endDate")
    val endDate: String,
    @Expose
    @SerializedName("pageBeginDate")
    val pageBeginDate: String,
    @Expose
    @SerializedName("pageEndDate")
    val pageEndDate: String
) {
    val endDateMillis get() = endDate.dateToMillis()
}