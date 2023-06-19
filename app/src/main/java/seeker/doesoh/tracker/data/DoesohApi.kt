package seeker.doesoh.tracker.data

import retrofit2.http.*
import seeker.doesoh.tracker.data.remote.*

interface DoeSohApi {

    @FormUrlEncoded
    @POST("/api/session")
    suspend fun addSession(@Field("email") email: String,@Field("password") password: String): User

    @Headers( "Content-Type: application/json" )
    @PUT("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long,@Body user: User)

    @GET("/api/devices")
    suspend fun getDevices(): List<Device>

    @POST("/api/commands/send")
    suspend fun sendCommand(@Body command: Command): Command

    @Headers("Accept: application/json")
    @GET("/api/reports/route")
    suspend fun getRoute(
        @Query("deviceId") deviceIds: List<Long>,
        @Query("groupId") groupIds: List<Long>,
        @Query("from") from: String,
        @Query("to") to: String): List<Position>

    @Headers("Accept: application/json")
    @GET("/api/reports/trips")
    suspend fun getTrip(
        @Query("deviceId") deviceIds: List<Long>,
        @Query("groupId") groupIds: List<Long>,
        @Query("from") from: String,
        @Query("to") to: String): List<TripReport>

    @Headers("Accept: application/json")
    @GET("/api/reports/stops")
    suspend fun getStop(
        @Query("deviceId") deviceIds: List<Long>,
        @Query("groupId") groupIds: List<Long>,
        @Query("from") from: String,
        @Query("to") to: String): List<StopReport>

    @Headers("Accept: application/json")
    @GET("/api/reports/events")
    suspend fun getEvent(
        @Query("deviceId") deviceIds: List<Long>,
        @Query("groupId") groupIds: List<Long>,
        @Query("from") from: String,
        @Query("to") to: String): List<EventReport>

    @Headers("Accept: application/json")
    @GET("/api/positions")
    suspend fun getPositions(
        @Query("id") id: List<Long>
    ): List<Position>

}