package repository.remote

import api.ResponseCallBack
import api.model.EventInfo

interface MLTDRemoteDataSource {
    fun getEventInfoList(callBack:ResponseCallBack<List<EventInfo>>)
}