package repository.remote

import api.RetrofitProvider
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
}