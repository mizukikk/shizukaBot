package repository.remote

import api.ApiCallBack
import api.ResponseCallBack
import api.RetrofitProvider
import api.model.EventInfo
import api.service.MLTDService

class MLTDDataSource : MLTDRemoteDataSource {
    companion object {
        private var instance: MLTDDataSource? = null

        @Synchronized
        fun getInstance(): MLTDDataSource {
            if (instance == null) {
                instance = MLTDDataSource()
            }
            return instance!!
        }
    }

    private val mltdService = RetrofitProvider
        .getInstance()
        .createService(MLTDService::class.java)

    override fun getEventInfoList(callBack: ResponseCallBack<List<EventInfo>>) {
        val call = mltdService.getEventInfoList()
        call.enqueue(object :ApiCallBack<List<EventInfo>>(){
            override fun success(response: List<EventInfo>) {
                callBack.success(response)
            }

            override fun fail() {
                callBack.fail()
            }
        })
    }
}