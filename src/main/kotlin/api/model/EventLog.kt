package api.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class EventLog(
    @Expose
    @SerializedName("data")
    val `data`: List<Score>,
    @Expose
    @SerializedName("rank")
    val rank: Int
)
