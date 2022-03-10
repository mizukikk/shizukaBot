package repository.remote

import api.ResponseCallBack
import api.model.EventBorder
import api.model.EventInfo
import api.model.EventLog
import api.parameter.GetAnivIdolEventLogsParameter
import api.parameter.GetEventLogsParameter

interface MLTDRemoteDataSource {
    fun getEventInfoList(callBack: ResponseCallBack<List<EventInfo>>)
    fun getEventBorders(id: Int, callBack: ResponseCallBack<EventBorder>)
    fun getEventLogs(
        para: GetEventLogsParameter,
        callBack: ResponseCallBack<List<EventLog>>
    )

    fun getAnivIdolEventLogs(
        para: GetAnivIdolEventLogsParameter,
        callBack: ResponseCallBack<List<EventLog>>
    )

}