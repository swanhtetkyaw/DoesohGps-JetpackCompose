package seeker.doesoh.tracker.data.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import seeker.doesoh.tracker.data.DoeSohApi
import seeker.doesoh.tracker.data.remote.User
import seeker.doesoh.tracker.util.Resource
import java.io.IOException
import javax.inject.Inject

class LogInUseCase @Inject constructor() {
    operator fun invoke(email: String,password: String,doeSohApi: DoeSohApi): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
//            val doeSohApi = retrofit.newBuilder().baseUrl(baseUrl).build().create(
//                DoeSohApi::class.java)
//            //
            val user = doeSohApi.addSession(email,password)
            emit(Resource.Success(user))
        } catch (e: HttpException) {
//            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
            emit(Resource.Error("Wrong Username Or Password!"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }



}