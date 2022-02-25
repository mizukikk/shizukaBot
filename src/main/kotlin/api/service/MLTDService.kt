package api.service

import api.model.EventInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MLTDService {
    @GET("events")
    fun getEventInfoList(@Query("prettyPrint") prettyPrint: Boolean = false): Call<List<EventInfo>>
}