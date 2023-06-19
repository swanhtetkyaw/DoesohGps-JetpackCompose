package seeker.doesoh.tracker.data.use_case

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import seeker.doesoh.tracker.data.DoeSohApi
import seeker.doesoh.tracker.data.remote.Position
import seeker.doesoh.tracker.util.Resource
import java.io.IOException

class ReportRouteUseCase {
    operator fun invoke(doeSohApi: DoeSohApi,deviceId: Long,from: String,to: String ): Flow<Resource<List<Position>>> = flow {

        try {
            emit(Resource.Loading())
            coroutineScope {
                val route = async {doeSohApi.getRoute(deviceIds = listOf(deviceId), groupIds = emptyList(),from = from,to = to)}
                emit(Resource.Success(route.await()))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

}