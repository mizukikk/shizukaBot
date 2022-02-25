package repository

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

}