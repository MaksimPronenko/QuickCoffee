package home.samples.quickcoffee.api

import home.samples.quickcoffee.models.CafeData
import home.samples.quickcoffee.models.RegistrationData
import home.samples.quickcoffee.models.RegistrationResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface CoffeeApi {

    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json;charset=utf-8"
    )
    @POST("/auth/register")
    suspend fun register(@Body registrationData: RegistrationData): RegistrationResult

    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json;charset=utf-8"
    )
    @POST("/auth/login")
    suspend fun login(@Body registrationData: RegistrationData): RegistrationResult

    @GET("/locations")
    suspend fun getLocations(@Header("Authorization") bearerToken: String): List<CafeData>?
//    suspend fun getLocations(@Header("Bearer Token") token: String): List<CafeData>?

//    @GET("/v3/d144777c-a67f-4e35-867a-cacc3b827473")
//    suspend fun getHotel(): Hotel?
//
//    @GET("/v3/8b532701-709e-4194-a41c-1a903af00195")
//    suspend fun getRoomsList(): RoomsList?
//
//    @GET("/v3/63866c74-d593-432c-af8e-f279d1a8d2ff")
//    suspend fun getBookingData(): BookingData?
}

val retrofit: CoffeeApi = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl("http://147.78.66.203:3210")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(CoffeeApi::class.java)