package api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class ApiCallBack<T> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            val body = response.body()
            success(body!!)
        } else {
            fail()
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        fail()
    }

    abstract fun success(response: T)
    abstract fun fail()
}