package repository

import api.ResponseCallBack
import api.model.EventInfo
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
}