package repository

import api.ResponseCallBack
import api.model.EventBorder
import api.model.EventInfo
import api.model.EventLog
import api.parameter.GetAnivIdolEventLogsParameter
import api.parameter.GetEventLogsParameter
import repository.remote.MLTDDataSource
import repository.remote.MLTDRemoteDataSource

class MLTDRepository(private val remoteDataSource: MLTDRemoteDataSource) : MLTDRemoteDataSource {
    companion object {
        private var instance: MLTDRepository? = null

        @Synchronized
        fun getInstance(): MLTDRepository {
            if (instance == null) {
                instance = MLTDRepository(
                    MLTDDataSource.getInstance()
                )
            }
            return instance!!
        }
    }

    override fun getEventInfoList(callBack: ResponseCallBack<List<EventInfo>>) {
        remoteDataSource.getEventInfoList(callBack)
    }

    override fun getEventBorders(id: Int, callBack: ResponseCallBack<EventBorder>) {
        remoteDataSource.getEventBorders(id, callBack)
    }

    override fun getEventLogs(para: GetEventLogsParameter, callBack: ResponseCallBack<List<EventLog>>) {
        remoteDataSource.getEventLogs(para, callBack)
    }

    override fun getAnivIdolEventLogs(para: GetAnivIdolEventLogsParameter, callBack: ResponseCallBack<List<EventLog>>) {
        remoteDataSource.getAnivIdolEventLogs(para, callBack)
    }
}