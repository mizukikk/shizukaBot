package api.service

import api.model.EventBorder
import api.model.EventInfo
import api.model.EventLog
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MLTDService {
    @GET("events")
    fun getEventInfoList(@Query("prettyPrint") prettyPrint: Boolean = false): Call<List<EventInfo>>

    @GET("events/{id}/rankings/borders")
    fun getEventBorders(@Path("id") id: Int, @Query("prettyPrint") prettyPrint: Boolean = false): Call<EventBorder>

    @GET("events/{id}/rankings/logs/{type}/{ranks}")
    fun getEventLogs(
        @Path("id") id: Int,
        @Path("type") type: String,
        @Path("ranks") ranks: String,
        @Query("timeMin") timeMin: String? = null,
        @Query("prettyPrint") prettyPrint: Boolean = false
    ): Call<List<EventLog>>

    @GET("events/{id}/rankings/logs/idolPoint/{idolId}/10,100")
    fun getAnivIdolEventLogs(
        @Path("id") id: Int,
        @Path("idolId") idolId: Int,
        @Query("timeMin") timeMin: String? = null,
        @Query("prettyPrint") prettyPrint: Boolean = false
    ): Call<List<EventLog>>
}