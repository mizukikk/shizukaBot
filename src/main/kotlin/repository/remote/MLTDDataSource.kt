package repository.remote

import api.ApiCallBack
import api.ResponseCallBack
import api.RetrofitProvider
import api.model.EventBorder
import api.model.EventInfo
import api.model.EventLog
import api.parameter.GetAnivIdolEventLogsParameter
import api.parameter.GetEventLogsParameter
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
        call.enqueue(object : ApiCallBack<List<EventInfo>>() {
            override fun success(response: List<EventInfo>) {
                callBack.success(response)
            }

            override fun fail() {
                callBack.fail()
            }
        })
    }

    override fun getEventBorders(id: Int, callBack: ResponseCallBack<EventBorder>) {
        val call = mltdService.getEventBorders(id)
        call.enqueue(object : ApiCallBack<EventBorder>() {
            override fun success(response: EventBorder) {
                callBack.success(response)
            }

            override fun fail() {
                callBack.fail()
            }
        })
    }

    override fun getEventLogs(para: GetEventLogsParameter, callBack: ResponseCallBack<List<EventLog>>) {
        val call = mltdService.getEventLogs(para.id, para.type.name, para.ranks, para.timeMin)
        call.enqueue(object : ApiCallBack<List<EventLog>>() {
            override fun success(response: List<EventLog>) {
                callBack.success(response)
            }

            override fun fail() {
                callBack.fail()
            }
        })
    }

    override fun getAnivIdolEventLogs(para: GetAnivIdolEventLogsParameter, callBack: ResponseCallBack<List<EventLog>>) {
        val call = mltdService.getAnivIdolEventLogs(para.id, para.idolId, para.timeMin)
        call.enqueue(object : ApiCallBack<List<EventLog>>() {
            override fun success(response: List<EventLog>) {
                callBack.success(response)
            }

            override fun fail() {
                callBack.fail()
            }
        })
    }
}