package api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider private constructor() {
    companion object {
        private var instance: RetrofitProvider? = null
        private const val BASE_URL = "https://api.matsurihi.me/mltd/v1/"

        @Synchronized
        fun getInstance(): RetrofitProvider {
            if (instance == null) {
                instance = RetrofitProvider()
            }
            return instance!!
        }
    }

    private val gson by lazy {
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    private val client by lazy {
        val interceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> createService(clazz: Class<T>) = retrofit.create(clazz)
}